
        package com.example.finaproject; // غيّر هذا حسب اسم مشروعك

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

        public class signup extends AppCompatActivity {

            // تعريف العناصر
            private EditText etUsername, etEmail, etPassword, etConfirmPassword;
            private Spinner spinnerRegion;
            private Button btnSignup;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_signup); // يربط هذه الشاشة مع ملف XML

                // ربط العناصر مع واجهة XML
                etUsername = findViewById(R.id.etUsername);
                etEmail = findViewById(R.id.etEmail);
                etPassword = findViewById(R.id.etPassword);
                etConfirmPassword = findViewById(R.id.etConfirmPassword);
                spinnerRegion = findViewById(R.id.spinnerRegion);
                btnSignup = findViewById(R.id.btnSignup);

                // تعبئة قائمة المناطق (يمكنك تعديلها حسب بلدك)
                String[] regions = {"Select region", "North", "Center", "South"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, regions);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRegion.setAdapter(adapter);

                // عند الضغط على زر التسجيل
                btnSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = etUsername.getText().toString().trim();
                        String email = etEmail.getText().toString().trim();
                        String password = etPassword.getText().toString().trim();
                        String confirmPassword = etConfirmPassword.getText().toString().trim();
                        String region = spinnerRegion.getSelectedItem().toString();

                        // التحقق من المدخلات
                        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!password.equals(confirmPassword)) {
                            Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (region.equals("Select region")) {
                            Toast.makeText(signup.this, "Please select your region", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // إذا كانت جميع الحقول صحيحة
                        Toast.makeText(signup.this, "Account created successfully!", Toast.LENGTH_LONG).show();

                        // هنا لاحقًا يمكن إضافة رفع البيانات إلى قاعدة بيانات أو Firebase
                        finish(); // يغلق الشاشة ويرجع للشاشة السابقة (مثلاً شاشة الدخول)
                    }
                });
            }
        }
    }
}