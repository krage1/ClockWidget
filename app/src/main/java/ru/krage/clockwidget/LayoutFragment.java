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

import java.util.List;

import ru.krage.clockwidget.databinding.FragmentLayoutBinding;
import ru.krage.clockwidget.databinding.LayoutRowBinding;


public class LayoutFragment extends Fragment implements IConstants {

    private CustomAdapter adapter;
    private SharedViewModel sharedViewModel;
    private LayoutViewModel viewModel;
    private FragmentLayoutBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //* Initialize the binding class
        binding = FragmentLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //% Initializing the ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(LayoutViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.initStyle(requireContext());

        setupRecyclerView(); // Configuring RecyclerView

        // Watching the Style Index
        viewModel.getSelectedStyleIndex().observe(getViewLifecycleOwner(), index -> {
            if (adapter != null && index != null) {
                adapter.updateSelection(index);
            }
        });
        //@  Accept button listener
        binding.btnAcceptLayout.setOnClickListener(v -> {
            Integer selectedIndex = viewModel.getSelectedStyleIndex().getValue();
            if (selectedIndex != null) {
                sharedViewModel.select(KEY_INDEX_LAYOUT, selectedIndex);
            }
            if (isAdded() && getActivity() != null) {
                requireActivity().finish();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CustomAdapter(viewModel.getStyles(), viewModel.getStyleImages(),
                (v, position) -> { // Passing Lambda for Click Processing
                    Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.row_scale);
                    v.startAnimation(animation);
                    viewModel.selectStyle(requireContext(), position);
                }
        );

        //% Setting our custom style for the separator
        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(),
                LinearLayoutManager.VERTICAL);
        divider.setDividerColorResource(requireContext(), android.R.color.transparent);
        divider.setDividerThickness(20);
        binding.recyclerView.addItemDecoration(divider);

        //* LayoutManager
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

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

        private final List<String> styles;
        private final List<Integer> images;
        private final OnItemClickListener clickListener;
        private int selectedIndex = -1; // Store the selected index inside the adapter

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public CustomAdapter(List<String> styles, List<Integer> images, OnItemClickListener clickListener) {
            this.styles = styles;
            this.images = images;
            this.clickListener = clickListener;
        }

        //% Creating a method to update the data
        public void updateSelection(int newSelectedIndex) {
            if (newSelectedIndex == this.selectedIndex) {
                return; // Nothing has changed
            }

            int oldSelectedIndex = this.selectedIndex;
            this.selectedIndex = newSelectedIndex;

            // Notifying the adapter when only the necessary elements are changed
            if (oldSelectedIndex != -1) {
                // Removing the old tick
                notifyItemChanged(oldSelectedIndex);
            }
            if (newSelectedIndex != -1) {
                // Putting a new tick
                notifyItemChanged(newSelectedIndex);
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            LayoutRowBinding layoutBinding = LayoutRowBinding.inflate(inflater, parent, false);
            return new ViewHolder(layoutBinding, clickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // The bind logic is no longer directly dependent on the ViewModel
            boolean isSelected = (position == selectedIndex);
            holder.bind(styles.get(position), images.get(position), isSelected);
        }

        @Override
        public int getItemCount() {
            return styles.size();
        }

        //@ Classic ViewHolder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final LayoutRowBinding layoutBinding;

            public ViewHolder(LayoutRowBinding layoutBinding, OnItemClickListener listener) {
                super(layoutBinding.getRoot());
                this.layoutBinding = layoutBinding;
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v, position);
                        }
                    }
                });
            }

            public void bind(String style, int imageRes, boolean isSelected) {
                layoutBinding.ivStyle.setImageResource(imageRes);
                layoutBinding.tvLayout.setText(style);

                if (isSelected) {
                    layoutBinding.ivCheckLayout.setImageResource(R.drawable.check_on);
                } else {
                    layoutBinding.ivCheckLayout.setImageResource(R.drawable.check_off);
                }
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}