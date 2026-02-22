package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // استيراد EditText
import android.widget.ImageView;
import android.widget.ProgressBar; // استيراد ProgressBar
import android.widget.TextView;
import android.widget.Toast; // استيراد Toast لعرض الأخطاء

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.GeminiHelper; // استيراد GeminiHelper
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.example.finaproject.data.ResponseCallback; // استيراد ResponseCallback
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * الشاشة الرئيسية للتطبيق (MainActivity).
 * مسؤولة عن عرض قائمة التقارير، والتفاعل مع Gemini، والتنقل بين الشاشات المختلفة.
 */
public class MainActivity extends AppCompatActivity {

    // --- متغيرات واجهة عرض التقارير (الكود الأصلي) ---
    private TextView tvTitle;
    private TextView responseText;
    private ImageView imgPreview;
    private TextView tvSubtitle;
    private TextInputLayout inputSearchLayout;
    private TextInputEditText inputSearch;
    private Button btnAddReport;
    private RecyclerView recyclerReports;
    private MyTaskAdapter myTaskAdapter;

    // --- متغيرات واجهة Gemini (الكود الجديد المضاف) ---
    /**
     * حقل إدخال النص لإرسال سؤال إلى Gemini.
     */
    private EditText inputText;
    /**
     * زر لإرسال السؤال.
     */
    private Button sendButton;

    /**
     * شريط تقدم يظهر أثناء انتظار الرد.
     */
    private ProgressBar progressBar;

