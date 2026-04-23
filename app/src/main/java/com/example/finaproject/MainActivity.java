package com.example.finaproject; // تعريف مكان الملف داخل المجلدات

// استيراد المكتبات البرمجية اللازمة لعمل الواجهات والاتصال بالإنترنت وقاعدة البيانات
import android.annotation.SuppressLint;      // لتجاهل تنبيهات معينة من النظام
import android.content.BroadcastReceiver;    // لاستقبال إشارات البث من نظام أندرويد
import android.content.Context;              // للوصول إلى موارد التطبيق الأساسية
import android.content.Intent;               // للانتقال بين شاشات التطبيق المختلفة
import android.content.IntentFilter;         // لتحديد نوع الإشارات التي نريد استقبالها
import android.graphics.Color;                // للتحكم في الألوان برمجياً
import android.os.Bundle;                   // لحفظ ونقل حالة الشاشة
import android.view.View;                   // المكون الأساسي لجميع عناصر الواجهة
import android.widget.Button;               // عنصر الزر القابل للنقر
import android.widget.EditText;              // حقل إدخال النصوص
import android.widget.ImageView;             // عنصر عرض الصور
import android.widget.ProgressBar;          // شريط يوضح حالة التحميل
import android.widget.TextView;              // عنصر عرض النصوص الثابتة
import android.widget.Toast;                // لعرض رسائل سريعة للمستخدم

// استيراد مكتبات AndroidX لدعم الميزات الحديثة
import androidx.activity.EdgeToEdge;        // لتوسيع واجهة التطبيق لتشمل حواف الشاشة
import androidx.annotation.NonNull;           // للإشارة إلى أن القيمة لا يمكن أن تكون فارغة
import androidx.appcompat.app.AppCompatActivity; // الكلاس الأساسي للشاشات الحديثة
import androidx.core.graphics.Insets;         // للتعامل مع مسافات النظام (أشرطة الحالة)
import androidx.core.view.ViewCompat;          // لتوفير التوافق بين إصدارات أندرويد
import androidx.core.view.WindowInsetsCompat;   // للتحكم في هوامش النوافذ
import androidx.recyclerview.widget.LinearLayoutManager; // لترتيب القائمة بشكل طولي
import androidx.recyclerview.widget.RecyclerView;            // القائمة المتطورة لعرض البيانات

// استيراد كلاسات المشروع الداخلية (البيانات، قاعدة البيانات، والمساعدين)
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.GeminiHelper;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.example.finaproject.data.ResponseCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList; // قائمة ديناميكية لتخزين المهام

/**
 * الكلاس الرئيسي (MainActivity):
 * هو الشاشة المركزية التي تربط عرض البيانات بالسحابة وبالذكاء الاصطناعي.
 */
public class MainActivity extends AppCompatActivity {

    // --- تعريف المتغيرات البرمجية لعناصر الواجهة ---
    private TextView tvTitle;              // عنوان الشاشة (Reports)
    private TextView responseText;         // مكان عرض رد الذكاء الاصطناعي
    private ImageView imgPreview;          // أيقونة الإعدادات
    private Button btnAddReport;           // زر إضافة بلاغ جديد
    private RecyclerView recyclerReports;  // القائمة التي تعرض البلاغات
    private MyTaskAdapter myTaskAdapter;   // المحول المسؤول عن ملء القائمة بالبيانات

    private EditText inputText;            // حقل كتابة سؤال لـ Gemini
    private Button sendButton;             // زر إرسال السؤال
    private ProgressBar progressBar;       // دائرة التحميل عند انتظار رد Gemini

