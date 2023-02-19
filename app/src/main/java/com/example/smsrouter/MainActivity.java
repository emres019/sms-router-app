package com.example.smsrouter;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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

    @SuppressWarnings("unused")
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
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
                public void onActivityResult(Map<String, Boolean> areGranted) {
                    if (Boolean.FALSE.equals(areGranted.get(Manifest.permission.RECEIVE_SMS)) ||
                            Boolean.FALSE.equals(areGranted.get(Manifest.permission.SEND_SMS))) {
                        // TODO: Change snack bar with something else
                        Snackbar.make(findViewById(android.R.id.content),
                                        getString(R.string.error_sms_perms_not_granted),
                                        Snackbar.LENGTH_INDEFINITE)
                                .setAction(android.R.string.ok, v ->
                                        mSmsPermissionLauncher.launch(SMS_PERMISSIONS))
                                .show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> mContactPhoneNumberLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        final Uri contactUri = result.getData().getData();
                        try (Cursor cursor = getContentResolver().query(contactUri, new String[]{
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        }, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                String number = cursor.getString(0);
                                if (binding.tfFrom.hasFocus()) {
                                    binding.tfFrom.getEditText().setText(number);
                                } else if (binding.tfTo.hasFocus()) {
                                    binding.tfTo.getEditText().setText(number);
                                }
                            }
                        }
                    }
                }
            });

    @Override
    protected void onStart() {
        super.onStart();

        // Request necessary permissions
        mSmsPermissionLauncher.launch(SMS_PERMISSIONS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);

        // Set switch state
        final SwitchCompat sw = menu.findItem(R.id.menu_item_1).getActionView().findViewById(R.id.switch_app_bar);
        sw.setChecked(mSharedPreferences.getBoolean(getString(R.string.saved_enabled_key), false));

        // Set switch listener
        sw.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // Update shared preferences
            mSharedPreferences.edit()
                    .putBoolean(getString(R.string.saved_enabled_key), isChecked)
                    .apply();
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(getString(R.string.saved_sender_key), binding.tfFrom.getEditText().getText().toString());
        outState.putString(getString(R.string.saved_receiver_key), binding.tfTo.getEditText().getText().toString());
        outState.putString(getString(R.string.saved_pattern_key), binding.tfPattern.getEditText().getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mSharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);

        // Set text of text fields
        if (savedInstanceState == null) {
            binding.tfFrom.getEditText().setText(mSharedPreferences.getString(
                    getString(R.string.saved_sender_key), "")
            );
            binding.tfTo.getEditText().setText(mSharedPreferences.getString(
                    getString(R.string.saved_receiver_key), "")
            );
            binding.tfPattern.getEditText().setText(mSharedPreferences.getString(
                    getString(R.string.saved_pattern_key), "")
            );
        } else {
            binding.tfFrom.getEditText().setText(savedInstanceState.getString(getString(R.string.saved_sender_key)));
            binding.tfTo.getEditText().setText(savedInstanceState.getString(getString(R.string.saved_receiver_key)));
            binding.tfPattern.getEditText().setText(savedInstanceState.getString(getString(R.string.saved_pattern_key)));
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.tfFrom.isErrorEnabled()) {
                    binding.tfFrom.setError(null);
                }
            }
        });
        binding.tfTo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.tfTo.isErrorEnabled()) {
                    binding.tfTo.setError(null);
                }
            }
        });
        binding.tfPattern.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.tfPattern.isErrorEnabled()) {
                    binding.tfPattern.setError(null);
                }
            }
        });
    }

    /**
     * Saves sender, receiver and pattern to shared preferences.
     *
     * @param btn Save button
     */
    private void btnSave_OnClick(View btn) {
        String from = binding.tfFrom.getEditText().getText().toString().trim();
        String to = binding.tfTo.getEditText().getText().toString().trim();
        String pattern = binding.tfPattern.getEditText().getText().toString().trim();

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

        // Save data to shared preferences
        Executors.newSingleThreadExecutor().execute(() -> {
            mSharedPreferences.edit()
                    .putString(getString(R.string.saved_sender_key), from)
                    .putString(getString(R.string.saved_receiver_key), to)
                    .putString(getString(R.string.saved_pattern_key), pattern)
                    .commit();

            runOnUiThread(()-> Toast.makeText(MainActivity.this,
                    getString(R.string.toast_save_text), Toast.LENGTH_SHORT).show());
        });
    }
}