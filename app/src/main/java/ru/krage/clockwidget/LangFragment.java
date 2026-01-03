package ru.krage.clockwidget;


import android.content.Context;
import android.content.res.Configuration;
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
import java.util.Locale;
import java.util.Objects;

import ru.krage.clockwidget.databinding.FragmentLangBinding;
import ru.krage.clockwidget.databinding.LangRowBinding;


public class LangFragment extends Fragment implements IConstants {

    private CustomAdapter adapter;
    private LangViewModel viewModel;
    private FragmentLangBinding binding;
    private SharedViewModel sharedViewModel;
    private int previousIndex = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //* Initialize the binding class
        binding = FragmentLangBinding.inflate(inflater, container, false);
        //* Returning the root view from binding
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //% Initializing the ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(LangViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.initLang(requireContext());

        setupRecyclerView();

        //* Observe the selected index
        viewModel.getSelectedLangIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null) {
                // Update the list more efficiently
                if (previousIndex != -1) {
                    adapter.notifyItemChanged(previousIndex); // Removing the old tick
                }
                adapter.notifyItemChanged(index); // Putting a new tick
                previousIndex = index;

                updateButtonText(index); // Updating the localization of the button and header
            }
        });

        //@  Accept button listener
        binding.btnAcceptLang.setOnClickListener(v -> {
            Integer index = viewModel.getSelectedLangIndex().getValue();
            if (index != null) {
                sharedViewModel.select(KEY_INDEX_LANGUAGE, index);
            }
            if (isAdded() && getActivity() != null) {
                requireActivity().finish();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new CustomAdapter(viewModel.getLanguages(), viewModel.getFlagIds(),position -> {
                // Click animation
            View clickedView = Objects.requireNonNull(binding.rvRecyclerView.findViewHolderForAdapterPosition(position)).itemView;
                Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.row_scale);
                clickedView.startAnimation(animation);
                // Updating the ViewModel
                viewModel.selectLanguage(requireContext(), position);
            }
        );

        //% --- Logic for adding a separator ---
        // 1. Create an instance MaterialDividerItemDecoration
        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(),
                LinearLayoutManager.VERTICAL // Specify the orientation of the list
        );

        // 2. Setting our custom style for the separator
        divider.setDividerColorResource(requireContext(), android.R.color.transparent); // Color Setting
        divider.setDividerThickness(20); // Setting the Pixel Thickness (or Use Resources)

        // 3. Adding the created separator to RecyclerView
        binding.rvRecyclerView.addItemDecoration(divider);
        
        //* Installing the LayoutManager
        binding.rvRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Getting a standard animator
        RecyclerView.ItemAnimator animator = binding.rvRecyclerView.getItemAnimator();
        // Check that it is DefaultItemAnimator (it usually is)
        if (animator instanceof SimpleItemAnimator) {
            // Disable the change animation (which causes the "flashing")
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        //* Install the adapter
        binding.rvRecyclerView.setAdapter(adapter);

        //* Animating a list
        Animation animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.list_combo);
        binding.rvRecyclerView.startAnimation(animation);
    }
    // Instant localization of the button and title in the fragment
    private void updateButtonText(int index) {
        if (isAdded() && getActivity() != null) {
            String langCode = viewModel.getLangCode(index);
            if (langCode != null) {
                Locale newLocale = new Locale(langCode);
                Context context = requireContext();
                Configuration configuration = new Configuration(context.getResources().getConfiguration());
                configuration.setLocale(newLocale);
                Context localizedContext = context.createConfigurationContext(configuration);

                String buttonText = localizedContext.getResources().getString(R.string.text_button_apply);
                String titleText = localizedContext.getResources().getString(R.string.title_fragment_language);
                binding.tvLang.setText(titleText);
                binding.btnAcceptLang.setText(buttonText);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //@ 3. Adapter for RecyclerView
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private final List<String> langs;
        private final List<Integer> flags;
        private final OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public CustomAdapter(List<String> langs, List<Integer> flags, OnItemClickListener listener) {
            this.langs = langs;
            this.flags = flags;
            this.listener = listener;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LangRowBinding rowBinding = LangRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false);
            return new ViewHolder(rowBinding, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Integer selectedIndex = viewModel.getSelectedLangIndex().getValue();
            boolean isSelected = selectedIndex != null && selectedIndex == position;
            holder.bind(langs.get(position), flags.get(position), isSelected);
        }

        @Override
        public int getItemCount() {
            return langs.size();
        }

        //@ 4. Classic ViewHolder as a Static Nested Class
        public static class ViewHolder extends RecyclerView.ViewHolder {
            final LangRowBinding binding;

            public ViewHolder(LangRowBinding binding, OnItemClickListener listener) {
                super(binding.getRoot());
                this.binding = binding;
                itemView.setOnClickListener(v -> {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }

            void bind(String lang, int flag, boolean isSelected) {
                binding.tvLanguage.setText(lang);
                binding.ivFlag.setImageResource(flag);
                binding.ivCheckLang.setImageResource(isSelected ? R.drawable.check_yes : 0);
            }
        }
    }
}