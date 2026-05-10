package com.example.finaproject;

// --- استيراد المكتبات الأساسية لواجهات الأندرويد والخدمات ---
import android.content.BroadcastReceiver; // كلاس لاستقبال رسائل النظام العامة (مثل حالة الإنترنت أو وضع الطيران)
import android.content.Context; // كائن يوفر معلومات عن بيئة عمل التطبيق للوصول للمصادر
import android.content.Intent; // كائن يستخدم للانتقال بين شاشات التطبيق أو تشغيل المكونات
import android.content.IntentFilter; // كائن يحدد نوع الإشارات التي يريد مراقبتها
import android.graphics.Color; // مكتبة للتعامل مع الألوان وتغييرها برمجياً
import android.os.Bundle; // كائن يستخدم لحفظ وتمرير البيانات عند إنشاء النشاط (Activity)
import android.widget.Button; // عنصر الزر القابل للنقر في الواجهة
import android.widget.EditText; // حقل مخصص لإدخال النصوص من قبل المستخدم
import android.widget.TextView; // عنصر مخصص لعرض النصوص الثابتة للمستخدم
import android.widget.Toast; // أداة لإظهار رسائل تنبيهية صغيرة تختفي تلقائياً

// --- مكتبات الدعم والتوافقية والذكاء الاصطناعي ---
import androidx.activity.EdgeToEdge; // تفعيل ميزة العرض الشامل من الحافة إلى الحافة (ملء الشاشة)
import androidx.annotation.NonNull; // وسم للتأكد من أن المتغيرات الممررة ليست فارغة (null)
import androidx.appcompat.app.AppCompatActivity; // الكلاس الأساسي لجميع شاشات التطبيق لضمان التوافق مع الإصدارات القديمة
import androidx.core.content.ContextCompat; // فئة مساعدة للوصول للخدمات والألوان بشكل متوافق
import androidx.core.graphics.Insets; // كائن يمثل المسافات اللازمة لتجنب التداخل مع أشرطة النظام
import androidx.core.view.ViewCompat; // فئة مساعدة للتعامل مع خصائص العرض بشكل متوافق
import androidx.core.view.WindowInsetsCompat; // لإدارة هوامش النوافذ (مثل مكان الساعة وأزرار التنقل)
import androidx.recyclerview.widget.LinearLayoutManager; // لترتيب عناصر القائمة بشكل رأسي منظم
import androidx.recyclerview.widget.RecyclerView; // القائمة المتقدمة لعرض البيانات (قائمة البلاغات)

// --- مكتبات قاعدة البيانات والذكاء الاصطناعي من Firebase ---
import com.example.finaproject.data.MyTaskTable.MyTask; // استيراد كائن "المهمة" الذي يمثل بلاغ المستخدم
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter; // استيراد المحول المسؤول عن ملء القائمة بالبيانات
import com.google.common.util.concurrent.FutureCallback; // للتعامل مع نتائج العمليات المستقبلية (مثل نجاح طلب الـ AI)
import com.google.common.util.concurrent.Futures; // إدارة العمليات التي تعمل في الخلفية
import com.google.common.util.concurrent.ListenableFuture; // كائن يمثل "وعداً" باستلام نتيجة من الذكاء الاصطناعي لاحقاً
import com.google.firebase.vertexai.FirebaseVertexAI; // الكلاس الرئيسي للوصول لخدمات الذكاء الاصطناعي في Firebase
import com.google.firebase.vertexai.GenerativeModel; // الكلاس الذي يمثل نموذج الذكاء الاصطناعي (مثل Gemini)
import com.google.firebase.vertexai.java.GenerativeModelFutures; // جسر الربط ليعمل الذكاء الاصطناعي مع لغة الجافا بسلاسة
import com.google.firebase.vertexai.type.Content; // لتغليف وتجهيز النص المرسل للذكاء الاصطناعي
import com.google.firebase.vertexai.type.GenerateContentResponse; // لاستقبال ومعالجة رد الذكاء الاصطناعي
import com.google.firebase.database.DataSnapshot; // كائن يحتوي على "لقطة" من البيانات القادمة من Firebase
import com.google.firebase.database.DatabaseError; // لمعالجة أي أخطاء تحدث أثناء الاتصال بقاعدة البيانات السحابية
import com.google.firebase.database.FirebaseDatabase; // الكلاس الرئيسي للتعامل مع قاعدة بيانات Firebase Realtime
import com.google.firebase.database.ValueEventListener; // مستمع يراقب تغييرات البيانات في السحابة لحظياً

