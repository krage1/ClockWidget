package ru.krage.clockwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import ru.krage.clockwidget.databinding.ActivityConfigBinding;


public class ConfigActivity extends AppCompatActivity implements IConstants,View.OnClickListener {

    private ActivityConfigBinding binding;
    private ConfigViewModel viewModel;
    private Intent resultValue;
    private Animation animationIn;

    private final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new
            ActivityResultContracts.StartActivityForResult(), this::handleActivityResult);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //* The name of the ActivityConfigBinding class is automatically generated from the name
        //* Layout XML file (activity_config.xml)
        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        //* Set the root View from the ViewBinding as the Activity content
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ConfigViewModel.class);

        int widgetID = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_CANCELED, resultValue);
        viewModel.setWidgetID(widgetID);

        viewModel.detectSystemLanguage();

        animationIn = AnimationUtils.loadAnimation(this, R.anim.alpha_config);
        binding.constraintLayout.startAnimation(animationIn);

        //$  Initializing handlers
        binding.btnLayout.setOnClickListener(this);
        binding.btnBackground.setOnClickListener(this);
        binding.btnUser.setOnClickListener(this);
        binding.btnLang.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

        observeViewModel();

        boolean enable = viewModel.updateAddButtonText();
        if (enable) binding.btnAdd.setText(R.string.update);
        
        // Checking if you need to show the dialog
        checkBatteryOptimization();
    }
    
    private void checkBatteryOptimization() {
        // We use our Helper only to check the status (boolean)
        boolean isOptimized = !BatteryOptimizationHelper.isIgnoringBatteryOptimizations(this);
        
        if (isOptimized) {
            // Check if the dialog is already open (so as not to multiply copies when rotated)
            if (getSupportFragmentManager().findFragmentByTag(BatteryDialogFragment.TAG) == null) {
                
                BatteryDialogFragment dialog = new BatteryDialogFragment();
                // show() method takes a FragmentManager and a string tag
                dialog.show(getSupportFragmentManager(), BatteryDialogFragment.TAG);
            }
        }
    }
    
    private void observeViewModel() {
    //* Monitoring the status of the "Add/Update" button
        viewModel.isAddButtonEnabled().observe(this, enabled ->
                binding.btnAdd.setEnabled(enabled));
    //* Monitoring the status of the information icon of the selected widget language
    //* and widget localization
        viewModel.getLanguageIndex().observe(this, index -> {
            if (index != null && index >= 0) {
                binding.tvInfoLang.setText(viewModel.getLangsArray()[index]);
                binding.ivInfoFlag.setImageResource(viewModel.getFlagResId(index));
                setAppLocale(viewModel.getLangsArray()[index]);
            }
        });
    }
    //* Processing of Results Obtained from Fragments
    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Intent intent = result.getData();
            int layout = intent.getIntExtra(KEY_INDEX_LAYOUT, -1);
            if (layout != -1) viewModel.setLayoutIndex(layout);

            int background = intent.getIntExtra(KEY_INDEX_BACKGROUND, -1);
            if (background != -1) viewModel.setBackgroundIndex(background);

            int user = intent.getIntExtra(KEY_USER_BACKGROUND, 0);
            if (user != 0) viewModel.setUserBackground(user);

            int lang = intent.getIntExtra(KEY_INDEX_LANGUAGE, -1);
            if (lang != -1) {
                viewModel.setLanguageIndex(lang);
                setAppLocale(viewModel.getLangsArray()[lang]);
                recreate();
            }
        }
    }

    //@  Installing the application localization
    private void setAppLocale(String locale) {
        LocaleListCompat list = LocaleListCompat.forLanguageTags(locale);
        AppCompatDelegate.setApplicationLocales(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //@ Hiding the ActionBar in the UI
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //@ Hiding the StatusBar in the UI interface
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Objects.requireNonNull(getWindow().getDecorView().getWindowInsetsController()).
                    hide(WindowInsetsCompat.Type.statusBars());
        }
        //@  Starting an animation
        binding.constraintLayout.startAnimation(animationIn);
    }
    //@  Button handlers
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_layout) {
            startSubActivity(VALUE_LAYOUT);
        } else if (id == R.id.btn_background) {
            startSubActivity(VALUE_BACKGROUND);
        } else if (id == R.id.btn_lang) {
            startSubActivity(VALUE_LANG);
        } else if (id == R.id.btn_add) {
            handleButtonAdd();
        } else if (id == R.id.btn_user) {
            startSubActivity(VALUE_USER);
        }
    }
    //* Launching ActivityForResult
    private void startSubActivity(int value) {
        Intent intent = new Intent(getApplicationContext(), ContainerActivity.class);
        intent.putExtra(KEY_INDEX, value);
        mStartForResult.launch(intent);
    }
    //^ Add/Update button handler
    private void handleButtonAdd() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        ClockWidget.updateBackground(getApplicationContext(), appWidgetManager, viewModel.getWidgetId());
        ClockWidget.startUpdate(getApplicationContext());
        setResult(RESULT_OK, resultValue);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}