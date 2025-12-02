package com.example.finaproject;

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
import com.example.finaproject.data.MyProfileTable.MyProfileQuery;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class signup extends AppCompatActivity {

    private Button btnSignup1;

    private TextInputEditText inputEmail;
    private TextInputEditText inputUsername;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSignup1 = findViewById(R.id.btnSignup1);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAndReadData()) {
                    Intent intent = new Intent(signup.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean validateAndReadData() {
        boolean isValid = true;
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (username.isEmpty()) {
            inputUsername.setError("Username is required");
            isValid = false;
        }
        if (email.isEmpty()) {
            inputEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        if (password.isEmpty() || password.length() < 5 || password.length() > 8) {
            inputPassword.setError("Password must be at least 5 characters and not more than 8");
            isValid = false;
        }
        if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError("Confirm Password is required");
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            inputPassword.setError("Password does not match");
            inputConfirmPassword.setError("Password does not match");
            isValid = false;
        }
        if (isValid == false) {

            Toast.makeText(getApplicationContext(), "Error in form", Toast.LENGTH_SHORT).show();

        }

//        if (isValid) {
//            // فحص هل الايميل موجود من قبل
//
//            Profile myProfile = AppDatabase.getdb(this).getProfileQuery().checkEmail(email);
//            if (myProfile != null) {
//                inputEmail.setError("Email already registered");
//                isValid = false;
//            }
//        }
        if (isValid) {
            // Do something with the data

            Profile myUser = new Profile();
            myUser.setUsername(username);
            myUser.setEmail(email);
            myUser.setPassw(password);
            AppDatabase.getdb(getApplicationContext()).getMyProfileQuery().insert(myUser);
        }


        return isValid;


    }
}





