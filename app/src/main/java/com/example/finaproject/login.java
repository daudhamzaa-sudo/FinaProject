package com.example.finaproject;

// استيراد مكتبات Android الأساسية
import android.annotation.SuppressLint;      // لتجاهل تحذيرات معينة من المحلل
import android.content.Intent;              // للانتقال بين شاشات التطبيق
import android.content.SharedPreferences;    // لحفظ البيانات البسيطة (مثل حالة المدير)
import android.os.Bundle;                  // لتمرير البيانات بين حالات النشاط
import android.util.Log;                    // لتسجيل الرسائل في Logcat لتصحيح الأخطاء
import android.view.View;                  // الفئة الأساسية لعناصر واجهة المستخدم
import android.widget.Button;              // عنصر زر قابل للنقر
import android.widget.Toast;               // لعرض رسائل قصيرة للمستخدم

// مكتبات داعمة لتصميم واجهة تفاعلية ومتوافقة مع الحواف
import androidx.activity.EdgeToEdge;       // لتفعيل وضع العرض من الحافة إلى الحافة
import androidx.annotation.NonNull;          // للإشارة إلى أن قيمة المعلمة أو الدالة لا يمكن أن تكون null
import androidx.appcompat.app.AppCompatActivity; // الفئة الأساسية للأنشطة التي تستخدم شريط الأدوات
import androidx.core.graphics.Insets;        // للتعامل مع هوامش النظام
import androidx.core.view.ViewCompat;         // فئة مساعدة للتوافق مع إصدارات Android المختلفة
import androidx.core.view.WindowInsetsCompat;  // للتعامل مع هوامش النوافذ (مثل شريط الحالة)

// استيراد قاعدة البيانات المحلية (Room) والكيانات الخاصة بها
import com.example.finaproject.data.AppDatabase;          // الفئة الرئيسية لقاعدة بيانات Room
import com.example.finaproject.data.MyProfileTable.Profile; // كائن يمثل جدول الملفات الشخصية

// مكتبات Firebase وخدمات Google لتسجيل الدخول
import com.google.android.gms.auth.api.signin.GoogleSignIn; // الفئة الرئيسية لتسجيل الدخول بحساب جوجل
import com.google.android.gms.auth.api.signin.GoogleSignInAccount; // يمثل حساب جوجل الذي تم تسجيل الدخول به
import com.google.android.gms.auth.api.signin.GoogleSignInClient;   // العميل الرئيسي للتفاعل مع Google Sign-In API
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;  // لتكوين خيارات تسجيل الدخول (مثل طلب البريد الإلكتروني)
import com.google.android.gms.common.api.ApiException;        // للتعامل مع الأخطاء القادمة من Google APIs
import com.google.android.gms.tasks.OnCompleteListener;     // مستمع يتم استدعاؤه عند اكتمال مهمة غير متزامنة
import com.google.android.gms.tasks.Task;                     // يمثل عملية غير متزامنة
import com.google.android.material.textfield.TextInputEditText; // حقل إدخال نص متقدم من مكتبة Material
import com.google.firebase.auth.AuthCredential;          // بيانات اعتماد المصادقة (مثل token)
import com.google.firebase.auth.AuthResult;               // نتيجة عملية المصادقة
import com.google.firebase.auth.FirebaseAuth;              // الفئة الرئيسية لخدمة المصادقة في Firebase
import com.google.firebase.auth.FirebaseUser;              // يمثل المستخدم المسجل دخوله حاليًا
import com.google.firebase.auth.GoogleAuthProvider;        // مزود المصادقة الخاص بجوجل لـ Firebase

/**
 * شاشة تسجيل الدخول: تتيح للمستخدم الدخول عبر البريد الإلكتروني أو حساب جوجل.
 * كما تتحقق من صلاحيات المستخدم (مدير أم لا) وتتحقق من وجود جلسة سابقة.
 */
public class login extends AppCompatActivity {

