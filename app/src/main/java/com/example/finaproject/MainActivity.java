package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // استيراد EditText
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

public class MainActivity extends AppCompatActivity {

    // --- متغيرات واجهة عرض التقارير (الكود الأصلي) ---
    private TextView tvTitle;

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
    private TextView responseText;
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
        inputSearch = findViewById(R.id.inputSearch);
        btnAddReport = findViewById(R.id.btnAddReport);
        responseText = findViewById(R.id.responseText);
        // إعداد RecyclerView لعرض البيانات
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<MyTask> myTasks = (ArrayList<MyTask>) AppDatabase.getdb(this).getMyTaskQuery().getAllTasks();
        myTaskAdapter = new MyTaskAdapter(this, myTasks);
        recyclerReports.setAdapter(myTaskAdapter);

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
        GeminiHelper.getInstance().sendMessage(query, new ResponseCallback() {
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

    /**
     * يتم استدعاؤها عند رجوع المستخدم إلى الشاشة.
     * تقوم بتحديث قائمة التقارير.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // التحقق من أن myTaskAdapter ليس null قبل استخدامه
        if (myTaskAdapter != null) {
            ArrayList<MyTask> myTasks = (ArrayList<MyTask>) AppDatabase.getdb(this).getMyTaskQuery().getAllTasks();
            myTaskAdapter.setTasksList(myTasks);
            myTaskAdapter.notifyDataSetChanged();
        }
    }
}