    /**
     * تعريف مستقبل البث (BroadcastReceiver):
     * وظيفته مراقبة "وضع الطيران" في الهاتف بشكل لحظي.
     */
    private final BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // التحقق إذا كان الحدث هو تغير وضع الطيران
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                // استخراج الحالة الجديدة (مفعل أم لا)
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if (isAirplaneModeOn) {
                    // إذا فُعل وضع الطيران، نغير لون الزر للأحمر كتنبيه
                    btnAddReport.setBackgroundColor(Color.RED);
                } else {
                    // إذا عُطل، نرجع للون الأخضر الأصلي
                    btnAddReport.setBackgroundColor(Color.parseColor("#2E7D32"));
                }
            }
        }
    };

    @SuppressLint("MissingInflatedId") // تجاهل تحذيرات المعرفات المفقودة أثناء البرمجة
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // استدعاء دالة الإنشاء الأساسية
        EdgeToEdge.enable(this);             // تفعيل خاصية ملء الشاشة بالكامل
        setContentView(R.layout.activity_main); // ربط الكلاس بملف التصميم XML

        // استدعاء الدوال الفرعية لتهيئة الأجزاء المختلفة
        setupReportsUI();      // تهيئة القائمة وأزرار التقارير
        setupGeminiUI();       // تهيئة واجهة الذكاء الاصطناعي
        getAllFromFirebase();  // بدء جلب البيانات من الإنترنت (Firebase)

        // ضبط هوامش الشاشة لتفادي التداخل مع شريط النظام العلوي والسفلي
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * دالة تهيئة واجهة التقارير (UI Setup):
     * تربط الأزرار والقوائم بالكود وتحدد ماذا يحدث عند النقر عليها.
     */
    private void setupReportsUI() {
        recyclerReports = findViewById(R.id.recyclerReports); // ربط القائمة
        imgPreview = findViewById(R.id.imgPreview);           // ربط أيقونة الإعدادات
        btnAddReport = findViewById(R.id.btnAddReport);       // ربط زر الإضافة

        // عند النقر على أيقونة الإعدادات، انتقل لشاشة Settings
        imgPreview.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, Settings.class));
        });

        // إعداد القائمة لتكون مرتبة رأسياً (Vertical)
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        // إنشاء المحول (Adapter) وربطه بالقائمة
        myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
        recyclerReports.setAdapter(myTaskAdapter);

        // إضافة مستمع للنقر على أي بلاغ داخل القائمة للانتقال لصفحة التفاصيل
        recyclerReports.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerReports, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // الحصول على بيانات البلاغ الذي تم النقر عليه
                ArrayList<MyTask> currentTasks = myTaskAdapter.getTasksList();
                if (currentTasks != null && position < currentTasks.size()) {
                    MyTask clickedTask = currentTasks.get(position);
                    // إنشاء نية انتقال (Intent) وحمل بيانات البلاغ معه
                    Intent intent = new Intent(MainActivity.this, ReportDetailsActivity.class);
                    intent.putExtra("TASK_EXTRA", clickedTask); // إرسال الكائن كاملاً
                    startActivity(intent);
                }
            }
            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        // عند النقر على زر "إضافة بلاغ"، انتقل لشاشة الإضافة
        btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
    }

    /**
     * دالة جلب البيانات من Firebase:
     * وظيفتها مراقبة قاعدة البيانات السحابية وتحديث القائمة فوراً عند إضافة أي بلاغ.
     */
    private void getAllFromFirebase() {
        FirebaseDatabase.getInstance().getReference("tasks") // الوصول لعقدة المهام في Firebase
            .addValueEventListener(new ValueEventListener() { // إضافة مستمع للتغيرات اللحظية
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<MyTask> tasks = new ArrayList<>();
                    // المرور على كل البلاغات الموجودة في السحابة وتحويلها لكائنات برمجية
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        MyTask task = postSnapshot.getValue(MyTask.class);
                        if (task != null) tasks.add(task);
                    }
                    // تحديث القائمة بالبيانات الجديدة
                    myTaskAdapter.setTasksList(tasks);
                    myTaskAdapter.notifyDataSetChanged(); // إخبار الواجهة بإعادة الرسم
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // عرض رسالة في حال فشل الاتصال بالإنترنت أو حدوث خطأ
                    Toast.makeText(MainActivity.this, "حدث خطأ في جلب البيانات", Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * تهيئة واجهة الذكاء الاصطناعي (Gemini):
     * تربط حقل الإدخال وزر الإرسال بمنطق التعامل مع Gemini API.
     */
    private void setupGeminiUI() {
        inputText = findViewById(R.id.inputText);   // ربط حقل النص
        sendButton = findViewById(R.id.sendButton);   // ربط زر الإرسال
        responseText = findViewById(R.id.responseText); // ربط مكان الرد
        progressBar = findViewById(R.id.progressBar);   // ربط مؤشر التحميل

        sendButton.setOnClickListener(v -> {
            String query = inputText.getText().toString(); // الحصول على سؤال المستخدم
            if (!query.isEmpty()) {
                callGemini(query); // استدعاء دالة التواصل مع الذكاء الاصطناعي
            }
        });
    }

    /**
     * دالة الاتصال بـ Gemini:
     * ترسل السؤال وتستقبل الإجابة وتعرضها.
     */
    private void callGemini(String query) {
        progressBar.setVisibility(View.VISIBLE); // إظهار مؤشر التحميل
        // استخدام المساعد GeminiHelper لإرسال الرسالة
        GeminiHelper.getInstance().sendMessage(PromptBuilder.buildReportPrompt(query), new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                // عند استلام الرد، نعرضه في الواجهة من خلال الخيط الرئيسي (Main Thread)
                runOnUiThread(() -> {
                    responseText.setText(response);
                    progressBar.setVisibility(View.GONE); // إخفاء مؤشر التحميل
                });
            }

            @Override
            public void onError(Throwable throwable) {
                // في حال حدوث خطأ في الاتصال
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "خطأ في الذكاء الاصطناعي", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تفعيل مراقب وضع الطيران فور عودة المستخدم للتطبيق
        registerReceiver(airplaneReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // إيقاف المراقب عند خروج المستخدم من التطبيق لتوفير موارد الهاتف والبطارية
        unregisterReceiver(airplaneReceiver);
    }
}