    // --- عناصر واجهة المستخدم ---
    /** زر تسجيل الدخول التقليدي بالبريد الإلكتروني وكلمة المرور. */
    private Button btnLogin;
    /** زر تسجيل الدخول باستخدام فيسبوك (غير مستخدم حاليًا). */
    private Button btnLoginWithFacebook;
    /** زر تسجيل الدخول باستخدام حساب جوجل. */
    private Button btnLoginWithGoogle;
    /** زر الانتقال إلى شاشة إنشاء حساب جديد. */
    private Button btnSignup1;
    /** حقل إدخال البريد الإلكتروني. */
    private TextInputEditText inputEmail;
    /** حقل إدخال كلمة المرور. */
    private TextInputEditText inputPassword;

    // --- كائنات Firebase و Google ---
    /** مرجع رئيسي لخدمة المصادقة في Firebase. */
    private FirebaseAuth mAuth;
    /** مرجع آخر لخدمة المصادقة، يستخدم في تسجيل دخول جوجل. */
    private FirebaseAuth auth;
    /** العميل المسؤول عن إدارة عملية تسجيل الدخول عبر جوجل. */
    private GoogleSignInClient googleSignInClient;

    // --- ثوابت ---
    /** رمز طلب فريد لتمييز عملية تسجيل الدخول عبر جوجل عند عودة النتيجة. */
    private final int RC_SIGN_IN = 100;

    /**
     * دالة `onCreate` هي نقطة انطلاق النشاط. يتم استدعاؤها عند إنشاء الشاشة.
     * @param savedInstanceState بيانات محفوظة من حالة سابقة للنشاط إذا كانت متاحة.
     */
    @SuppressLint({"MissingInflatedId", "WrongViewCast"}) // تجاهل تحذيرات ربط العناصر
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // استدعاء الدالة الأم

        // تفعيل وضع الشاشة الكاملة (Edge-to-Edge) لعرض المحتوى تحت أشرطة النظام
        EdgeToEdge.enable(this);

