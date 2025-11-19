package com.example.bytelift;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bytelift.databinding.ActivityWelcomeBinding;
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

import com.example.bytelift.models.UserProfile;

public class WelcomeActivity extends AppCompatActivity{

    private ActivityWelcomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(this, "Google sign in failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Google signup button
        binding.btnGoogleSignup.setOnClickListener (v -> signUpWithGoogle());

        // email signup buton
        binding.btnEmailSignup.setOnClickListener (v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // login text link
        binding.tvLogin.setOnClickListener (v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void signUpWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle (String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // check if user profile exist
                            checkAndCreateUserProfile(user);
                        }
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndCreateUserProfile(FirebaseUser user) {
        String userId = user.getUid();

        // Check if user profile already exist
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        // NEW user - create Profile
                        String displayName = user.getDisplayName() != null ? user.getDisplayName() : "";
                        String[] nameParts = displayName.split(" ", 2);
                        String firstName = nameParts[0];
                        String lastName = nameParts.length > 1 ? nameParts[1] : "";
                        String username = user.getEmail().split("@")[0];

                        UserProfile userProfile = new UserProfile(userId, username, firstName, lastName, user.getEmail());

                        db.collection("users").document(userId)
                                .set(userProfile)
                                .addOnSuccessListener(aVoid -> {
                                    navigateToMainActivity();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(WelcomeActivity.this, "Failed to save profile :" + e.getMessage(), Toast.LENGTH_SHORT).show();;
                    });
                }else {
                        navigateToMainActivity();
                    }
    })
                .addOnFailureListener(e -> {
                    Toast.makeText(WelcomeActivity.this, "Error checking profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void navigateToMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // check if user already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }
    }
}

