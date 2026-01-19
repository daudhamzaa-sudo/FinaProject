package com.example.finaproject;

// استيراد مكتبات Android الأساسية
import android.annotation.SuppressLint; // لتجاهل بعض التحذيرات
import android.content.Intent;          // لإنشاء Intent للتنقل بين الشاشات
import android.content.SharedPreferences;
import android.os.Bundle;              // لتمرير بيانات للشاشة عند إنشائها
import android.view.View;              // للتعامل مع عناصر الواجهة مثل الأزرار
import android.widget.Button;          // زر
import android.widget.Toast;           // لعرض رسائل مؤقتة على الشاشة

// مكتبات داعمة لتصميم واجهة تفاعلية
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// استيراد قاعدة البيانات الخاصة بالمشروع
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;

// مكتبة لإدخال النصوص بطريقة جميلة
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

// تعريف الكلاس login (Activity لتسجيل الدخول)
public class login extends AppCompatActivity {

    // تعريف أزرار الشاشة
    private Button btnLogin;              // زر تسجيل الدخول

    private FirebaseAuth mAuth;
    private Button btnLoginWithFacebook;  // زر تسجيل الدخول عبر فيسبوك
    private Button btnLoginWithGoogle;    // زر تسجيل الدخول عبر جوجل
    private Button btnSignup1;            // زر الانتقال لشاشة التسجيل (SignUp)

    // تعريف حقول الإدخال
    private TextInputEditText inputEmail;    // حقل إدخال البريد الإلكتروني
    private TextInputEditText inputPassword; // حقل إدخال كلمة المرور

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تمكين التصميم الذي يشغل كامل الشاشة حتى حواف الجهاز
        EdgeToEdge.enable(this);

        // ربط الكلاس بواجهة المستخدم XML الخاصة بالشاشة
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance(); // <--- أضف هذا السطر هنا
        // ضبط الهوامش بحيث لا تتداخل مع شريط النظام أو الأزرار العلوية والسفلية
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط المتغيرات بعناصر XML
        btnLogin = findViewById(R.id.btnLogin);                   // زر تسجيل الدخول
        btnSignup1 = findViewById(R.id.btnSignup1);               // زر التسجيل
        btnLoginWithFacebook = findViewById(R.id.btnLoginWithFacebook); // زر فيسبوك
        // btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle); // زر جوجل (معلق حالياً)
        inputEmail = findViewById(R.id.inputEmail);               // حقل البريد
        inputPassword = findViewById(R.id.inputPassword);         // حقل الباسورد

        // عند الضغط على زر التسجيل
        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // الانتقال لشاشة التسجيل
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        // عند الضغط على زر تسجيل الدخول
        // داخل onCreate في login.java
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. استدعاء الدالة واستقبال بيانات المستخدم
                Profile loggedInUser = validateAndReadData();

                // 2. التحقق من أن تسجيل الدخول ناجح
                if (loggedInUser != null) {
                    // 3. حفظ صلاحية المستخدم في الذاكرة المؤقتة
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("IS_ADMIN", loggedInUser.isAdmin()); // <-- حفظ صلاحية المدير
                    editor.apply();

                    // 4. الانتقال للشاشة الرئيسية
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // إغلاق شاشة تسجيل الدخول
                }
            }
        });

    }

    // دالة للتحقق من صحة البيانات المدخلة في حقول البريد وكلمة المرور
// غيّر نوع الإرجاع من boolean إلى Profile
    private Profile validateAndReadData() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError("Invalid or empty email");
            return null; // فشل
        }
        if (password.isEmpty()) {
            inputPassword.setError("Password is required");
            return null; // فشل
        }

        AppDatabase db = AppDatabase.getdb(getApplicationContext());
        Profile profile = db.getProfile().checkEmail(email);

        if (profile != null && profile.getPassw().equals(password)) {
            return profile; // نجاح: أرجع بيانات المستخدم كاملة
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            return null; // فشل
        }
    }


    // دالة للتحقق من صحة البريد الإلكتروني بصيغة صحيحة
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

}