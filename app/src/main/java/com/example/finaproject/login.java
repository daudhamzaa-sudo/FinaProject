package com.example.finaproject;

// استيراد مكتبات Android الأساسية
import android.annotation.SuppressLint; // لتجاهل بعض التحذيرات
import android.content.Intent;          // لإنشاء Intent للتنقل بين الشاشات
import android.content.SharedPreferences;
import android.os.Bundle;              // لتمرير بيانات للشاشة عند إنشائها
import android.util.Log;
import android.view.View;              // للتعامل مع عناصر الواجهة مثل الأزرار
import android.widget.Button;          // زر
import android.widget.Toast;           // لعرض رسائل مؤقتة على الشاشة

// مكتبات داعمة لتصميم واجهة تفاعلية
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// استيراد قاعدة البيانات الخاصة بالمشروع
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;

// مكتبة لإدخال النصوص بطريقة جميلة
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

// تعريف الكلاس login (Activity لتسجيل الدخول)
public class login extends AppCompatActivity {

    // تعريف أزرار الشاشة
    private Button btnLogin;              // زر تسجيل الدخول

    private FirebaseAuth mAuth;
    private Button btnLoginWithFacebook;  // زر تسجيل الدخول عبر فيسبوك
    private Button btnLoginWithGoogle;    // زر تسجيل الدخول عبر جوجل
    private Button btnSignup1;            // زر الانتقال لشاشة التسجيل (SignUp)
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;

    private final int RC_SIGN_IN = 100;
    // تعريف حقول الإدخال
    private TextInputEditText inputEmail;    // حقل إدخال البريد الإلكتروني
    private TextInputEditText inputPassword; // حقل إدخال كلمة المرور

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // تمكين التصميم الذي يشغل كامل الشاشة حتى حواف الجهاز
        EdgeToEdge.enable(this);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish(); //
        }

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
         btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle); // زر جوجل (معلق حالياً)
        inputEmail = findViewById(R.id.inputEmail);               // حقل البريد
        inputPassword = findViewById(R.id.inputPassword);         // حقل الباسورد
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // عند الضغط على زر التسجيل
        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // الانتقال لشاشة التسجيل
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });
        btnLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                   // Log.d(TAG, "signInWithEmail:success");
                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); //
                                    // updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                   // Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });
            }
        });
                    // 4. الانتقال للشاشة الرئيسية
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

    //داله للدخول باستخدام جوجل
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential =
                        GoogleAuthProvider.getCredential(account.getIdToken(), null);

                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {

                                // ✅ تسجيل دخول ناجح
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