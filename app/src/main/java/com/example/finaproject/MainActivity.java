package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // استيراد EditText
import android.widget.ImageView;
import android.widget.ProgressBar; // استيراد ProgressBar
import android.widget.TextView;
import android.widget.Toast; // استيراد Toast لعرض الأخطاء

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * حقل نصي لعرض إجابة Gemini.
     */

    /**
     * شريط تقدم يظهر أثناء انتظار الرد.
     */
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- تهيئة وعرض التقارير (الكود الأصلي) ---
        setupReportsUI();

        // --- تهيئة واجهة Gemini (الكود الجديد المضاف) ---
        setupGeminiUI();

        // --- إعداد هوامش النظام (الكود الأصلي) ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * دالة لتنظيم وتهيئة كل ما يتعلق بواجهة عرض التقارير.
     */
    private void setupReportsUI() {
        // ربط المتغيرات بعناصر الواجهة
        recyclerReports = findViewById(R.id.recyclerReports);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        inputSearchLayout = findViewById(R.id.inputSearchLayout);
        imgPreview = findViewById(R.id.imgPreview);
        btnAddReport = findViewById(R.id.btnAddReport);
        responseText = findViewById(R.id.responseText);

        // --- الكود المضاف لحل المشكلة ---
        // إضافة مستمع النقر على صورة الإعدادات لفتح شاشة الإعدادات
        imgPreview.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });

        // إعداد RecyclerView لعرض البيانات
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
        recyclerReports.setAdapter(myTaskAdapter);

        // إضافة مستمع للنقرات على الـ RecyclerView (الكود المصحح والمدمج)
        recyclerReports.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerReports, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 1. الحصول على قائمة البلاغات الحالية من الـ Adapter
                ArrayList<MyTask> currentTasks = myTaskAdapter.getTasksList();
                if (currentTasks != null && position >= 0 && position < currentTasks.size()) {
                    MyTask clickedTask = currentTasks.get(position);

                    // 2. إنشاء "نية" (Intent) للانتقال إلى شاشة التفاصيل
                    Intent intent = new Intent(MainActivity.this, ReportDetailsActivity.class);

                    // 3. وضع جميع بيانات البلاغ في الـ Intent
                    intent.putExtra("TASK_id", clickedTask.getId());
                    intent.putExtra("TITLE", clickedTask.getTaskTitle());
                    intent.putExtra("DESCRIPTION", clickedTask.getTaskDescription());
                    intent.putExtra("STATUS", clickedTask.getTaskStatus());
                    intent.putExtra("IMAGE_URL", clickedTask.getImageUrl());
                    intent.putExtra("LAT", clickedTask.getLatitude());
                    intent.putExtra("LON", clickedTask.getLongitude());

                    // 4. بدء تشغيل شاشة التفاصيل
                    startActivity(intent);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // يمكنك إضافة كود هنا إذا أردت تفعيل شيء عند الضغط المطول
            }
        }));

        // مستمع النقر لزر إضافة تقرير جديد
        btnAddReport.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewReporScreen.class);
            startActivity(intent);
        });
    }

    /**
     * دالة لتهيئة كل ما يتعلق بواجهة التفاعل مع Gemini.
     * ملاحظة: هذه العناصر يجب إضافتها إلى ملف activity_main.xml لكي يعمل هذا الكود.
     */
    private void setupGeminiUI() {
         //ربط المتغيرات بعناصر واجهة Gemini
        //** هام: الكود التالي سيعمل فقط بعد إضافة هذه العناصر (inputText, sendButton, etc.) إلى ملف XML **
         inputText = findViewById(R.id.inputText);
         sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);
         progressBar = findViewById(R.id.progressBar);

        // التحقق من أن الزر ليس null قبل إضافة المستمع (لتجنب الخطأ)
        if (sendButton != null) {
            // مستمع النقر لزر إرسال السؤال إلى Gemini
            sendButton.setOnClickListener(v -> {
                // التأكد من أن حقل الإدخال ليس null
                if (inputText != null) {
                    String query = inputText.getText().toString();
                    if (!query.isEmpty()) {
                        callGemini(query); // استدعاء دالة التواصل مع Gemini
                    } else {
                        Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * دالة لإرسال الطلب إلى Gemini ومعالجة الرد.
     * @param query السؤال المراد إرساله.
     */
    private void callGemini(String query) {
        // إظهار شريط التقدم وإخفاء النص القديم
        progressBar.setVisibility(View.VISIBLE);
        responseText.setText("");
        inputText.setText(""); // مسح حقل الإدخال بعد الإرسال

        // الحصول على نسخة من GeminiHelper وإرسال الطلب
        String prompt = PromptBuilder.buildReportPrompt(query);

        GeminiHelper.getInstance().sendMessage(prompt, new ResponseCallback()  {
            /**
             * يتم استدعاؤها عند وصول الرد بنجاح.
             */
            @Override
            public void onResponse(String response) {
                // تحديث واجهة المستخدم من خلال الـ Main Thread
                runOnUiThread(() -> {
                    responseText.setText(response);
                    progressBar.setVisibility(View.GONE);
                });
            }

            /**
             * يتم استدعاؤها عند حدوث خطأ.
             */
            @Override
            public void onError(Throwable throwable) {
                // تحديث واجهة المستخدم من خلال الـ Main Thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadTasks() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<MyTask> myTasksList = AppDatabase.getdb(getApplicationContext()).getMyTaskQuery().getAllTasks();
            ArrayList<MyTask> myTasks = new ArrayList<>(myTasksList);
            runOnUiThread(() -> {
                if (myTaskAdapter != null) {
                    myTaskAdapter.setTasksList(myTasks);
                    myTaskAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    /**
     * يتم استدعاؤها عند رجوع المستخدم إلى الشاشة.
     * تقوم بتحديث قائمة التقارير.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}
