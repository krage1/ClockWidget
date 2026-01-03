package ru.krage.clockwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.krage.clockwidget.databinding.FragmentUserBinding;
import ru.krage.clockwidget.databinding.SpinnerRowBinding;


public class UserFragment extends Fragment implements IConstants, SeekBar.OnSeekBarChangeListener, 
            RadioGroup.OnCheckedChangeListener {
    
    //^ Enumerate editing objectives
    private enum EditTarget {
        SOLID_BACKGROUND,   //* Background: Solid Color
        GRADIENT_START,     //* Background: Gradient Start
        GRADIENT_CENTER,    //* Background: Gradient Midpoint
        GRADIENT_END,       //* Background: End of Gradient
        SOLID_STROKE,       //* Stroke: Solid Mode
        GRADIENT_STROKE     //* Stroke: Gradient Mode
    }

    private FragmentUserBinding binding;
    private SharedViewModel viewModel;
    private LocalPrefs prefs;
    private GradientDrawable gradientDrawable;
    private Bitmap imageBitmap;
    private String[] orients;
    private TypedArray arrows;
    
    // Store colors in memory, do not reset them when switching tabs
    private int color = 0, startColor = 0, endColor = 0, centerColor = 0;
    private int solidStrokeColor = 0, gradStrokeColor = 0;
    
    // Sizes
    private int solidCornerRadius = 0, gradCornerRadius = 0;
    private int solidStrokeWidth = 0, gradStrokeWidth = 0;
    
    // Temporal ARGB values for sliders
    private int alpha = 0, red = 0, green = 0, blue = 0;
    
    private boolean userSelect = false;
    private int widgetID;
    
    //* Current Edit Target (Solid Background by Default)
    private EditTarget currentTarget = EditTarget.SOLID_BACKGROUND;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing Components
        arrows = getResources().obtainTypedArray(R.array.arrow_ids);
        orients = getResources().getStringArray(R.array.orients);
        prefs = new LocalPrefs(requireActivity());
        widgetID = prefs.getWidgetID();
        
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        binding.ivSpaceView.setBackground(gradientDrawable);
        
        //% Initializing the ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        
        setupSpinner();
        initSeekBars();
        initListeners();
        initData(); // Load data once at startup
    }
    
    private void setupSpinner(){
        CustomAdapter adapter = new CustomAdapter(requireContext(), R.layout.spinner_row, orients);
        adapter.setDropDownViewResource(R.layout.spinner_row);
        binding.spSpinner.setAdapter(adapter);
    }
    
    private void initSeekBars(){
        //@  SeekBar handlers
        binding.sbRed.setOnSeekBarChangeListener(this);
        binding.sbGreen.setOnSeekBarChangeListener(this);
        binding.sbBlue.setOnSeekBarChangeListener(this);
        binding.sbAlpha.setOnSeekBarChangeListener(this);
        binding.sbStrokeWidth.setOnSeekBarChangeListener(this);
        binding.sbCornerRadius.setOnSeekBarChangeListener(this);
        //@  Switch handlers
        binding.rgGradientSolid.setOnCheckedChangeListener(this);
        binding.rgStartCenterEnd.setOnCheckedChangeListener(this);
        binding.rgStrokeToggle.setOnCheckedChangeListener(this);
    }
    
    //@ A single method for loading data. Read Prefs only here.
    private void initData() {
        color = prefs.getDataColors(KEY_COLOR, widgetID, Color.DKGRAY);
        startColor = prefs.getDataColors(KEY_STARTCOLOR, widgetID, Color.GRAY);
        endColor = prefs.getDataColors(KEY_ENDCOLOR, widgetID, Color.BLACK);
        centerColor = prefs.getDataColors(KEY_CENTERCOLOR, widgetID, 0);
        
        solidStrokeColor = prefs.getDataColors(KEY_SOLID_STROKE_COLOR, widgetID, Color.BLUE);
        gradStrokeColor = prefs.getDataColors(KEY_GRADIENT_STROKE_COLOR, widgetID, Color.YELLOW);
        
        solidStrokeWidth = prefs.getDataColors(KEY_SOLID_STROKE_WIDTH, widgetID, 10);
        gradStrokeWidth = prefs.getDataColors(KEY_GRADIENT_STROKE_WIDTH, widgetID, 8);
        
        solidCornerRadius = prefs.getDataColors(KEY_SOLID_CORNER_RADIUS, widgetID, 30);
        gradCornerRadius = prefs.getDataColors(KEY_GRADIENT_CORNER_RADIUS, widgetID, 20);
        
        // Restoring the UI
        restoreRadioState(binding.rgGradientSolid, KEY_SOLID_GRADIENT_ID);
        restoreRadioState(binding.rgStartCenterEnd, KEY_START_END_ID);
        restoreRadioState(binding.rgStrokeToggle, KEY_TOGGLE_STROKE_ID);
        
        // Spinner Recovery
        int index = prefs.getData(KEY_STATE_SPINNER, widgetID, -1);
        if (index != -1) {
            binding.spSpinner.setSelection(index);
            gradientDrawable.setOrientation(GradientDrawable.Orientation.valueOf(orients[index]));
        } else {
            gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
        }
        
        // Initializing the display logic
        updateEditTarget();
        updateUiControls(); // Locking/unlocking the buttons
        updateProgressFromModel(); // Setting seekbars
        applyDrawableChanges();
    }
    
    private void restoreRadioState(RadioGroup group, String key) {
        int id = prefs.getData(key, widgetID, -1);
        if (id != -1) group.check(id);
    }
    
    private void initListeners(){
        
        binding.btnAcceptUser.setOnClickListener(v -> saveAndExit());
        
        //@  Spinner Listener
        binding.spSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) userSelect = true;
            if (event.getAction() == MotionEvent.ACTION_UP) v.performClick();
            return false;
        });
        binding.spSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (userSelect) {
                    gradientDrawable.setOrientation(GradientDrawable.Orientation.valueOf(orients[position]));
                    prefs.setData(KEY_STATE_SPINNER, position, widgetID);
                    userSelect = false;
                }
                int index = prefs.getData(KEY_STATE_SPINNER, widgetID, -1);
                if (index != -1 && binding.spSpinner.getSelectedItemPosition() != index)
                    binding.spSpinner.setSelection(index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    //@ The saving logic is moved to a separate method
    private void saveAndExit() {
        binding.btnAcceptUser.setEnabled(false);
        
        if (imageBitmap != null && !imageBitmap.isRecycled()) {
            imageBitmap.recycle();
        }
        // Create a copy of the Drawable state for rendering so that it does not depend on the UI thread
        Drawable drawableClone = Objects.requireNonNull(gradientDrawable.getConstantState()).newDrawable().mutate();
        
        returnSizeBounds();
        
        executor.execute(() -> {
            // Render to a fixed-size bitmap for the widget
            imageBitmap = createBitmap(drawableClone);
            
            // Save the file
            boolean isSaved = saveBitmapToFile(requireContext(), imageBitmap);
            
            requireActivity().runOnUiThread(() -> {
                if (isSaved) {
                    // Signaling success
                    viewModel.select(KEY_USER_BACKGROUND, 20);
                    requireActivity().finish();
                } else {
                    binding.btnAcceptUser.setEnabled(true);
                    Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    
    private void updateEditTarget() {
        if (binding.rbStroke.isChecked()) {
            currentTarget = binding.rbSolid.isChecked() ? EditTarget.SOLID_STROKE : EditTarget.GRADIENT_STROKE;
        } else {
            if (binding.rbSolid.isChecked()) {
                currentTarget = EditTarget.SOLID_BACKGROUND;
            } else {
                if (binding.rbStartColor.isChecked()) currentTarget = EditTarget.GRADIENT_START;
                else if (binding.rbCenterColor.isChecked()) currentTarget = EditTarget.GRADIENT_CENTER;
                else currentTarget = EditTarget.GRADIENT_END;
            }
        }
    }
    
    private void updateUiControls() {
        boolean isStroke = binding.rbStroke.isChecked();
        boolean isGradient = binding.rbGradient.isChecked();
        
        // If the stroke is selected, you cannot change the background type (Solid/Gradient) - this is logical,
        // But in our code, the buttons were disabled. Let's leave this logic.
        binding.rbSolid.setEnabled(!isStroke);
        binding.rbGradient.setEnabled(!isStroke);
        
        // The gradient color selection buttons are only available in gradient mode and NOT in stroke mode
        boolean enableGradColors = isGradient && !isStroke;
        binding.rbStartColor.setEnabled(enableGradColors);
        binding.rbCenterColor.setEnabled(enableGradColors);
        binding.rbEndColor.setEnabled(enableGradColors);
    }
    
    //@ SeekBar Progress Listener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser) return;
        
        if (seekBar == binding.sbRed) red = progress;
        else if (seekBar == binding.sbGreen) green = progress;
        else if (seekBar == binding.sbBlue) blue = progress;
        else if (seekBar == binding.sbAlpha) alpha = progress;
        else if (seekBar == binding.sbStrokeWidth) {
            if (binding.rbSolid.isChecked()) solidStrokeWidth = progress;
            else gradStrokeWidth = progress;
            binding.tvStrokeValue.setText(String.valueOf(progress));
        } else if (seekBar == binding.sbCornerRadius) {
            if (binding.rbSolid.isChecked()) solidCornerRadius = progress;
            else gradCornerRadius = progress;
            binding.tvCornerValue.setText(String.valueOf(progress));
        }
        
        // Collecting the color and updating the model in memory
        int newColor = Color.argb(alpha, red, green, blue);
        updateColorTexts(newColor);
        
        switch (currentTarget) {
            case SOLID_BACKGROUND -> color = newColor;
            case GRADIENT_START -> startColor = newColor;
            case GRADIENT_CENTER -> centerColor = newColor;
            case GRADIENT_END -> endColor = newColor;
            case SOLID_STROKE -> solidStrokeColor = newColor;
            case GRADIENT_STROKE -> gradStrokeColor = newColor;
        }
        applyDrawableChanges();
    }
    
    //@
    private void applyDrawableChanges() {
        gradientDrawable.mutate();
        if (binding.rbSolid.isChecked()) {
            // For Solid, use a single color (or an array of identical colors if setColors requires an array)
            gradientDrawable.setColors(new int[]{color, color});
            gradientDrawable.setStroke(solidStrokeWidth, solidStrokeColor);
            gradientDrawable.setCornerRadius(solidCornerRadius);
        } else {
            // Smart logic for gradient: if the center is transparent (0), use 2 colors
            if (centerColor == 0) {
                gradientDrawable.setColors(new int[]{startColor, endColor});
            } else {
                gradientDrawable.setColors(new int[]{startColor, centerColor, endColor});
            }
            gradientDrawable.setStroke(gradStrokeWidth, gradStrokeColor);
            gradientDrawable.setCornerRadius(gradCornerRadius);
        }
        binding.ivSpaceView.invalidate();
    }
    
    private void updateColorTexts(int c) {
        binding.tvRedValue.setText(String.valueOf(Color.red(c)));
        binding.tvGreenValue.setText(String.valueOf(Color.green(c)));
        binding.tvBlueValue.setText(String.valueOf(Color.blue(c)));
        binding.tvAlphaValue.setText(String.valueOf(Color.alpha(c)));
        
        // Color visualization on the Alpha slider
        binding.sbAlpha.setProgressTintMode(PorterDuff.Mode.SRC_IN);
        binding.sbAlpha.setProgressTintList(ColorStateList.valueOf(c));
        
        binding.tvHexValue.setText(String.format("#%08X", c));
    }
    
    //@  Radio Button State Change Listener
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // Save the tab selection
        if (group == binding.rgGradientSolid) prefs.setData(KEY_SOLID_GRADIENT_ID, checkedId, widgetID);
        else if (group == binding.rgStartCenterEnd) prefs.setData(KEY_START_END_ID, checkedId, widgetID);
        else if (group == binding.rgStrokeToggle) prefs.setData(KEY_TOGGLE_STROKE_ID, checkedId, widgetID);
        
        updateEditTarget();
        updateUiControls();
        updateProgressFromModel(); // Loading values from variables into sliders
        applyDrawableChanges();
    }
    
    // Updating sliders when changing modes
    private void updateProgressFromModel() {
        
        int c = getCurrentTargetColor();
        
        alpha = Color.alpha(c);
        red = Color.red(c);
        green = Color.green(c);
        blue = Color.blue(c);
        
        binding.sbAlpha.setProgress(alpha);
        binding.sbRed.setProgress(red);
        binding.sbGreen.setProgress(green);
        binding.sbBlue.setProgress(blue);
        
        updateColorTexts(c);
        
        if (binding.rbSolid.isChecked()) {
            binding.sbCornerRadius.setProgress(solidCornerRadius);
            binding.sbStrokeWidth.setProgress(solidStrokeWidth);
            binding.tvCornerValue.setText(String.valueOf(solidCornerRadius));
            binding.tvStrokeValue.setText(String.valueOf(solidStrokeWidth));
        } else {
            binding.sbCornerRadius.setProgress(gradCornerRadius);
            binding.sbStrokeWidth.setProgress(gradStrokeWidth);
            binding.tvCornerValue.setText(String.valueOf(gradCornerRadius));
            binding.tvStrokeValue.setText(String.valueOf(gradStrokeWidth));
        }
    }
    
    private int getCurrentTargetColor() {
        return switch (currentTarget) {
            case SOLID_BACKGROUND -> color;
            case GRADIENT_START -> startColor;
            case GRADIENT_CENTER -> centerColor;
            case GRADIENT_END -> endColor;
            case SOLID_STROKE -> solidStrokeColor;
            case GRADIENT_STROKE -> gradStrokeColor;
        };
    }
    
    //@  Saving data after changing slider progress
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Save to Prefs only when the user has released the finger
        if (seekBar == binding.sbCornerRadius) {
            if (binding.rbSolid.isChecked()) prefs.setDataColors(KEY_SOLID_CORNER_RADIUS, solidCornerRadius, widgetID);
            else prefs.setDataColors(KEY_GRADIENT_CORNER_RADIUS, gradCornerRadius, widgetID);
        } else if (seekBar == binding.sbStrokeWidth) {
            if (binding.rbSolid.isChecked()) prefs.setDataColors(KEY_SOLID_STROKE_WIDTH, solidStrokeWidth, widgetID);
            else prefs.setDataColors(KEY_GRADIENT_STROKE_WIDTH, gradStrokeWidth, widgetID);
        } else {
            // Color Preservation
            saveCurrentColorToPrefs();
        }
    }
    
    private void saveCurrentColorToPrefs() {
        int c = getCurrentTargetColor();
        String key = switch (currentTarget) {
            case SOLID_BACKGROUND -> KEY_COLOR;
            case GRADIENT_START -> KEY_STARTCOLOR;
            case GRADIENT_CENTER -> KEY_CENTERCOLOR;
            case GRADIENT_END -> KEY_ENDCOLOR;
            case SOLID_STROKE -> KEY_SOLID_STROKE_COLOR;
            case GRADIENT_STROKE -> KEY_GRADIENT_STROKE_COLOR;
        };
        prefs.setDataColors(key, c, widgetID);
    }
    
    //@  Creating a Bitmap from a Gradient
    private Bitmap createBitmap(Drawable drawable){
        //* Bitmap size in pixels
        int pxWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 224,
                        getResources().getDisplayMetrics());
        int pxHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 144,
                        getResources().getDisplayMetrics());
        
        //* Creating a bitmap
        Bitmap bitmap = Bitmap.createBitmap(pxWidth, pxHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    //@ Writing a bitmap to a file
    private boolean saveBitmapToFile(Context context, Bitmap bitmap) {
        //* Create a file in the internal memory of the application
        File file = new File(context.getCacheDir(), "imgBitmap" + widgetID + ".webp");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            Bitmap.CompressFormat format = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                    ? Bitmap.CompressFormat.WEBP_LOSSLESS
                    : Bitmap.CompressFormat.PNG;
            bitmap.compress(format, 100, fos);
            fos.flush();
            return true;
        } catch (IOException e) {
            Log.d("UserFragment", "Error saving bitmap: " + e.getMessage());
            return false;
        }
    }
    //@ Returning the Bitmap Dimensions
    private void returnSizeBounds() {
        int width = binding.ivSpaceView.getWidth();
        int height = binding.ivSpaceView.getHeight();
        gradientDrawable.setBounds(0, 0, width, height);
    }
    //@ Creating an adapter
    public class CustomAdapter extends ArrayAdapter<String> {
        private final LayoutInflater inflater;
        private final int activeColor; // Caching the color
        
        public CustomAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
            // Get the color once when creating the adapter
            activeColor = ContextCompat.getColor(context, R.color.blue_dark);
        }
        @Override
        public View getDropDownView(int position, @Nullable View convertView,
                                    @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                SpinnerRowBinding spinnerRowBinding = SpinnerRowBinding.inflate(inflater, parent, false);
                holder = new ViewHolder(spinnerRowBinding);
                convertView = spinnerRowBinding.getRoot();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.spinnerRowBinding.tvOrientation.setText(orients[position]);
            holder.spinnerRowBinding.tvOrientation.setTextColor(activeColor);
            holder.spinnerRowBinding.ivOrientation.setImageResource(arrows.getResourceId(position, 0));

            return convertView;
        }
        
        private record ViewHolder(SpinnerRowBinding spinnerRowBinding) {
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (imageBitmap != null && !imageBitmap.isRecycled()) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
        if (arrows != null) {
            arrows.recycle();
            arrows = null;
        }
        binding = null;
        executor.shutdownNow();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
}
