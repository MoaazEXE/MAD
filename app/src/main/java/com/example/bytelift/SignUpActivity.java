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

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (!validateInput(email, password, confirmPassword)) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSignUp.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSignUp.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
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
                    binding.progressBar.setVisibility(View.GONE);
                    
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed: " + 
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
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
}
