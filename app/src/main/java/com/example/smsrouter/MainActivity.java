package com.example.smsrouter;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smsrouter.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String[] SMS_PERMISSIONS = {
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
    };

    private ActivityMainBinding binding;
    private SharedPreferences mSharedPreferences;

    private final ActivityResultLauncher<String[]> mSmsPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> arePermissionsGranted) {

                    boolean receiveSmsPermissionGranted = Boolean.TRUE.equals(arePermissionsGranted.get(
                            Manifest.permission.RECEIVE_SMS));
                    boolean sendSmsPermissionGranted = Boolean.TRUE.equals(arePermissionsGranted.get(
                            Manifest.permission.SEND_SMS));
                    if (receiveSmsPermissionGranted && sendSmsPermissionGranted) {
                        return;
                    }

                    Snackbar.make(findViewById(android.R.id.content),
                                    getString(R.string.error_sms_perms_not_granted),
                                    Snackbar.LENGTH_INDEFINITE
                            )
                            .setAction(android.R.string.ok, v -> {

                                boolean userHitDontAskAgain = !shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)
                                        || !shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS);
                                if (userHitDontAskAgain) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(intent);
                                } else {
                                    mSmsPermissionLauncher.launch(SMS_PERMISSIONS);
                                }
                            })
                            .show();
                }
            });

    private final ActivityResultLauncher<Intent> mContactPhoneNumberLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() != RESULT_OK) {
                        return;
                    }

                    final Uri contactUri = result.getData().getData();
                    try (Cursor cursor = getContentResolver().query(contactUri, new String[]{
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    }, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            String contactNumber = cursor.getString(0);
                            if (binding.tfFrom.hasFocus()) {
                                binding.tfFrom.getEditText().setText(contactNumber);
                            } else if (binding.tfTo.hasFocus()) {
                                binding.tfTo.getEditText().setText(contactNumber);
                            }
                        }
                    }
                }
            });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_app_bar, menu);

        // Set switch state
        final SwitchCompat sw = menu.findItem(R.id.menu_item_1).getActionView().findViewById(R.id.switch_app_bar);
        sw.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                scheduleDummyJob();
            } else {
                cancelDummyJob();
            }

            mSharedPreferences.edit()
                    .putBoolean(getString(R.string.prefs_key_app_enabled), isChecked)
                    .apply();
        });
        sw.setChecked(mSharedPreferences.getBoolean(getString(R.string.prefs_key_app_enabled), false));

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(getString(R.string.prefs_key_sender), binding.tfFrom.getEditText().getText().toString());
        outState.putString(getString(R.string.prefs_key_receiver), binding.tfTo.getEditText().getText().toString());
        outState.putString(getString(R.string.prefs_key_pattern), binding.tfPattern.getEditText().getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mSharedPreferences = getSharedPreferences(getString(R.string.prefs_file_path), MODE_PRIVATE);

        // Set text of text fields
        if (savedInstanceState == null) {
            binding.tfFrom.getEditText().setText(mSharedPreferences.getString(
                    getString(R.string.prefs_key_sender), "")
            );
            binding.tfTo.getEditText().setText(mSharedPreferences.getString(
                    getString(R.string.prefs_key_receiver), "")
            );
            binding.tfPattern.getEditText().setText(mSharedPreferences.getString(
                    getString(R.string.prefs_key_pattern), "")
            );
        } else {
            binding.tfFrom.getEditText().setText(savedInstanceState.getString(getString(R.string.prefs_key_sender)));
            binding.tfTo.getEditText().setText(savedInstanceState.getString(getString(R.string.prefs_key_receiver)));
            binding.tfPattern.getEditText().setText(savedInstanceState.getString(getString(R.string.prefs_key_pattern)));
        }

        // Show contact picker when start icons are clicked
        binding.tfFrom.setStartIconOnClickListener(v -> {
            binding.tfFrom.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    binding.tfFrom.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

            Intent intent = new Intent(Intent.ACTION_PICK)
                    .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                mContactPhoneNumberLauncher.launch(intent);
            }
        });
        binding.tfTo.setStartIconOnClickListener(v -> {
            binding.tfTo.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    binding.tfTo.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

            Intent intent = new Intent(Intent.ACTION_PICK)
                    .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                mContactPhoneNumberLauncher.launch(intent);
            }
        });

        // Save sender, receiver and message when save button is clicked
        binding.btnSave.setOnClickListener(this::btnSave_OnClick);

        // Format text field
        binding.tfTo.getEditText().addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Clear error text when text field is typed in
        binding.tfFrom.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.tfFrom.isErrorEnabled()) {
                    binding.tfFrom.setError(null);
                }
            }
        });
        binding.tfTo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.tfTo.isErrorEnabled()) {
                    binding.tfTo.setError(null);
                }
            }
        });
        binding.tfPattern.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.tfPattern.isErrorEnabled()) {
                    binding.tfPattern.setError(null);
                }
            }
        });
    }

    private void btnSave_OnClick(View btn) {

        String from = binding.tfFrom.getEditText().getText().toString().trim();
        String to = binding.tfTo.getEditText().getText().toString().trim();
        String pattern = binding.tfPattern.getEditText().getText().toString().trim();

        mSmsPermissionLauncher.launch(SMS_PERMISSIONS);

        if (from.isEmpty()) {
            binding.tfFrom.setError(getString(R.string.error_text_empty));
            return;
        }

        if (to.isEmpty()) {
            binding.tfTo.setError(getString(R.string.error_text_empty));
            return;
        }

        if (pattern.isEmpty()) {
            binding.tfPattern.setError(getString(R.string.error_text_empty));
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            boolean isSuccessful = mSharedPreferences.edit()
                    .putString(getString(R.string.prefs_key_sender), from)
                    .putString(getString(R.string.prefs_key_receiver), to)
                    .putString(getString(R.string.prefs_key_pattern), pattern)
                    .commit();

            runOnUiThread(() -> {
                if (isSuccessful) {
                    Toast.makeText(this, getString(R.string.toast_save_successful), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.toast_save_failed), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Schedules a dummy job one day apart to keep app alive. If already scheduled, replaces previous
     * job with the new one.
     */
    private void scheduleDummyJob() {

        long oneDayInterval = 1000 * 60 * 60 * 24;
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(
                new JobInfo.Builder(DummyJobService.DUMMY_JOB_ID, new ComponentName(this, DummyJobService.class))
                        .setPersisted(true)
                        .setPeriodic(oneDayInterval)
                        .build()
        );
    }

    /**
     * Cancels dummy job. If not scheduled, does nothing.
     */
    private void cancelDummyJob() {

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(DummyJobService.DUMMY_JOB_ID);
    }
}