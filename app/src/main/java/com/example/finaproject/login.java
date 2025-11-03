package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    protected void onCreate(Bundle savedInstanceState)
    {
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
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view1) {

                Intent intent1 = new Intent(login.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }
}