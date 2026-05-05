package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {

    private Button btnLogin, btnSignup1, btnLoginWithGoogle;
    private TextInputEditText inputEmail, inputPassword;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private final int RC_SIGN_IN = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(login.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupGoogleSignIn();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup1 = findViewById(R.id.btnSignup1);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnSignup1.setOnClickListener(v -> startActivity(new Intent(login.this, signup.class)));

        btnLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void attemptLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // تسجيل الدخول عبر Firebase فقط (تجنب قاعدة البيانات المحلية مؤقتاً)
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(login.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, t -> {
                    if (t.isSuccessful()) {
                        startActivity(new Intent(login.this, MainActivity.class));
                        finish();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
