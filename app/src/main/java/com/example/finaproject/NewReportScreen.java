package com.example.finaproject; // تعريف الحزمة (المجلد) الذي ينتمي إليه الملف

// استيراد كافة المكتبات اللازمة للتعامل مع الموقع، الصور، قاعدة البيانات، والواجهات
import android.Manifest;                      // لاستخدام صلاحيات النظام (مثل الموقع)
import android.content.Intent;                // للانتقال أو فتح تطبيقات أخرى (مثل المعرض)
import android.content.pm.PackageManager;      // لفحص هل منح المستخدم الصلاحيات أم لا
import android.net.Uri;                       // للتعامل مع مسارات الصور
import android.os.Build;                      // لمعرفة إصدار أندرويد للهاتف
import android.os.Bundle;                     // لإدارة حالة الشاشة
import android.view.View;                     // المكون الأساسي للواجهة
import android.widget.Button;                 // عنصر الزر
import android.widget.ImageView;              // عنصر عرض الصور
import android.widget.TextView;               // عنصر عرض النصوص
import android.widget.Toast;                  // عرض رسائل سريعة للمستخدم

// استيراد مكتبات AndroidX الحديثة
import androidx.activity.EdgeToEdge;          // تفعيل عرض الشاشة الكاملة
import androidx.activity.result.ActivityResultLauncher; // أداة حديثة لانتظار نتائج من تطبيقات أخرى
import androidx.activity.result.contract.ActivityResultContracts; // عقود جاهزة (مثل اختيار محتوى)
import androidx.appcompat.app.AppCompatActivity; // الكلاس الأساسي للشاشات
import androidx.core.app.ActivityCompat;      // أداة مساعدة لطلب الصلاحيات
import androidx.core.content.ContextCompat;    // أداة لفحص الصلاحيات بشكل متوافق
import androidx.core.graphics.Insets;           // مسافات النظام
import androidx.core.view.ViewCompat;            // توافق الواجهات
import androidx.core.view.WindowInsetsCompat;     // هوامش النوافذ

// استيراد خدمات جوجل للموقع والبيانات
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.google.android.gms.location.FusedLocationProviderClient; // المحرك الرئيسي لجلب الموقع
import com.google.android.gms.location.LocationServices;            // خدمات الموقع من جوجل
import com.google.android.gms.location.Priority;                    // تحديد دقة الموقع المطلوبة
import com.google.android.gms.tasks.CancellationTokenSource;       // لإلغاء طلب الموقع إذا أغلق المستخدم الشاشة
import com.google.android.material.button.MaterialButton;           // زر متطور من مكتبة Material
import com.google.android.material.textfield.TextInputEditText;     // حقل إدخال نصوص متطور
import com.google.firebase.database.DatabaseReference;              // مرجع قاعدة بيانات Firebase
import com.google.firebase.database.FirebaseDatabase;               // محرك قاعدة بيانات Firebase

/**
 * كلاس NewReportScreen:
 * الوظيفة: يسمح للمستخدم بإنشاء بلاغ جديد (عنوان، وصف، صورة، موقع جغرافي).
 * المنطق: يحفظ البيانات محلياً (Room) لضمان العمل بدون إنترنت، وسحابياً (Firebase) للمزامنة.
 */
public class NewReportScreen extends AppCompatActivity {

    // --- تعريف متغيرات عناصر الواجهة ---
    private ImageView ivSelectedImage;       // لعرض الصورة التي يختارها المستخدم
    private Uri selectedImageUri;            // لتخزين مسار الصورة المختار
    private TextInputEditText inputTitle;    // حقل إدخال عنوان البلاغ
    private TextInputEditText inputDescription; // حقل إدخال وصف البلاغ
    private FusedLocationProviderClient fusedLocationClient; // أداة جلب الموقع من جوجل
    private TextView tvLocation;             // لعرض الإحداثيات المجلوبة
    private double latitude = 0;             // متغير لتخزين خط العرض
    private double longitude = 0;            // متغير لتخزين خط الطول
    private MaterialButton btnSubmit;        // زر حفظ وإرسال البلاغ

    // --- أدوات حديثة لطلب النتائج (أذونات وصور) ---
    private ActivityResultLauncher<String> pickImage; // أداة فتح معرض الصور واختيار واحدة
    private ActivityResultLauncher<String> requestReadMediaPermission; // طلب صلاحية الوصول للصور

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // دالة الإنشاء الأساسية
        EdgeToEdge.enable(this);             // جعل التطبيق يملأ الشاشة بالكامل
        setContentView(R.layout.activity_new_repor_screen); // ربط ملف التصميم XML

