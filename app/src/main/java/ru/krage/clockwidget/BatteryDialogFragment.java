package ru.krage.clockwidget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BatteryDialogFragment extends DialogFragment {
    
    public static final String TAG = "BatteryDialogFragment";
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use requireActivity() or requireContext() to get the context
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        
        // Installing texts from resources (automatic localization)
        builder.setTitle(R.string.dialog_battery_title)
                .setMessage(R.string.dialog_battery_message)
                
                // Configure button
                .setPositiveButton(R.string.btn_configure, (dialog, which) ->
                        openBatterySettings())
                
                // Later button
                .setNegativeButton(R.string.btn_later, (dialog, which) ->
                        dialog.dismiss());
        
        return builder.create();
    }
    
    private void openBatterySettings() {
        try {
            @SuppressLint("BatteryLife")
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // If the direct intent did not work (vendor specifics)
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(getContext(), R.string.find_app, Toast.LENGTH_LONG).show();
        }
    }
}
