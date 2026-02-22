package com.example.finaproject;
// استيراد مكتبة المصادقة من Firebase
import com.google.firebase.auth.FirebaseAuth;

// استيرادات أساسية من Android
import android.content.Intent; // للانتقال بين الشاشات
import android.os.Bundle; // لتمرير البيانات عند إنشاء الشاشة
import android.view.View; // الفئة الأساسية لعناصر الواجهة
import android.widget.Button; // عنصر الزر
import android.widget.Toast; // لعرض رسائل قصيرة للمستخدم

// استيرادات لدعم تصميم الواجهة وتوافقها
import androidx.activity.EdgeToEdge; // لتفعيل وضع العرض من الحافة إلى الحافة
import androidx.appcompat.app.AppCompatActivity; // الفئة الأساسية للأنشطة
import androidx.core.graphics.Insets; // للتعامل مع هوامش النظام
import androidx.core.view.ViewCompat; // فئة مساعدة للتوافق
import androidx.core.view.WindowInsetsCompat; // للتعامل مع هوامش النوافذ

// استيرادات خاصة بقاعدة البيانات المحلية (Room)
import com.example.finaproject.data.AppDatabase; // الفئة الرئيسية لقاعدة البيانات
import com.example.finaproject.data.MyProfileTable.MyProfileQuery; // واجهة استعلامات الملف الشخصي
import com.example.finaproject.data.MyProfileTable.Profile; // كائن يمثل الملف الشخصي

// استيرادات من مكتبة Material Design
import com.google.android.material.button.MaterialButton; // زر بتصميم Material
import com.google.android.material.textfield.TextInputEditText; // حقل إدخال نص متقدم
import com.google.android.material.textfield.TextInputLayout; // حاوية لحقل الإدخال

/**
 * شاشة إنشاء حساب جديد (signup).
 * تتيح للمستخدم إدخال بياناته لإنشاء حساب جديد في قاعدة البيانات المحلية (Room) و Firebase.
 */
public class signup extends AppCompatActivity {

    // --- تعريف متغيرات عناصر الواجهة ---
    /** زر إنشاء الحساب. */
    private Button btnSignup1;
    /** حقل إدخال البريد الإلكتروني. */
    private TextInputEditText inputEmail;
    /** حقل إدخال اسم المستخدم. */
    private TextInputEditText inputUsername;
    /** حقل إدخال كلمة المرور. */
    private TextInputEditText inputPassword;
    /** حقل تأكيد كلمة المرور. */
    private TextInputEditText inputConfirmPassword;
    /** زر تسجيل الدخول (غير مستخدم في هذا الكود). */
    private MaterialButton btnLogin;

    // --- تعريف متغير المصادقة من Firebase ---
    /** مرجع لخدمة المصادقة في Firebase. */
    private com.google.firebase.auth.FirebaseAuth mAuth; // <--- أضف هذا السطر

