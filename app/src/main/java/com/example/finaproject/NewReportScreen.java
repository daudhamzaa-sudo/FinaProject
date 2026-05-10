package com.example.finaproject;

// --- استيراد المكتبات الأساسية (Imports) ---
import android.Manifest; // للوصول لتعريفات صلاحيات النظام (مثل الموقع)
import android.annotation.SuppressLint; // لتجاهل تنبيهات معينة من المحرر (مثل المنبه الدقيق)
import android.app.AlarmManager; // الكلاس المسؤول عن جدولة المنبهات في الأندرويد
import android.app.DatePickerDialog; // نافذة منبثقة لاختيار التاريخ (يوم/شهر/سنة)
import android.app.PendingIntent; // "نية مؤجلة": تصريح للنظام بتنفيذ كود لاحقاً حتى لو أُغلق التطبيق
import android.app.TimePickerDialog; // نافذة منبثقة لاختيار الوقت (ساعة/دقيقة)
import android.content.Context; // سياق التطبيق للوصول للخدمات (مثل مدير المنبهات)
import android.content.Intent; // للانتقال بين الشاشات أو تشغيل المستقبلات
import android.content.pm.PackageManager; // لفحص هل الصلاحيات ممنوحة من قبل المستخدم أم لا
import android.net.Uri; // للتعامل مع مسارات الملفات (مثل رابط الصورة المختار)
import android.os.Build; // لفحص إصدار أندرويد لضمان التوافقية
import android.os.Bundle; // لتمرير البيانات عند إنشاء الشاشة
import android.view.View; // الفئة الأساسية لجميع عناصر واجهة المستخدم
import android.widget.Button; // لعنصر الزر التقليدي
import android.widget.ImageView; // لعنصر عرض الصور
import android.widget.TextView; // لعنصر عرض النصوص
import android.widget.Toast; // لعرض رسائل قصيرة تظهر وتختفي تلقائياً

// --- مكتبات الدعم والتوافقية (AndroidX) ---
import androidx.activity.EdgeToEdge; // لجعل التصميم يمتد تحت شريط الحالة (ملء الشاشة)
import androidx.activity.result.ActivityResultLauncher; // لاستقبال نتائج من تطبيقات أخرى (مثل الصور)
import androidx.activity.result.contract.ActivityResultContracts; // عقود جاهزة للتعامل مع نتائج النظام
import androidx.annotation.NonNull; // وسم للتأكد أن القيم الممررة ليست فارغة
import androidx.appcompat.app.AppCompatActivity; // الفئة الأساسية للشاشات الحديثة والمتوافقة
import androidx.core.app.ActivityCompat; // فئة مساعدة للتعامل مع الصلاحيات بشكل آمن
import androidx.core.content.ContextCompat; // للوصول للموارد والخدمات بشكل موحد
import androidx.core.graphics.Insets; // للتعامل مع مسافات وهوامش الشاشة
import androidx.core.view.ViewCompat; // للتعامل مع خصائص العرض بشكل متوافق
import androidx.core.view.WindowInsetsCompat; // لإدارة هوامش النوافذ (مثل أماكن النوتش والساعة)

// --- مكتبات قاعدة البيانات والخدمات ---
import com.example.finaproject.data.AppDatabase; // الوصول لقاعدة بيانات Room المحلية
import com.example.finaproject.data.MyTaskTable.MyTask; // الكيان (Entity) الذي يمثل البلاغ الواحد
import com.google.android.gms.location.FusedLocationProviderClient; // مكتبة جوجل لجلب إحداثيات الموقع (GPS)
import com.google.android.gms.location.LocationServices; // تهيئة خدمات الموقع الجغرافي
import com.google.android.gms.location.Priority; // لتحديد دقة تحديد الموقع المطلوبة
import com.google.android.gms.tasks.CancellationTokenSource; // لإلغاء طلب الموقع إذا استغرق وقتاً طويلاً
import com.google.android.material.button.MaterialButton; // زر بتصميم حديث من جوجل
import com.google.android.material.textfield.TextInputEditText; // حقل إدخال نص متطور
import com.google.firebase.database.DatabaseReference; // مرجع يشير لعقدة في Firebase
import com.google.firebase.database.FirebaseDatabase; // الكلاس الرئيسي لـ Firebase Database

import java.util.Calendar; // للتعامل مع العمليات الحسابية للوقت والتاريخ

/**
 * شاشة NewReportScreen: مسؤولة عن جمع بيانات البلاغ الجديد (عنوان، وصف، صورة، موقع، وتذكير).
 */
public class NewReportScreen extends AppCompatActivity {