    /**
     * دالة `onCreate` هي نقطة انطلاق النشاط (Activity).
     * يتم استدعاؤها عند إنشاء الشاشة لأول مرة.
     * @param savedInstanceState بيانات محفوظة من حالة سابقة للنشاط إذا كانت متاحة.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // تفعيل وضع العرض من الحافة إلى الحافة
        setContentView(R.layout.activity_main); // ربط النشاط بملف التصميم الخاص به

        // --- تهيئة وعرض التقارير (الكود الأصلي) ---
        setupReportsUI();

        // --- تهيئة واجهة Gemini (الكود الجديد المضاف) ---
        setupGeminiUI();

        // --- جلب البيانات من Firebase عند بدء التشغيل ---
        getAllFromFirebase();

        // --- إعداد هوامش النظام لتجنب تداخل واجهة المستخدم مع أشرطة النظام (مثل شريط الحالة) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * دالة لتنظيم وتهيئة كل ما يتعلق بواجهة عرض التقارير.
     * تقوم بربط متغيرات الكود بعناصر الواجهة الرسومية وإعداد المستمعين (Listeners).
     */
    private void setupReportsUI() {
        // ربط المتغيرات بعناصر الواجهة من خلال الـ ID الخاص بها في ملف التصميم
        recyclerReports = findViewById(R.id.recyclerReports);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        inputSearchLayout = findViewById(R.id.inputSearchLayout);
        imgPreview = findViewById(R.id.imgPreview);
        btnAddReport = findViewById(R.id.btnAddReport);
        responseText = findViewById(R.id.responseText);

        // إضافة مستمع النقر على صورة الإعدادات (imgPreview) لفتح شاشة الإعدادات
        imgPreview.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });

        // إعداد RecyclerView (قائمة التقارير)
        recyclerReports.setLayoutManager(new LinearLayoutManager(this)); // تحديد طريقة عرض العناصر (عموديًا)
        myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>()); // إنشاء Adapter جديد بقائمة فارغة مبدئيًا
        recyclerReports.setAdapter(myTaskAdapter); // ربط الـ RecyclerView بالـ Adapter

        // إضافة مستمع للنقرات على عناصر الـ RecyclerView
        recyclerReports.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerReports, new RecyclerItemClickListener.OnItemClickListener() {
            /**
             * يتم استدعاؤها عند النقر على أي عنصر في القائمة.
             * @param view العنصر الذي تم النقر عليه.
             * @param position موقع العنصر في القائمة.
             */
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<MyTask> currentTasks = myTaskAdapter.getTasksList();
                if (currentTasks != null && position >= 0 && position < currentTasks.size()) {
                    MyTask clickedTask = currentTasks.get(position); // الحصول على الكائن المقابل للعنصر المنقور

                    // إنشاء Intent للانتقال إلى شاشة تفاصيل التقرير
                    Intent intent = new Intent(MainActivity.this, ReportDetailsActivity.class);

                    // إرفاق بيانات التقرير مع الـ Intent ليتم عرضها في الشاشة التالية
                    intent.putExtra("TASK_id", clickedTask.getId());
                    intent.putExtra("TITLE", clickedTask.getTaskTitle());
                    intent.putExtra("DESCRIPTION", clickedTask.getTaskDescription());
                    intent.putExtra("STATUS", clickedTask.getTaskStatus());
                    intent.putExtra("IMAGE_URL", clickedTask.getImageUrl());
                    intent.putExtra("LAT", clickedTask.getLatitude());
                    intent.putExtra("LON", clickedTask.getLongitude());

                    startActivity(intent); // بدء النشاط الجديد
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // يمكنك إضافة كود هنا إذا أردت تفعيل شيء عند الضغط المطول
            }
        }));

        // مستمع النقر لزر إضافة تقرير جديد
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, NewReporScreen.class);
                    startActivity(intent);
                }

        });
    }

    /**
     * دالة لجلب جميع البيانات من Firebase Realtime Database وتحديث القائمة.
     * تستخدم `addValueEventListener` للاستماع للتغييرات بشكل حي.
     */
    private void getAllFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(); // الحصول على نسخة من قاعدة البيانات
        DatabaseReference myRef = database.getReference("tasks"); // الإشارة إلى عقدة "tasks"

        // إضافة مستمع للإصغاء لأي تغيير يحدث في البيانات تحت عقدة "tasks"
        myRef.addValueEventListener(new ValueEventListener() {
            /**
             * يتم استدعاؤها مرة واحدة عند بدء التشغيل، وكلما تغيرت البيانات.
             * @param snapshot نسخة من البيانات الحالية.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MyTask> tasks = new ArrayList<>(); // إنشاء قائمة جديدة لتخزين المهام
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MyTask task = postSnapshot.getValue(MyTask.class); // تحويل كل عنصر إلى كائن MyTask
                    if (task != null) {

                        // يفترض الآن أن حقل الـ id الرقمي يتم جلبه مباشرة من بيانات Firebase
                        tasks.add(task); // إضافة المهمة إلى القائمة
                    }
                }

                // تحديث الـ adapter بالبيانات الجديدة
                if (myTaskAdapter != null) {
                    myTaskAdapter.setTasksList(tasks); // تمرير القائمة الجديدة للـ Adapter
                    myTaskAdapter.notifyDataSetChanged(); // إعلام الـ Adapter بأن البيانات قد تغيرت ليقوم بتحديث العرض
                }
            }

            /**
             * يتم استدعاؤها في حال فشل عملية قراءة البيانات.
             * @param error خطأ يصف سبب الفشل.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // عرض رسالة خطأ للمستخدم
                Toast.makeText(MainActivity.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * دالة لتهيئة كل ما يتعلق بواجهة التفاعل مع Gemini.
     */
    private void setupGeminiUI() {
        //ربط المتغيرات بعناصر واجهة Gemini
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);
        progressBar = findViewById(R.id.progressBar);

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                if (inputText != null) {
                    String query = inputText.getText().toString();
                    if (!query.isEmpty()) {
                        callGemini(query); // استدعاء دالة إرسال الطلب إذا كان حقل الإدخال غير فارغ
                    } else {
                        Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * دالة لإرسال الطلب إلى Gemini API ومعالجة الرد.
     * @param query السؤال المراد إرساله.
     */
    private void callGemini(String query) {
        progressBar.setVisibility(View.VISIBLE); // إظهار شريط التقدم
        responseText.setText(""); // مسح النص القديم
        inputText.setText(""); // مسح حقل الإدخال

        // بناء نص الطلب (prompt)
        String prompt = PromptBuilder.buildReportPrompt(query);

        // إرسال الطلب بشكل غير متزامن
        GeminiHelper.getInstance().sendMessage(prompt, new ResponseCallback() {
            /**
             * يتم استدعاؤها عند وصول الرد بنجاح.
             * @param response النص المستلم من Gemini.
             */
            @Override
            public void onResponse(String response) {
                // تحديث واجهة المستخدم من خلال الـ Main Thread
                runOnUiThread(() -> {
                    responseText.setText(response); // عرض الرد
                    progressBar.setVisibility(View.GONE); // إخفاء شريط التقدم
                });
            }

            /**
             * يتم استدعاؤها في حال حدوث خطأ أثناء الطلب.
             * @param throwable الخطأ الذي حدث.
             */
            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE); // إخفاء شريط التقدم
                    Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * دالة لجلب المهام من قاعدة البيانات المحلية (Room). لم تعد مستخدمة حالياً.
     * تعمل في خيط منفصل (background thread) لتجنب تجميد الواجهة.
     */
    private void loadTasks() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // جلب البيانات من قاعدة البيانات
            List<MyTask> myTasksList = AppDatabase.getdb(getApplicationContext()).getMyTaskQuery().getAllTasks();
            ArrayList<MyTask> myTasks = new ArrayList<>(myTasksList);
            // تحديث واجهة المستخدم في الخيط الرئيسي
            runOnUiThread(() -> {
                if (myTaskAdapter != null) {
                    myTaskAdapter.setTasksList(myTasks);
                    myTaskAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    /**
     * يتم استدعاؤها عند رجوع المستخدم إلى الشاشة أو عندما تصبح في الواجهة.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // loadTasks(); // تم تعطيل هذا السطر لأننا الآن نستخدم Firebase لجلب البيانات بشكل تلقائي عبر المستمع (listener)
    }
}