import java.util.ArrayList; // مصفوفة ديناميكية لتخزين قائمة البلاغات في ذاكرة الهاتف
import java.util.concurrent.Executor; // لإدارة تنفيذ الأكواد في "خيوط" (Threads) مختلفة

/**
 * شاشة MainActivity: هي الشاشة المركزية للتطبيق، تعرض البلاغات وتوفر ميزة الذكاء الاصطناعي.
 */
public class MainActivity extends AppCompatActivity {
    // --- تعريف عناصر الواجهة (Properties) ---
    private Button button11;
    // زر إرسال السؤال للذكاء الاصطناعي

    private EditText etTaskTopic;      // حقل كتابة المشكلة أو السؤال
    private TextView tvAiResponse;     // المكان الذي سيظهر فيه رد الذكاء الاصطناعي
    private Button btnAddReport;       // زر الانتقال لشاشة إضافة بلاغ جديد
    private MyTaskAdapter myTaskAdapter; // المحول الذي يربط بيانات البلاغات بشكلها في القائمة
    private GenerativeModelFutures model; // محرك الذكاء الاصطناعي (Vertex AI Gemini)

    // --- مستقبل البث لمراقبة وضع الطيران في الهاتف ---
    private final BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // التحقق إذا كان الحدث المستلم هو تغير وضع الطيران في النظام
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                // جلب الحالة الحالية (هل وضع الطيران مفعل؟)
                boolean isOn = intent.getBooleanExtra("state", false);
                if (btnAddReport != null) {
                    // تغيير لون زر الإضافة للون الأحمر لتنبيه المستخدم عند تفعيل وضع الطيران
                    btnAddReport.setBackgroundColor(isOn ? Color.RED : Color.parseColor("#2E7D32"));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تفعيل ميزة استغلال كامل مساحة الشاشة (تحت شريط الساعة)
        EdgeToEdge.enable(this);
        // ربط كود الجافا بملف التصميم XML الخاص بهذه الشاشة
        setContentView(R.layout.activity_main);

        // 1. تهيئة نموذج الذكاء الاصطناعي (Gemini 1.5 Flash)
        try {
            // جلب نسخة من الموديل عبر FirebaseVertexAI
            GenerativeModel gm = FirebaseVertexAI.getInstance().generativeModel("gemini-1.5-flash");
            // تحويل الموديل ليكون متوافقاً مع لغة الجافا (استخدام Futures)
            model = GenerativeModelFutures.from(gm);
        } catch (Exception e) {
            // إظهار رسالة في حال فشل الاتصال الأولي بالذكاء الاصطناعي
            Toast.makeText(this, "فشل تهيئة الذكاء الاصطناعي", Toast.LENGTH_SHORT).show();
        }

        // 2. استدعاء دوال التهيئة وجلب البيانات
        initViews();       // دالة لربط العناصر وبرمجة الأزرار
        getAllFromFirebase(); // دالة لجلب كافة البلاغات السابقة من السحابة

        // 3. ضبط هوامش الشاشة لضمان عدم تداخل المحتوى مع ساعة الهاتف وأشرطة النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * دالة initViews: تربط متغيرات الجافا بالعناصر المعرفة في XML وتبرمج الأزرار.
     */
    private void initViews() {
        button11 = findViewById(R.id.button11);
        etTaskTopic = findViewById(R.id.etTaskTopic);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        btnAddReport = findViewById(R.id.btnAddReport);
        RecyclerView rv = findViewById(R.id.recyclerReports);

        // برمجة زر السؤال للذكاء الاصطناعي
        if (button11 != null) {
            button11.setOnClickListener(v -> {
                // جلب النص الذي كتبه المستخدم وحذف الفراغات الزائدة
                String q = etTaskTopic.getText().toString().trim();
                if (!q.isEmpty()) {
                    // تعويض النص الخام بالبرومت المنظم مباشرة من كلاس PromptBuilder
                   // askAi(PromptBuilder.buildReportPrompt(q));
                    askAi(q);
                } else {
                    Toast.makeText(this, "يرجى كتابة سؤال أولاً", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // إعداد القائمة (RecyclerView)
        if (rv != null) {
            // تحديد شكل القائمة (خطي رأسي)
            rv.setLayoutManager(new LinearLayoutManager(this));
            // إنشاء المحول وتمرير قائمة فارغة له في البداية
            myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
            rv.setAdapter(myTaskAdapter);
        }

        // برمجة زر إضافة بلاغ جديد للانتقال لشاشة الإضافة
        if (btnAddReport != null) {
            btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
        }

        // برمجة أيقونة الإعدادات الموجودة في الواجهة
        findViewById(R.id.imgPreview).setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));
    }

    /**
     * دالة askAi: ترسل النص المكتوب إلى ذكاء Firebase الاصطناعي وتعرض النتيجة في الواجهة.
     */

    private void askAi(String prompt) {
        if (model == null) return;

        // 1. تعريف البرومبت الموحد (التعليمات الثابتة)
        // يمكنك تغيير النص بين علامات التنصيص حسب ما تريد
        String systemInstruction ="You are a smart assistant for city issue reporting.\n" +
                "\n" +
                "A user reported this issue: \"{problem}\".\n" +
                "\n" +
                "Provide:\n" +
                "- A brief description of the issue\n" +
                "- Step-by-step actions to resolve it\n" +
                "- The responsible authority\n" +
                "- Estimated urgency level (low, medium, high)\n" +
                "- Safety tips if relevant\n" +
                "\n" +
                "Make the response clear and structured." ;

        // 2. دمج التعليمات الثابتة مع سؤال المستخدم في متغير واحد
        String finalPrompt = systemInstruction + "\n\nسؤال المستخدم: " + prompt;

        // إظهار حالة "جاري التحميل" وتعطيل الزر مؤقتاً
        tvAiResponse.setText("جاري التفكير في بلاغك...");
        button11.setEnabled(false);

        // 3. إرسال النص المدمج (finalPrompt) بدلاً من (prompt)
        Content content = new Content.Builder().addText(finalPrompt).build();

        Executor executor = ContextCompat.getMainExecutor(this);
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    tvAiResponse.setText(result.getText());
                    button11.setEnabled(true);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                runOnUiThread(() -> {
                    tvAiResponse.setText("خطأ في الاتصال: " + t.getMessage());
                    button11.setEnabled(true);
                });
            }
        }, executor);
    }

    /**
     * دالة getAllFromFirebase: تجلب كافة البلاغات من قاعدة بيانات Firebase Realtime Database.
     */
    private void getAllFromFirebase() {
        // الوصول لفرع "tasks" في قاعدة البيانات ومراقبته باستمرار عند حدوث أي تغيير
        FirebaseDatabase.getInstance().getReference("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MyTask> list = new ArrayList<>();
                // قراءة كل بلاغ وتحويله من شكل Firebase إلى كائن MyTask وإضافته للمصفوفة
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MyTask t = ds.getValue(MyTask.class);
                    if (t != null) list.add(t);
                }
                // تحديث المحول (Adapter) بالبيانات الجديدة ليعرضها في القائمة فوراً
                if (myTaskAdapter != null) {
                    myTaskAdapter.setTasksList(list);
                    myTaskAdapter.notifyDataSetChanged(); // إبلاغ القائمة بضرورة إعادة الرسم
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // دالة يتم استدعاؤها في حال فشل جلب البيانات من السحابة
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تسجيل مراقب وضع الطيران ليعمل فور فتح التطبيق
        registerReceiver(airplaneReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // إلغاء تسجيل المراقب عند الخروج من التطبيق لتوفير موارد الهاتف ومنع الانهيار
        unregisterReceiver(airplaneReceiver);
    }
}
