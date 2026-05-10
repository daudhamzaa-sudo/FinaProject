package com.example.finaproject;

// استيراد المكتبات اللازمة للمصادقة عبر Firebase والواجهات
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
 * شاشة إنشاء حساب جديد (Signup Activity): تتيح للمستخدمين الجدد تسجيل أنفسهم في التطبيق عبر Firebase.
 */
public class signup extends AppCompatActivity {

    // تعريف متغيرات عناصر الواجهة (حقول الإدخال والأزرار)
    private Button btnSignup1;
    private TextInputEditText inputEmail;
    private TextInputEditText inputUsername;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تفعيل وضع العرض من الحافة إلى الحافة (استغلال كامل مساحة الشاشة)
        EdgeToEdge.enable(this);
        // ربط الكود بملف التصميم XML الخاص بشاشة التسجيل
        setContentView(R.layout.activity_signup);

        // ضبط الهوامش تلقائياً لضمان عدم تداخل الواجهة مع أشرطة النظام (الساعة والبطارية)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط المتغيرات بالعناصر الموجودة في التصميم باستخدام المعرفات (IDs)
        btnSignup1 = findViewById(R.id.btnSignup1);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        // برمجة زر "إنشاء الحساب" لاستدعاء دالة التحقق عند النقر
        btnSignup1.setOnClickListener(view -> {
            validateAndCreateAccount();
        });
    }

    /**
     * دالة التحقق من صحة البيانات المدخلة وبدء عملية إنشاء الحساب في Firebase.
     */
    private void validateAndCreateAccount() {
        // جلب النصوص من الحقول وحذف المسافات الزائدة من البداية والنهاية
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // 1. التحقق من أن حقل اسم المستخدم ليس فارغاً
        if (username.isEmpty()) {
            inputUsername.setError("Username is required");
            return;
        }
        // 2. التحقق من صحة صيغة البريد الإلكتروني باستخدام نمط قياسي
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Valid email is required");
            return;
        }
        // 3. التحقق من طول كلمة المرور (يجب أن تكون بين 5 و 8 أحرف لزيادة الأمان البسيط)
        if (password.isEmpty() || password.length() < 5 || password.length() > 8) {
            inputPassword.setError("Password must be 5-8 characters");
            return;
        }
        // 4. التأكد من تطابق كلمة المرور مع تأكيد كلمة المرور
        if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Passwords do not match");
            return;
        }

        // 5. البدء بإنشاء الحساب في نظام Firebase Authentication
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    // في حال النجاح: إظهار رسالة تأكيد للمستخدم
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    
                    // الانتقال تلقائياً للشاشة الرئيسية (MainActivity)
                    Intent intent = new Intent(signup.this, MainActivity.class);
                    startActivity(intent);
                    // إغلاق شاشة التسجيل لكي لا يعود إليها المستخدم عند الضغط على زر الرجوع
                    finish();
                })
                .addOnFailureListener(e -> {
                    // في حال الفشل (مثل بريد مسجل مسبقاً): إظهار سبب الخطأ القادم من Firebase
                    Toast.makeText(this, "Firebase Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
