package com.example.finaproject;

// استيراد المكتبات اللازمة للواجهات، المصادقة (Firebase)، وحفظ البيانات (Room/SharedPreferences)
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

/**
 * شاشة تسجيل الدخول (Login Activity): تتيح للمستخدم الدخول بالبريد الإلكتروني أو حساب جوجل.
 */
public class login extends AppCompatActivity {

    // تعريف عناصر الواجهة
    private Button btnLogin, btnSignup1, btnLoginWithGoogle;
    private TextInputEditText inputEmail, inputPassword;
    private FirebaseAuth mAuth; // كائن المصادقة الخاص بـ Firebase
    private GoogleSignInClient googleSignInClient; // كائن التعامل مع تسجيل دخول جوجل
    private final int RC_SIGN_IN = 100; // رمز خاص للتحقق من نتيجة طلب تسجيل دخول جوجل

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تفعيل العرض الشامل
        EdgeToEdge.enable(this);

        // التحقق مما إذا كان المستخدم مسجلاً للدخول مسبقاً، إذا نعم ننتقل مباشرة للرئيسية
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(login.this, MainActivity.class));
            finish();
            return;
        }

        // تحديد ملف التصميم
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance(); // تهيئة كائن Firebase Auth

        // ضبط الهوامش تلقائياً لتناسب شريط الحالة
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews(); // ربط العناصر
        setupGoogleSignIn(); // إعداد تسجيل دخول جوجل
    }

    /**
     * ربط متغيرات الجافا بالعناصر في الـ XML وبرمجة مستمعي النقرات
     */
    private void initViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup1 = findViewById(R.id.btnSignup1);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        // زر الانتقال لشاشة إنشاء حساب جديد
        btnSignup1.setOnClickListener(v -> startActivity(new Intent(login.this, signup.class)));

        // زر تسجيل الدخول بواسطة جوجل
        btnLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // زر تسجيل الدخول العادي
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    /**
     * إعداد خيارات تسجيل الدخول عبر جوجل
     */
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // طلب رمز الـ ID لاستخدامه مع Firebase
                .requestEmail() // طلب الوصول لبريد المستخدم
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * معالجة عملية تسجيل الدخول عند الضغط على الزر
     */
    private void attemptLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // التأكد من ملء الحقول
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // التحقق من قاعدة البيانات المحلية في "خيط خلفي" (Thread) لمنع توقف التطبيق
        new Thread(() -> {
            // جلب بيانات المستخدم من قاعدة بيانات Room المحلية
            Profile user = AppDatabase.getdb(getApplicationContext()).getProfile().checkEmail(email);
            
            runOnUiThread(() -> {
                // إذا وجد المستخدم وكلمة المرور صحيحة محلياً
                if (user != null && user.getPassw().equals(password)) {
                    // حفظ حالة كونه مديراً (Admin) أم لا في الـ SharedPreferences لاستخدامها لاحقاً
                    SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("IS_ADMIN", user.isAdmin());
                    editor.apply();
                }
                
                // تسجيل الدخول الحقيقي عبر خادم Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // نجاح الدخول: الانتقال للرئيسية
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                        } else {
                            // فشل الدخول: عرض رسالة تنبيه
                            Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
            });
        }).start();
    }

    /**
     * استقبال نتيجة طلب تسجيل دخول جوجل
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // جلب بيانات حساب جوجل من الـ Intent القادم
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // ربط حساب جوجل بـ Firebase
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(this, t -> {
                    if (t.isSuccessful()) {
                        // نجاح الربط والدخول
                        startActivity(new Intent(login.this, MainActivity.class));
                        finish();
                    }
                });
            } catch (ApiException e) {
                // فشل العملية (مثلاً المستخدم ألغى الاختيار)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
