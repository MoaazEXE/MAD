package com.example.bytelift. ui.settings_page;

import android. Manifest;
import android.content. Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android. net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text. Editable;
import android. text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx. appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.bytelift.BaseActivity;
import com.example. bytelift.R;
import com.example.bytelift.data.settings.SettingsRepository;
import com.example.bytelift.databinding.ActivityEditAccountBinding;
import com.example.bytelift.models.UserProfile;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java. io.IOException;

public class EditAccountActivity extends BaseActivity {
    private ActivityEditAccountBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private UserProfile originalProfile;
    private Uri selectedImageUri;
    private boolean hasUnsavedChanges = false;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsRepository settingsRepo = SettingsRepository.getInstance(this);

        super.onCreate(savedInstanceState);
        binding = ActivityEditAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActionBar();
        initializeFirebase();
        setupActivityResultLaunchers();
        loadUserProfile();
        setupTextWatchers();
        setupClickListeners();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Account");
            getSupportActionBar().setElevation(0);
        }
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void setupActivityResultLaunchers() {
        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            loadImageIntoView(selectedImageUri);
                            hasUnsavedChanges = true;
                            updateSaveButtonState();
                        }
                    }
                }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result. getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                binding.ivProfilePicture.setImageBitmap(imageBitmap);
                                selectedImageUri = getImageUriFromBitmap(imageBitmap);
                                hasUnsavedChanges = true;
                                updateSaveButtonState();
                            }
                        }
                    }
                }
        );

        // Permission launcher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        showSnackbar("Camera permission is required to take photos");
                    }
                }
        );
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            showSnackbar("User not authenticated");
            finish();
            return;
        }

        showLoading(true);

        firestore.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    showLoading(false);
                    if (document.exists()) {
                        originalProfile = document.toObject(UserProfile.class);
                        if (originalProfile != null) {
                            populateFields();
                        } else {
                            showError("Failed to load profile data");
                        }
                    } else {
                        showError("Profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Failed to load profile: " + e. getMessage());
                });
    }

    private void populateFields() {
        // Populate text fields
        binding.etUsername.setText(originalProfile.getUsername());
        binding.etFirstName.setText(originalProfile.getFirstName());
        binding.etLastName.setText(originalProfile. getLastName());
        binding.etEmail.setText(originalProfile. getEmail());

        // Load profile picture if exists
        if (originalProfile.getProfilePicUrl() != null &&
                !originalProfile.getProfilePicUrl().isEmpty()) {
            Glide.with(this)
                    .load(originalProfile.getProfilePicUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(binding.ivProfilePicture);
        }

        // Show account creation date
        if (originalProfile.getCreatedAt() != null) {
            String joinDate = "Member since " +
                    android.text.format.DateFormat. format("MMM yyyy",
                            originalProfile.getCreatedAt().toDate());
            binding.tvJoinDate.setText(joinDate);
            binding.tvJoinDate.setVisibility(View.VISIBLE);
        }
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkForChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etUsername.addTextChangedListener(textWatcher);
        binding.etFirstName.addTextChangedListener(textWatcher);
        binding.etLastName.addTextChangedListener(textWatcher);
    }

    private void checkForChanges() {
        if (originalProfile == null) return;

        String currentUsername = binding.etUsername.getText().toString().trim();
        String currentFirstName = binding.etFirstName.getText().toString().trim();
        String currentLastName = binding.etLastName.getText().toString().trim();

        hasUnsavedChanges = ! currentUsername.equals(originalProfile. getUsername()) ||
                !currentFirstName.equals(originalProfile.getFirstName()) ||
                !currentLastName.equals(originalProfile.getLastName()) ||
                selectedImageUri != null;

        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        binding.btnSave.setEnabled(hasUnsavedChanges);
        binding.btnSave.setAlpha(hasUnsavedChanges ? 1.0f : 0.5f);
    }

    private void setupClickListeners() {
        binding.btnChangeProfilePicture.setOnClickListener(v -> showImageSourceDialog());
        binding.ivProfilePicture.setOnClickListener(v -> showImageSourceDialog());
        binding.btnSave.setOnClickListener(v -> saveChanges());
        binding.btnCancel.setOnClickListener(v -> handleBackPress());
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Remove Photo"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Profile Picture")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Take Photo
                            checkCameraPermissionAndOpen();
                            break;
                        case 1: // Choose from Gallery
                            openGallery();
                            break;
                        case 2: // Remove Photo
                            removeProfilePicture();
                            break;
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission. CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA);
            }
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent. resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            showSnackbar("No camera app found");
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        imagePickerLauncher.launch(galleryIntent);
    }

    private void removeProfilePicture() {
        binding.ivProfilePicture.setImageResource(R.drawable.ic_profile_placeholder);
        selectedImageUri = Uri.parse("REMOVE");
        hasUnsavedChanges = true;
        updateSaveButtonState();
    }

    private void loadImageIntoView(Uri imageUri) {
        try {
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(binding.ivProfilePicture);
        } catch (Exception e) {
            showSnackbar("Failed to load image");
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap. CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(), bitmap, "ProfilePic", null);
        return Uri.parse(path);
    }

    private void saveChanges() {
        if (! validateInputs()) {
            return;
        }

        String username = binding.etUsername.getText().toString().trim();
        String firstName = binding.etFirstName. getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            showError("User not authenticated");
            return;
        }

        showLoading(true);
        disableInputs();

        // If profile picture changed, upload it first
        if (selectedImageUri != null) {
            if (selectedImageUri.toString().equals("REMOVE")) {
                // Remove profile picture
                updateProfileData(currentUser. getUid(), username, firstName, lastName, "");
            } else {
                // Upload new profile picture
                uploadProfilePicture(currentUser. getUid(), username, firstName, lastName);
            }
        } else {
            // No picture change, just update data
            String existingPicUrl = originalProfile.getProfilePicUrl() != null ?
                    originalProfile.getProfilePicUrl() : "";
            updateProfileData(currentUser.getUid(), username, firstName, lastName, existingPicUrl);
        }
    }

    private void uploadProfilePicture(String userId, String username,
                                      String firstName, String lastName) {
        StorageReference profilePicRef = storage.getReference()
                .child("profile_pictures")
                .child(userId + ". jpg");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), selectedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap. compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] data = baos.toByteArray();

            profilePicRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        profilePicRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    updateProfileData(userId, username, firstName,
                                            lastName, uri.toString());
                                })
                                .addOnFailureListener(e -> {
                                    showLoading(false);
                                    enableInputs();
                                    showError("Failed to get image URL: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        showLoading(false);
                        enableInputs();
                        showError("Failed to upload image: " + e.getMessage());
                    });
        } catch (IOException e) {
            showLoading(false);
            enableInputs();
            showError("Failed to process image: " + e.getMessage());
        }
    }

    private void updateProfileData(String userId, String username,
                                   String firstName, String lastName,
                                   String profilePicUrl) {
        firestore.collection("users").document(userId)
                .update(
                        "username", username,
                        "firstName", firstName,
                        "lastName", lastName,
                        "profilePicUrl", profilePicUrl
                )
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    enableInputs();
                    hasUnsavedChanges = false;
                    showSuccessAndFinish();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    enableInputs();
                    showError("Failed to update profile: " + e.getMessage());
                });
    }

    private boolean validateInputs() {
        String username = binding.etUsername.getText().toString().trim();
        String firstName = binding.etFirstName. getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();

        if (username.isEmpty()) {
            binding.tilUsername.setError("Username is required");
            binding.etUsername.requestFocus();
            return false;
        }
        if (username.length() < 3) {
            binding.tilUsername.setError("Username must be at least 3 characters");
            binding. etUsername.requestFocus();
            return false;
        }
        if (! username.matches("^[a-zA-Z0-9_]+$")) {
            binding. tilUsername.setError("Username can only contain letters, numbers, and underscores");
            binding.etUsername.requestFocus();
            return false;
        }
        binding.tilUsername.setError(null);

        if (firstName.isEmpty()) {
            binding.tilFirstName.setError("First name is required");
            binding.etFirstName.requestFocus();
            return false;
        }
        binding.tilFirstName.setError(null);

        if (lastName.isEmpty()) {
            binding.tilLastName.setError("Last name is required");
            binding.etLastName.requestFocus();
            return false;
        }
        binding.tilLastName.setError(null);

        return true;
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.scrollView.setAlpha(show ? 0.5f : 1.0f);
    }

    private void disableInputs() {
        binding.etUsername. setEnabled(false);
        binding.etFirstName.setEnabled(false);
        binding.etLastName.setEnabled(false);
        binding.btnChangeProfilePicture.setEnabled(false);
        binding.btnSave.setEnabled(false);
        binding.btnCancel. setEnabled(false);
    }

    private void enableInputs() {
        binding.etUsername.setEnabled(true);
        binding.etFirstName. setEnabled(true);
        binding.etLastName.setEnabled(true);
        binding.btnChangeProfilePicture.setEnabled(true);
        binding.btnSave.setEnabled(true);
        binding.btnCancel.setEnabled(true);
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable. ic_dialog_alert)
                .show();
    }

    private void showSuccessAndFinish() {
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void handleBackPress() {
        if (hasUnsavedChanges) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Discard Changes? ")
                    .setMessage("You have unsaved changes. Are you sure you want to leave?")
                    .setPositiveButton("Discard", (dialog, which) -> finish())
                    .setNegativeButton("Keep Editing", null)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackPress();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBackPress();
    }
}