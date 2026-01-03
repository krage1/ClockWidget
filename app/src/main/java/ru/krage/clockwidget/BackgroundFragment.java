package ru.krage.clockwidget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.divider.MaterialDividerItemDecoration;

import java.util.Collections;
import java.util.List;

import ru.krage.clockwidget.databinding.BackgroundRowBinding;
import ru.krage.clockwidget.databinding.FragmentBackgroundBinding;


public class BackgroundFragment extends Fragment implements IConstants{

    private CustomAdapter adapter;
    private SharedViewModel sharedViewModel;
    private BackgroundViewModel viewModel;
    private FragmentBackgroundBinding binding;
    // Cache the animation so that it doesn't load with every click.
    // This prevents unnecessary object creation and reduces CPU load.
    private Animation scaleAnimation;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load the animation once when creating a View fragment.
        scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.row_scale);
        binding = FragmentBackgroundBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //% Initializing the ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(BackgroundViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.initBkg(requireContext());

        setupRecyclerView(); // Configuring RecyclerView

        //$ observe the change in the selected
        viewModel.getSelectedBkgIndex().observe(getViewLifecycleOwner(), index -> {
            if (adapter != null && index != null) {
                adapter.updateSelection(index);
            }
        });

        //@  Accept button listener
        binding.btnAcceptBackground.setOnClickListener(v -> {
            Integer selectedIndex = viewModel.getSelectedBkgIndex().getValue();
            if (selectedIndex != null) {
                sharedViewModel.select(KEY_INDEX_BACKGROUND, selectedIndex);
            }
            if (isAdded() && getActivity() != null) {
                requireActivity().finish();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CustomAdapter(viewModel.getBackgrounds(), viewModel.getIcons(), viewModel.getColors(),
                (v, position) -> { // Passing Lambda for Click Processing
                    // Use a cached animation instance.
                    v.startAnimation(scaleAnimation);
                    viewModel.selectBackground(requireContext(), position);
                }
        );

        // Set stable IDs to improve RecyclerView performance.
        // This helps RecyclerView better track elements and optimize animations.
        adapter.setHasStableIds(true);

        //% Setting our custom style for the separator
        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(),
                LinearLayoutManager.VERTICAL);
        divider.setDividerColorResource(requireContext(), android.R.color.transparent);
        divider.setDividerThickness(20);
        binding.recyclerView.addItemDecoration(divider);

        //* LayoutManager
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Increase the size of the pool of reusable Views.
        // This can improve scrolling performance by storing more
        // ViewHolders in memory for quick reuse.
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        viewPool.setMaxRecycledViews(0, 10); // Тип view = 0, количество = 10
        binding.recyclerView.setRecycledViewPool(viewPool);

        //% Disabling the standard animation
        RecyclerView.ItemAnimator animator = binding.recyclerView.getItemAnimator();
        // Check that it is DefaultItemAnimator (it usually is)
        if (animator instanceof SimpleItemAnimator) {
            // Disable the change animation (which causes the "flashing")
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        //* Install the adapter
        binding.recyclerView.setAdapter(adapter);
    }

    //@ Адаптер для RecyclerView
    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        // Use an empty list as a payload,
        // to signal that you need to update only the selection state (checkbox).
        private static final List<Object> PAYLOAD_CHECK_CHANGED = Collections.emptyList();

        private final List<String> backgrounds;
        private final List<Integer> icons;
        private final List<Integer> colors;
        private final OnItemClickListener clickListener;
        private int selectedIndex = -1; // Store the selected index inside the adapter

        public CustomAdapter(List<String> backgrounds, List<Integer> icons, List<Integer> colors,
                             OnItemClickListener clickListener) {

            this.backgrounds = backgrounds;
            this.icons = icons;
            this.colors = colors;
            this.clickListener = clickListener;
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        //% Creating a method to update the data
        public void updateSelection(int newSelectedIndex) {
            if (newSelectedIndex == this.selectedIndex) {
                return; // Nothing has changed
            }

            int oldSelectedIndex = this.selectedIndex;
            this.selectedIndex = newSelectedIndex;

            // Using notifyItemChanged with a payload,
            // to update only part of the View, rather than redrawing the entire View.
            // This is significantly more efficient than a full ViewHolder rebinding.
            if (oldSelectedIndex != -1) {
                notifyItemChanged(oldSelectedIndex, PAYLOAD_CHECK_CHANGED);
            }
            if (newSelectedIndex != -1) {
                notifyItemChanged(newSelectedIndex, PAYLOAD_CHECK_CHANGED);
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            BackgroundRowBinding backgroundBinding = BackgroundRowBinding.inflate(inflater, parent, false);
            return new ViewHolder(backgroundBinding, clickListener);
        }

        //@ Implement the onBindViewHolder with the payload.
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                // If the payload is empty, perform a full data binding.
                super.onBindViewHolder(holder, position, payloads);
            } else {
                // If there is a payload, we update only the selection state (checkbox).
                // This prevents unnecessary calls to setImageResource, setText, and so on.
                boolean isSelected = (position == selectedIndex);
                holder.bindSelectionState(isSelected);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Metric for measurement: logging full binding.
            boolean isSelected = (position == selectedIndex);
            holder.bind(backgrounds.get(position), icons.get(position), colors.get(position), isSelected);

        }

        //@ Override getItemId to use stable IDs.
        @Override
        public long getItemId(int position) {
            // Use the string hash code as a unique ID if it is stable.
            // If not, it's better to use a position.
            return position;
        }

        @Override
        public int getItemCount() {
            return backgrounds.size();
        }

        //@ Classic ViewHolder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final BackgroundRowBinding backgroundBinding;

            public ViewHolder(BackgroundRowBinding backgroundBinding, OnItemClickListener listener) {
                super(backgroundBinding.getRoot());
                this.backgroundBinding = backgroundBinding;
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, position);
                        }
                    }
                });
            }

            //@ Method for full data binding
            public void bind(String background, int imageRes, int colorRes, boolean isSelected) {
                backgroundBinding.ivBackground.setImageResource(imageRes);
                backgroundBinding.tvBackground.setText(background);
                int actualColor = itemView.getContext().getColor(colorRes);
                backgroundBinding.tvBackground.setTextColor(actualColor);

                bindSelectionState(isSelected);
            }

            //@ A separate method to update only the selection state.
            public void bindSelectionState(boolean isSelected) {
                if (isSelected) {
                    backgroundBinding.ivCheckBackground.setImageResource(R.drawable.check_on);
                } else {
                    backgroundBinding.ivCheckBackground.setImageResource(R.drawable.check_off);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null); // Cleaning the adapter to prevent leaks
        binding = null;
        scaleAnimation = null; // Clearing the animation link
    }
}