        // التحقق مما إذا كان هناك مستخدم مسجل دخول مسبقاً في Firebase
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            // إذا كان هناك مستخدم، انتقل مباشرة إلى الشاشة الرئيسية
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish(); // إنهاء شاشة تسجيل الدخول لمنع المستخدم من العودة إليها بالضغط على زر "رجوع"
        }

        // تحديد ملف التنسيق (Layout) الخاص بهذه الشاشة
        setContentView(R.layout.activity_login);

        // تهيئة كائن FirebaseAuth للحصول على نسخة منه
        mAuth = FirebaseAuth.getInstance();

        // ضبط هوامش الشاشة تلقائيًا لتجنب تداخل الواجهة مع أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- ربط المتغيرات بالعناصر الموجودة في ملف التصميم XML ---
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup1 = findViewById(R.id.btnSignup1);
        btnLoginWithFacebook = findViewById(R.id.btnLoginWithFacebook);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        auth = FirebaseAuth.getInstance(); // تهيئة مرجع المصادقة الثاني

        // --- إعداد خيارات تسجيل الدخول عبر جوجل ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // طلب الـ ID Token من جوجل لمصادقة Firebase
                .requestEmail() // طلب الوصول إلى البريد الإلكتروني للمستخدم
                .build(); // بناء كائن الخيارات

        // إنشاء عميل تسجيل الدخول بجوجل مع الخيارات المحددة
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // --- إعداد مستمعي النقرات (Click Listeners) ---

        // إعداد مستمع لزر "إنشاء حساب" للانتقال إلى شاشة التسجيل
        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        // إعداد مستمع لزر "تسجيل الدخول بجوجل"
        btnLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent(); // إنشاء Intent لبدء شاشة تسجيل دخول جوجل
            startActivityForResult(signInIntent, RC_SIGN_IN); // بدء الشاشة وانتظار النتيجة
        });

        // إعداد مستمع لزر "تسجيل الدخول" الرئيسي
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // الخطوة 1: استدعاء الدالة للتحقق من البيانات محلياً في قاعدة بيانات Room
                Profile loggedInUser = validateAndReadData();

                // الخطوة 2: إذا نجح التحقق المحلي (تم العثور على المستخدم وكلمة المرور صحيحة)
                if (loggedInUser != null) {
                    // الخطوة 3: حفظ صلاحية المستخدم (هل هو مدير أم لا) في SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE); // الحصول على ملف التفضيلات
                    SharedPreferences.Editor editor = prefs.edit(); // بدء التعديل
                    editor.putBoolean("IS_ADMIN", loggedInUser.isAdmin()); // حفظ قيمة `isAdmin`
                    editor.apply(); // تطبيق التغييرات
                }

                // --- تنفيذ تسجيل الدخول عبر Firebase باستخدام البريد الإلكتروني وكلمة المرور ---
                String email = inputEmail.getText().toString().trim(); // الحصول على البريد الإلكتروني من حقل الإدخال
                String password = inputPassword.getText().toString().trim(); // الحصول على كلمة المرور

                // التأكد من أن الحقول ليست فارغة قبل محاولة تسجيل الدخول
                if (!email.isEmpty() && !password.isEmpty()) {
                    // استدعاء دالة Firebase لتسجيل الدخول
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // في حال نجاح تسجيل الدخول: الانتقال إلى الشاشة الرئيسية
                                        Intent intent = new Intent(login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // إنهاء شاشة تسجيل الدخول
                                    } else {
                                        // في حال فشل تسجيل الدخول: عرض رسالة خطأ للمستخدم
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
     * دالة للتحقق من صحة المدخلات (البريد وكلمة المرور) والبحث عن المستخدم في قاعدة البيانات المحلية Room.
     * @return كائن `Profile` في حال كان المستخدم موجودًا وكلمة المرور صحيحة، أو `null` في حال الفشل.
     */
    private Profile validateAndReadData() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // التحقق من أن البريد الإلكتروني صالح وغير فارغ
        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError("Invalid or empty email"); // عرض خطأ في حقل الإدخال
            return null; // إرجاع null للدلالة على الفشل
        }
        // التحقق من أن كلمة المرور غير فارغة
        if (password.isEmpty()) {
            inputPassword.setError("Password is required");
            return null;
        }

        // --- البحث عن المستخدم في قاعدة بيانات Room ---
        AppDatabase db = AppDatabase.getdb(getApplicationContext()); // الحصول على نسخة من قاعدة البيانات
        Profile profile = db.getProfile().checkEmail(email); // البحث عن المستخدم باستخدام بريده الإلكتروني

        // التحقق مما إذا تم العثور على المستخدم وأن كلمة المرور المدخلة تطابق المحفوظة في قاعدة البيانات
        if (profile != null && profile.getPassw().equals(password)) {
            return profile; // إرجاع كائن المستخدم في حال النجاح
        } else {
            return null; // إرجاع null في حال لم يتم العثور عليه أو كلمة المرور خاطئة
        }
    }

    /**
     * دالة مساعدة للتحقق من أن صيغة البريد الإلكتروني المدخلة صحيحة.
     * @param email البريد الإلكتروني المراد التحقق منه.
     * @return `true` إذا كانت الصيغة صحيحة، و `false` خلاف ذلك.
     */
    private boolean isValidEmail(String email) {
        // استخدام النمط القياسي في Android للتحقق من البريد الإلكتروني
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * دالة `onActivityResult` يتم استدعاؤها عند العودة من نشاط تم بدؤه بواسطة `startActivityForResult`.
     * هنا، نستخدمها لمعالجة النتيجة القادمة من شاشة تسجيل دخول جوجل.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // التحقق مما إذا كانت النتيجة قادمة من عملية تسجيل الدخول بجوجل (باستخدام رمز الطلب)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // محاولة الحصول على بيانات حساب جوجل من النتيجة
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // إنشاء "بيانات اعتماد" Firebase باستخدام الـ ID Token الذي تم الحصول عليه من جوجل
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                // استخدام بيانات الاعتماد لتسجيل الدخول في نظام Firebase
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                // في حال نجاح تسجيل الدخول بجوجل في Firebase، انتقل للشاشة الرئيسية
                                startActivity(new Intent(login.this, MainActivity.class));
                                finish(); // إنهاء شاشة تسجيل الدخول
                            } else {
                                // في حال الفشل، عرض رسالة خطأ
                                Toast.makeText(this, "Google Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (ApiException e) {
                // التعامل مع الأخطاء المحتملة من عملية تسجيل دخول جوجل (مثل إلغاء المستخدم للعملية)
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
