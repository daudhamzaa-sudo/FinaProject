package com.example.finaproject;

import com.google.firebase.auth.FirebaseAuth;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * شاشة إنشاء حساب جديد: تم تعديلها لتسمح بتعيين مستخدم كمسؤول (Admin).
 */
public class signup extends AppCompatActivity {

    private Button btnSignup1;
    private TextInputEditText inputEmail;
    private TextInputEditText inputUsername;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmPassword;

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

        btnSignup1.setOnClickListener(view -> {
            if (validateAndReadData()) {
                // الانتقال للشاشة الرئيسية بعد النجاح
                Intent intent = new Intent(signup.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * التحقق من البيانات المدخلة وحفظ المستخدم في قاعدة البيانات المحلية و Firebase.
     */
    public boolean validateAndReadData() {
        boolean isValid = true;
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // فحص الحقول الفارغة والصيغ
        if (username.isEmpty()) {
            inputUsername.setError("Username is required");
            isValid = false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Valid email is required");
            isValid = false;
        }
        if (password.isEmpty() || password.length() < 5 || password.length() > 8) {
            inputPassword.setError("Password must be 5-8 characters");
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (isValid) {
            // فحص هل الايميل موجود مسبقاً في قاعدة البيانات المحلية (Room)
            Profile existingProfile = AppDatabase.getdb(this).getProfile().checkEmail(email);
            if (existingProfile != null) {
                inputEmail.setError("Email already registered");
                isValid = false;
            }
        }

        if (isValid) {
            // إنشاء كائن المستخدم الجديد
            Profile myUser = new Profile();
            myUser.setUsername(username);
            myUser.setEmail(email);
            myUser.setPassw(password);

            // --- منطق الأدمن الجديد ---
            // إذا كان الإيميل هو "admin@gmail.com"، اجعله مديراً تلقائياً
            if (email.equalsIgnoreCase("admin@gmail.com")) {
                myUser.setAdmin(true);
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            } else {
                myUser.setAdmin(false);
            }

            // حفظ البيانات محلياً
            AppDatabase.getdb(getApplicationContext()).getProfile().insert(myUser);

            // إنشاء الحساب في Firebase
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        return isValid;
    }
}