    // --- تعريف عناصر الواجهة ---
    private ImageView ivSelectedImage;       // لعرض صورة المعاينة المختار
    private Uri selectedImageUri;             // لتخزين مسار الصورة المختار
    private TextInputEditText inputTitle;     // حقل عنوان البلاغ
    private TextInputEditText inputDescription; // حقل وصف المشكلة
    private FusedLocationProviderClient fusedLocationClient; // محرك جلب الموقع (GPS)
    private TextView tvLocation;              // نص لعرض الإحداثيات للمستخدم
    private double latitude = 0;              // متغير لتخزين خط العرض
    private double longitude = 0;             // متغير لتخزين خط الطول
    private MaterialButton btnSubmit;         // زر الحفظ النهائي والمزامنة
    private Button btnSetReminder;            // زر لاختيار وقت التذكير
    private TextView tvReminderTime;          // نص يعرض وقت التنبيه المختار
    private long selectedReminderTime = 0;    // وقت المنبه بالملي ثانية

    // --- لاقطات النتائج (Launchers) ---
    private ActivityResultLauncher<String> pickImage; // لفتح الاستوديو واختيار صورة
    
    // لاقط لطلب صلاحية الإشعارات (إلزامي في أندرويد 13 فما فوق)
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher = 
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) Toast.makeText(this, "لن تتلقى تذكيرات بسبب رفض الصلاحية", Toast.LENGTH_SHORT).show();
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // تفعيل ملء الشاشة
        setContentView(R.layout.activity_new_repor_screen); // ربط التصميم
        
        // 1. طلب صلاحية الإشعارات لأندرويد 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // 2. تهيئة محرك الموقع من جوجل
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();       // ربط العناصر
        setupLaunchers();  // إعداد لاقط الصور
        handleSystemBars(); // ضبط هوامش النظام
    }

    private void initViews() {
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        tvLocation = findViewById(R.id.tvLocation);
        ivSelectedImage = findViewById(R.id.imgPreview);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        tvReminderTime = findViewById(R.id.tvReminderTime);

        // برمجة الأزرار
        btnSetReminder.setOnClickListener(v -> showDateTimePicker());
        findViewById(R.id.btnGetLocation).setOnClickListener(v -> fetchLocation());
        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSubmit.setOnClickListener(v -> saveReportLogic());
    }

    private void fetchLocation() {
        // فحص صلاحية الموقع
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        tvLocation.setText("جاري جلب الموقع...");
        // طلب الموقع الحالي بدقة عالية
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
            .addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tvLocation.setText("الموقع: " + latitude + " , " + longitude);
                } else {
                    Toast.makeText(this, "تأكد من تشغيل الـ GPS", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        Calendar date = Calendar.getInstance();

        // اختيار التاريخ ثم الوقت
        new DatePickerDialog(this, (view, year, month, day) -> {
            date.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hour);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);
                selectedReminderTime = date.getTimeInMillis();
                tvReminderTime.setText(date.getTime().toString());
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveReportLogic() {
        String title = inputTitle.getText().toString().trim();
        String desc = inputDescription.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "يرجى ملء الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        MyTask task = new MyTask();
        task.setTaskName(title);
        task.setTaskDescription(desc);
        task.setLatitude(latitude);
        task.setLongitude(longitude);
        task.setReminderTime(selectedReminderTime);
        if (selectedImageUri != null) task.setImageUrl(selectedImageUri.toString());

        // الحفظ في خيط خلفي لمنع تعليق التطبيق
        new Thread(() -> {
            // الحفظ المحلي أولاً للحصول على ID للمنبه
            long id = AppDatabase.getdb(this).getMyTaskQuery().insert(task);
            task.setId(id);
            
            runOnUiThread(() -> {
                // جدولة المنبه إذا كان الوقت مستقبلياً
                if (selectedReminderTime > System.currentTimeMillis()) {
                    scheduleAlarm(task);
                }
                // المزامنة مع Firebase
                syncWithFirebase(task);
            });
        }).start();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlarm(MyTask task) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskReminderReceiver.class);
        intent.putExtra("title", task.getTaskName());
        intent.putExtra("text", task.getTaskDescription());

        // إنشاء PendingIntent لفتح الإشعار لاحقاً
        PendingIntent pi = PendingIntent.getBroadcast(
                this, (int) task.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (am != null) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pi);
        }
    }

    private void syncWithFirebase(MyTask task) {
        // رفع البيانات لـ Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").push();
        task.setKid(ref.getKey());
        ref.setValue(task).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "تم الحفظ والمزامنة!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupLaunchers() {
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                ivSelectedImage.setImageURI(uri);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleSystemBars() {
        View v = findViewById(R.id.main);
        if (v != null) {
            ViewCompat.setOnApplyWindowInsetsListener(v, (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}