        // تهيئة محرك الموقع الجغرافي
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();           // دالة لربط العناصر البرمجية بالواجهة
        setupLaunchers();      // دالة لتجهيز أدوات اختيار الصور
        handleSystemBars();     // دالة لضبط هوامش الشاشة
    }

    /**
     * دالة ربط العناصر (Initialization):
     * تربط كل متغير بالـ ID الخاص به في ملف الـ XML.
     */
    private void initViews() {
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        tvLocation = findViewById(R.id.tvLocation);
        ivSelectedImage = findViewById(R.id.imgPreview);

        // عند النقر على أيقونة جلب الموقع
        findViewById(R.id.btnGetLocation).setOnClickListener(v -> fetchLocation());

        // عند النقر على مكان الصورة لفتح معرض الصور
        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));

        // عند النقر على زر "حفظ البلاغ"
        btnSubmit.setOnClickListener(v -> saveReportLogic());
    }

    /**
     * دالة جلب الموقع (Location Logic):
     * تطلب من نظام أندرويد الإحداثيات الحالية للمستخدم بدقة عالية.
     */
    private void fetchLocation() {
        // 1. التأكد من أن المستخدم منح التطبيق صلاحية الوصول للموقع
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // إذا لم تمنح، نطلبها الآن
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        tvLocation.setText("جاري جلب الموقع..."); // تحديث النص لإعلام المستخدم
        
        // 2. طلب الموقع "الحالي" بدقة عالية (High Accuracy)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // حفظ الإحداثيات في المتغيرات لاستخدامها لاحقاً عند الحفظ
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        tvLocation.setText("الموقع: " + latitude + " , " + longitude);
                    } else {
                        // في حال فشل الجلب (مثلاً GPS مغلق)
                        Toast.makeText(this, "تعذر جلب الموقع، تأكد من تشغيل GPS", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * دالة منطق الحفظ (Save Logic):
     * تتأكد من صحة البيانات ثم تحفظها في قاعدة البيانات المحلية والسحابية.
     */
    private void saveReportLogic() {
        String title = inputTitle.getText().toString().trim();
        String desc = inputDescription.getText().toString().trim();

        // فحص: هل الحقول فارغة؟
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        // إنشاء كائن المهمة وتعبئته بالبيانات المجمعة
        MyTask task = new MyTask();
        task.setTaskName(title);
        task.setTaskDescription(desc);
        task.setLatitude(latitude);
        task.setLongitude(longitude);
        if (selectedImageUri != null) {
            task.setImageUrl(selectedImageUri.toString());
        }

        // 1. الحفظ المحلي (Room Database):
        // نقوم بالحفظ في خيط منفصل (Thread) لعدم تجميد واجهة المستخدم
        new Thread(() -> {
            AppDatabase.getdb(this).getMyTaskQuery().insert(task);
            
            // العودة للخيط الرئيسي لتنفيذ المزامنة السحابية وإظهار النتائج
            runOnUiThread(() -> {
                syncWithFirebase(task);
            });
        }).start();
    }

    /**
     * دالة المزامنة السحابية (Firebase Sync):
     * ترفع البلاغ إلى قاعدة بيانات Firebase ليكون متاحاً لجميع المستخدمين.
     */
    private void syncWithFirebase(MyTask task) {
        // الحصول على مرجع فريد لكل بلاغ جديد في Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").push();
        task.setKid(ref.getKey()); // حفظ المعرف الفريد القادم من Firebase داخل الكائن

        // تنفيذ عملية الرفع
        ref.setValue(task).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "تم حفظ البلاغ ومزامنته سحابياً", Toast.LENGTH_SHORT).show();
            finish(); // إغلاق هذه الشاشة والرجوع للرئيسية بعد النجاح
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "فشل المزامنة السحابية: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * إعداد أدوات الاستجابة (Launchers):
     * تحدد ماذا يحدث عند اختيار صورة من المعرض.
     */
    private void setupLaunchers() {
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // حفظ مسار الصورة وعرضها في الواجهة فوراً
                selectedImageUri = uri;
                ivSelectedImage.setImageURI(uri);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * ضبط هوامش النظام (Handle System Bars):
     * تضمن أن المحتوى لن يختفي تحت شريط الحالة أو شريط التنقل السفلي.
     */
    private void handleSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
