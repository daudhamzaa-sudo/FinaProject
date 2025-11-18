package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.google.android.material.textfield.TextInputEditText;

public class login extends AppCompatActivity {

 private Button btnLogin;
 private Button btnLoginWithFacebook;
 private Button btnLoginWithGoogle;
 private Button btnSignup1;
 private TextInputEditText inputEmail;
 private TextInputEditText inputPassword;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup1 = findViewById(R.id.btnSignup1);
        btnLoginWithFacebook = findViewById(R.id.btnLoginWithFacebook);
        // btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }


        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAndReadData()) {
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        // Validate and read data

    }
    private boolean validateAndReadData() {
        boolean isValid = true;
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty()) {
            inputEmail.setError("Email is required");
            isValid = false;
        }
        if (password.isEmpty() || password.length() < 5 || password.length() > 8) {
            inputPassword.setError("Password must be at least 5 characters and not more than 8");
            isValid = false;
        }
        if (isValid==false) {
            Toast.makeText(getApplicationContext(), "Error in form", Toast.LENGTH_SHORT).show();
        }
        if(email.isEmpty()) {
            inputEmail.setError("Email is required");
            isValid = false;
        }
        if(password.isEmpty() || password.length() < 5 || password.length() > 8) {
            inputPassword.setError("Password must be at least 5 characters and not more than 8");
            isValid = false;
        }
if (isValid)
{

    Profile myProfile = AppDatabase.getdb(this).getProfileQuery().checkEmailPassw(email, password);
    if (myProfile != null) {
        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(login.this, MainActivity.class);
        startActivity(intent);
    }
    else {
        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
    }
    }

        return isValid;
    }

}