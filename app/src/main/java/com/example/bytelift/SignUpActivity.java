package com.example.bytelift;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bytelift.databinding.ActivitySignUpBinding;
import com.example.bytelift.models.UserProfile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Google Sign In launcher
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnSignUp.setOnClickListener(v -> signUpWithEmail());
        binding.btnGoogleSignUp.setOnClickListener(v -> signUpWithGoogle());
        binding.tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void signUpWithEmail() {
        String username = binding.etUsername.getText().toString().trim();
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (!validateInput(username, firstName, lastName, email, password, confirmPassword)) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSignUp.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save user profile to Firestore
                            saveUserProfile(user.getUid(), username, firstName, lastName, email);
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSignUp.setEnabled(true);
                        Toast.makeText(SignUpActivity.this, "Sign up failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUpWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        binding.progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // For Google Sign-In, use display name to split into first/last name
                            String displayName = user.getDisplayName() != null ? user.getDisplayName() : "User";
                            String[] nameParts = displayName.split(" ", 2);
                            String firstName = nameParts[0];
                            String lastName = nameParts.length > 1 ? nameParts[1] : "";
                            String username = user.getEmail().split("@")[0]; // Use email prefix as username

                            saveUserProfile(user.getUid(), username, firstName, lastName, user.getEmail());
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserProfile(String userId, String username, String firstName, String lastName, String email) {
        UserProfile userProfile = new UserProfile(userId, username, firstName, lastName, email);

        db.collection("users").document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSignUp.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                    navigateToOnboarding();  // ← CORRECT! Goes to Onboarding
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSignUp.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInput(String username, String firstName, String lastName, String email, String password, String confirmPassword) {
        // Username validation
        if (username.isEmpty()) {
            binding.etUsername.setError("Username is required");
            binding.etUsername.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            binding.etUsername.setError("Username must be at least 3 characters");
            binding.etUsername.requestFocus();
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            binding.etUsername.setError("Username can only contain letters, numbers, and underscores");
            binding.etUsername.requestFocus();
            return false;
        }

        // First name validation
        if (firstName.isEmpty()) {
            binding.etFirstName.setError("First name is required");
            binding.etFirstName.requestFocus();
            return false;
        }

        if (firstName.length() < 2) {
            binding.etFirstName.setError("First name must be at least 2 characters");
            binding.etFirstName.requestFocus();
            return false;
        }

        // Last name validation
        if (lastName.isEmpty()) {
            binding.etLastName.setError("Last name is required");
            binding.etLastName.requestFocus();
            return false;
        }

        if (lastName.length() < 2) {
            binding.etLastName.setError("Last name must be at least 2 characters");
            binding.etLastName.requestFocus();
            return false;
        }

        // Email validation
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            binding.etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Please enter a valid email");
            binding.etEmail.requestFocus();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            binding.etPassword.requestFocus();
            return false;
        }

        // Confirm password validation
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Please confirm your password");
            binding.etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            binding.etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToOnboarding() {
        Intent intent = new Intent(SignUpActivity.this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // No auto-navigation for sign up page
    }
}