    /**
     * دالة `onCreate` هي نقطة انطلاق النشاط. يتم استدعاؤها عند إنشاء الشاشة.
     * @param savedInstanceState بيانات محفوظة من حالة سابقة للنشاط إذا كانت متاحة.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // استدعاء الدالة الأم لاستكمال عملية الإنشاء
        super.onCreate(savedInstanceState);
        // تفعيل وضع العرض من الحافة إلى الحافة
        EdgeToEdge.enable(this);
        // ربط هذا النشاط بملف التصميم activity_signup.xml
        setContentView(R.layout.activity_signup);

        // إعداد مستمع لضبط هوامش الشاشة تلقائيًا لتجنب التداخل مع أشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // الحصول على أبعاد هوامش النظام (مثل شريط الحالة)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // تطبيق هذه الهوامش كـ padding للعنصر الرئيسي في الواجهة
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // إرجاع الـ insets لاستكمال المعالجة
            return insets;
        });

        // --- ربط المتغيرات البرمجية بعناصر الواجهة في ملف XML ---
        btnSignup1 = findViewById(R.id.btnSignup1);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        // إعداد مستمع النقر لزر إنشاء الحساب
        btnSignup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // عند النقر، يتم استدعاء دالة التحقق من البيانات وإنشائها
                if (validateAndReadData()) {
                    // إذا أرجعت الدالة `true`، يتم الانتقال إلى الشاشة الرئيسية
                    Intent intent = new Intent(signup.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * دالة تقوم بالتحقق من صحة المدخلات، ثم قراءة البيانات وإنشاء الحساب.
     * @return `true` إذا كانت المدخلات صالحة، و `false` خلاف ذلك.
     */
    public boolean validateAndReadData() {
        // متغير لتتبع صلاحية البيانات، يبدأ بـ true
        boolean isValid = true;
        // قراءة البيانات من حقول الإدخال وإزالة أي مسافات بيضاء في البداية أو النهاية
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // --- سلسلة من عمليات التحقق ---

        // التحقق من أن اسم المستخدم ليس فارغًا
        if (username.isEmpty()) {
            inputUsername.setError("Username is required"); // عرض رسالة خطأ
            isValid = false; // تحديث حالة الصلاحية إلى false
        }
        // التحقق من أن البريد الإلكتروني ليس فارغًا
        if (email.isEmpty()) {
            inputEmail.setError("Email is required");
            isValid = false;
        } // التحقق من أن صيغة البريد الإلكتروني صحيحة
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        // التحقق من أن كلمة المرور ليست فارغة وأن طولها بين 5 و 8 أحرف
        if (password.isEmpty() || password.length() < 5 || password.length() > 8) {
            inputPassword.setError("Password must be at least 5 characters and not more than 8");
            isValid = false;
        }
        // التحقق من أن حقل تأكيد كلمة المرور ليس فارغًا
        if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError("Confirm Password is required");
            isValid = false;
        }
        // التحقق من تطابق كلمتي المرور
        if (!password.equals(confirmPassword)) {
            inputPassword.setError("Password does not match");
            inputConfirmPassword.setError("Password does not match");
            isValid = false;
        }
        // إذا فشل أي من التحققات السابقة
        if (isValid == false) {
            // عرض رسالة عامة للمستخدم
            Toast.makeText(getApplicationContext(), "Error in form", Toast.LENGTH_SHORT).show();
        }

        // إذا كانت البيانات لا تزال صالحة بعد التحققات الأولية
        if (isValid) {
            // فحص هل البريد الإلكتروني موجود مسبقاً في قاعدة البيانات المحلية (Room)
            Profile myProfile = AppDatabase.getdb(this).getProfile().checkEmail(email);
            // إذا تم العثور على ملف شخصي بنفس البريد
            if (myProfile != null) {
                inputEmail.setError("Email already registered"); // عرض رسالة خطأ
                isValid = false; // تحديث حالة الصلاحية إلى false
            }
        }

        // إذا كانت البيانات لا تزال صالحة بعد كل التحققات
        if (isValid) {
            // إنشاء كائن Profile جديد
            Profile myUser = new Profile();
            // تعبئة بيانات الكائن من المدخلات
            myUser.setUsername(username);
            myUser.setEmail(email);
            myUser.setPassw(password);
            // إدراج الكائن (المستخدم الجديد) في قاعدة بيانات Room المحلية
            AppDatabase.getdb(getApplicationContext()).getProfile().insert(myUser);

            // --- إنشاء الحساب في Firebase ---
            FirebaseAuth.getInstance() // الحصول على نسخة من خدمة المصادقة
                    .createUserWithEmailAndPassword(email, password) // استدعاء دالة إنشاء الحساب
                    .addOnSuccessListener(result -> { // مستمع يتم استدعاؤه في حال نجاح العملية
                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
                        finish(); // إنهاء شاشة التسجيل الحالية
                    })
                    .addOnFailureListener(e -> { // مستمع يتم استدعاؤه في حال فشل العملية
                        // عرض رسالة الخطأ القادمة من Firebase للمستخدم
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // إرجاع القيمة النهائية لـ isValid
        return isValid;
    }
}
