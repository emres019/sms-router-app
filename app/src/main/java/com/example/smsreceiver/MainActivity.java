package com.example.smsreceiver;

import android.Manifest;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextInputLayout mTfFrom;
    private TextInputLayout mTfTo;
    private TextInputLayout mTfPattern;

    private final ActivityResultLauncher<String[]> mSmsPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> areGranted) {
                    if (Boolean.FALSE.equals(areGranted.get(Manifest.permission.RECEIVE_SMS)) ||
                            Boolean.FALSE.equals(areGranted.get(Manifest.permission.SEND_SMS))) {
                        Snackbar.make(findViewById(android.R.id.content),
                                getString(R.string.snack_bar_permission_text),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, v -> mSmsPermissionLauncher.launch(new String[] {
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.SEND_SMS
                                }))
                                .show();
                    }
                }
            });

    private final ActivityResultLauncher<Void> mContactPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.PickContact(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    // TODO: Handle the result uri and remove the log
                    Log.d(LOG_TAG, "onContactPickerLauncher: " + (result == null ? "null" : result));
                }
            });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_switch) {
            // TODO: Handle switch click action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign text fields
        mTfFrom = findViewById(R.id.tf_from);
        mTfTo = findViewById(R.id.tf_to);
        mTfPattern = findViewById(R.id.tf_pattern);

        // Request necessary permissions
        mSmsPermissionLauncher.launch(new String[] {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
        });

        // Show contact picker when start icons are clicked
        this.<TextInputLayout>findViewById(R.id.tf_from)
                .setStartIconOnClickListener(v -> mContactPickerLauncher.launch(null));
        this.<TextInputLayout>findViewById(R.id.tf_to)
                .setStartIconOnClickListener(v -> mContactPickerLauncher.launch(null));

        // Save sender, receiver and message when save button is clicked
        findViewById(R.id.btn_save).setOnClickListener(this::btnSaveOnClick);
    }

    private void btnSaveOnClick(View btn) {
        String from = mTfFrom.getEditText().getText().toString().trim();
        String to = mTfTo.getEditText().getText().toString().trim();
        String pattern = mTfPattern.getEditText().getText().toString().trim();

        if (from.isEmpty()) {
            mTfFrom.setError(getString(R.string.error_text_empty));
            return;
        }

        if (to.isEmpty()) {
            mTfTo.setError(getString(R.string.error_text_empty));
            return;
        }

        if (pattern.isEmpty()) {
            mTfPattern.setError(getString(R.string.error_text_empty));
            return;
        }

        // Save data to shared preferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.saved_from_key), from);
        editor.putString(getString(R.string.saved_to_key), to);
        editor.putString(getString(R.string.saved_pattern_key), pattern);

        editor.apply();
    }
}