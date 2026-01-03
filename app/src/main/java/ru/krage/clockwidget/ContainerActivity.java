package ru.krage.clockwidget;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class ContainerActivity extends AppCompatActivity implements IConstants{
    
    // Using the Constructor for Inflate Layout
    public ContainerActivity(){super(R.layout.activity_container);}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //* 1.Setting the UI (Hiding bars) right at creation to avoid flashing
        hideSystemUI();
        
        //* 2. Initializing View and Animations
        ConstraintLayout container = findViewById(R.id.cl_container);
        
        // Loading animations
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_container);
        Animation alphaAnim = AnimationUtils.loadAnimation(this, R.anim.fragment_alpha);
        
        //* 3. Intent Processing and Fragment Selection
        Intent intent = getIntent();
        int indexButton = intent.getIntExtra(KEY_INDEX, -1);
        
        Fragment selectedFragment = null;
        Animation selectedAnimation = null;
        
        switch (indexButton){
            case VALUE_LAYOUT -> {
                selectedFragment = new LayoutFragment();
                selectedAnimation = scaleAnim;
            }
            case VALUE_BACKGROUND -> {
                selectedFragment = new BackgroundFragment();
                selectedAnimation = scaleAnim;
            }
            case VALUE_LANG -> {
                selectedFragment = new LangFragment();
                selectedAnimation = alphaAnim;
            }
            case VALUE_USER -> {
                selectedFragment = new UserFragment();
                selectedAnimation = scaleAnim;
            }
        }
        
        //* 4. Applying logic if the fragment is selected
        if (selectedFragment != null) {
            // Starting the container animation
            if (container != null && selectedAnimation != null) {
                container.startAnimation(selectedAnimation);
            }
            // Execute the transaction
            replaceFragment(selectedFragment);
        }
        
        // 5. Subscribing to the ViewModel to return the result
        setupViewModelObserver();
        
    }
    
    /**
     * A method for replacing a fragment.
     * Removes duplicate transaction code.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container_view, fragment);
        transaction.commit();
    }
    
    /**
     * Configure the ViewModel observer.
     */
    private void setupViewModelObserver() {
        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.getSelectedData().observe(this, pair -> {
            if (pair != null) {
                sendDataAndFinish(pair.first, pair.second);
            }
        });
    }
    
    /**
     * Submit data and complete an Activity.
     */
    private void sendDataAndFinish(String keyIndex, int index) {
        Intent data = new Intent();
        data.putExtra(keyIndex, index);
        setResult(RESULT_OK, data);
        finish();
    }
    
    /**
     * Hiding the system interface (Status Bar, ActionBar)
     * Using WindowCompat for backward compatibility.
     */
    private void hideSystemUI() {
        // Hiding the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        
        // Use WindowInsetsControllerCompat to manage system bars
        // This works reliably on all Android versions and does not require SDK_INT verification
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        // Hiding the status bar
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
    }
}