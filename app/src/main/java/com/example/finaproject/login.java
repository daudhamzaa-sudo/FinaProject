package com.example.finaproject;

// استيراد مكتبات Android الأساسية
import android.annotation.SuppressLint; 
import android.content.Intent;          
import android.content.SharedPreferences;
import android.os.Bundle;              
import android.util.Log;
import android.view.View;              
import android.widget.Button;          
import android.widget.Toast;           

// مكتبات داعمة لتصميم واجهة تفاعلية ومتوافقة مع الحواف
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// استيراد قاعدة البيانات المحلية (Room) والكيانات الخاصة بها
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;

// مكتبات Firebase وخدمات Google لتسجيل الدخول
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * شاشة تسجيل الدخول: تتيح للمستخدم الدخول عبر البريد الإلكتروني أو حساب جوجل.
 */
public class login extends AppCompatActivity {

    // عناصر واجهة المستخدم (Buttons)
    private Button btnLogin;              
    private FirebaseAuth mAuth;           // مرجع لخدمة FirebaseAuth
    private Button btnLoginWithFacebook;  
    private Button btnLoginWithGoogle;    
    private Button btnSignup1;            
    private GoogleSignInClient googleSignInClient; // عميل تسجيل الدخول عبر جوجل
    private FirebaseAuth auth;

    // رمز تعريف لعملية تسجيل الدخول عبر جوجل
    private final int RC_SIGN_IN = 100;
    
    // حقول إدخال النص
    private TextInputEditText inputEmail;    
    private TextInputEditText inputPassword; 

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تفعيل وضع الشاشة الكاملة (Edge-to-Edge)
        EdgeToEdge.enable(this);
        
        // التحقق مما إذا كان هناك مستخدم مسجل دخول مسبقاً عبر Firebase
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish(); 
        }

        // تحديد ملف التنسيق الخاص بالشاشة
        setContentView(R.layout.activity_login);
        
        // تهيئة Firebase Auth
        mAuth = FirebaseAuth.getInstance(); 
        
        // ضبط هوامش الشاشة لتناسب أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ربط المتغيرات بالعناصر الموجودة في ملف XML
        btnLogin = findViewById(R.id.btnLogin);                   
        btnSignup1 = findViewById(R.id.btnSignup1);               
        btnLoginWithFacebook = findViewById(R.id.btnLoginWithFacebook); 
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle); 
        inputEmail = findViewById(R.id.inputEmail);               
        inputPassword = findViewById(R.id.inputPassword);         
        auth = FirebaseAuth.getInstance();

        // إعداد خيارات تسجيل الدخول عبر جوجل (طلب البريد الإلكتروني و ID Token)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // الانتقال لشاشة إنشاء حساب جديد عند الضغط على زر Signup
        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        // بدء عملية تسجيل الدخول عبر جوجل عند الضغط على الزر الخاص بها
        btnLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // معالجة ضغطة زر تسجيل الدخول (يدعم التحقق المحلي و Firebase)
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. استدعاء الدالة للتحقق من البيانات محلياً (Room Database)
                Profile loggedInUser = validateAndReadData();

                // 2. إذا نجح التحقق المحلي
                if (loggedInUser != null) {
                    // 3. حفظ حالة المستخدم (هل هو مدير أم لا) في SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("IS_ADMIN", loggedInUser.isAdmin()); 
                    editor.apply();
                }

                // تنفيذ تسجيل الدخول عبر Firebase باستخدام البريد والباسورد
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // نجاح الدخول: الانتقال للشاشة الرئيسية
                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); 
                                } else {
                                    // فشل الدخول: عرض رسالة خطأ
                                    Toast.makeText(login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }

    /**
     * دالة للتحقق من صحة المدخلات والبحث في قاعدة البيانات المحلية.
     * @return كائن الملف الشخصي Profile في حال النجاح، أو null في حال الفشل.
     */
    private Profile validateAndReadData() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError("Invalid or empty email");
            return null; 
        }
        if (password.isEmpty()) {
            inputPassword.setError("Password is required");
            return null; 
        }

        // البحث عن المستخدم في قاعدة بيانات Room
        AppDatabase db = AppDatabase.getdb(getApplicationContext());
        Profile profile = db.getProfile().checkEmail(email);

        if (profile != null && profile.getPassw().equals(password)) {
            return profile; 
        } else {
            return null; 
        }
    }

    /**
     * دالة مساعدة للتحقق من صيغة البريد الإلكتروني.
     */
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * معالجة النتيجة القادمة من شاشة تسجيل دخول جوجل.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // الحصول على بيانات حساب جوجل
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // إنشاء بيانات اعتماد Firebase باستخدام ID Token من جوجل
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                // تسجيل الدخول في Firebase باستخدام بيانات جوجل
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                // نجاح تسجيل الدخول عبر جوجل
                                startActivity(new Intent(login.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Google Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (ApiException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
