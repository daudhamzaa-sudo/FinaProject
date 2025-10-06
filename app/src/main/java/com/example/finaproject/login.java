
package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

        public class login extends AppCompatActivity {

            EditText inputEmail, inputPassword;
            Button btnLogin;
            TextView txtSignup, txtForgotPassword;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login); // يربط مع ملف XML

                inputEmail = findViewById(R.id.inputEmail);
                inputPassword = findViewById(R.id.inputPassword);
                btnLogin = findViewById(R.id.btnLogin);
                txtSignup = findViewById(R.id.txtSignup);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String user = inputEmail.getText().toString().trim();
                        String pass = inputPassword.getText().toString().trim();

                        if (user.isEmpty() || pass.isEmpty()) {
                            Toast.makeText(login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, "Welcome, " + user + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this, mainScreen.class));
                        }
                    }
                });

                txtSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(login.this, signup.class));
                    }
                });

                txtForgotPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(login.this,mainScreen.class));
                    }
                });
            }
        }

