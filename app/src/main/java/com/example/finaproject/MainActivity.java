package com.example.finaproject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.GeminiHelper;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.example.finaproject.data.ResponseCallback;
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
 * الشاشة الرئيسية (MainActivity):
 * الوظيفة: عرض قائمة البلاغات، البحث، والتفاعل مع الذكاء الاصطناعي (Gemini).
 */
public class MainActivity extends AppCompatActivity {

    // عناصر الواجهة
    private TextView tvTitle;
    private TextView responseText; // لعرض رد الذكاء الاصطناعي
    private ImageView imgPreview;  // أيقونة الإعدادات
    private Button btnAddReport;   // زر الانتقال لشاشة الإضافة
    private RecyclerView recyclerReports; // القائمة الذكية لعرض البيانات
    private MyTaskAdapter myTaskAdapter;  // الوسيط بين البيانات والواجهة

    private EditText inputText;    // حقل سؤال الذكاء الاصطناعي
    private Button sendButton;     // زر الإرسال لـ Gemini
    private ProgressBar progressBar; // مؤشر الانتظار

    /**
     * مُستقبل البث (BroadcastReceiver):
     * الوظيفة: مراقبة حالة "وضع الطيران" في الهاتف لحظياً.
     * لماذا: لتنبيه المستخدم عند انقطاع الاتصال بالسحابة عن طريق تغيير لون زر الإضافة.
     */
    private final BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if (isAirplaneModeOn) {
                    btnAddReport.setBackgroundColor(Color.RED); // تنبيه: انقطاع المزامنة
                } else {
                    btnAddReport.setBackgroundColor(Color.parseColor("#2E7D32")); // الحالة طبيعية
                }
            }
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // تفعيل العرض ملء الشاشة
        setContentView(R.layout.activity_main);

        setupReportsUI(); // تهيئة واجهة التقارير
        setupGeminiUI(); // تهيئة واجهة الذكاء الاصطناعي
        getAllFromFirebase(); // جلب البيانات الحية من السحابة

        // معالجة هوامش النظام (شريط الحالة والتنقل) لضمان عدم تداخل العناصر
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * تهيئة القائمة (RecyclerView):
     * لماذا: نستخدم RecyclerView لأنه فعال جداً في عرض القوائم الطويلة ويستهلك ذاكرة قليلة.
     */
    private void setupReportsUI() {
        recyclerReports = findViewById(R.id.recyclerReports);
        imgPreview = findViewById(R.id.imgPreview);
        btnAddReport = findViewById(R.id.btnAddReport);

        imgPreview.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });

        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
        recyclerReports.setAdapter(myTaskAdapter);

        // مستمع للنقر على عناصر القائمة للانتقال للتفاصيل
        recyclerReports.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerReports, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<MyTask> currentTasks = myTaskAdapter.getTasksList();
                if (currentTasks != null && position < currentTasks.size()) {
                    MyTask clickedTask = currentTasks.get(position);
                    Intent intent = new Intent(MainActivity.this, ReportDetailsActivity.class);
                    // تمرير البيانات للشاشة التالية
                    intent.putExtra("TASK_id", clickedTask.getId());
                    intent.putExtra("TITLE", clickedTask.getTaskTitle());
                    intent.putExtra("DESCRIPTION", clickedTask.getTaskDescription());
                    intent.putExtra("IMAGE_URL", clickedTask.getImageUrl());
                    startActivity(intent);
                }
            }
            @Override
            public void onLongItemClick(View view, int position) {}
        }));

        btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
    }

    /**
     * جلب البيانات من Firebase:
     * لماذا: لضمان مزامنة التقارير بين جميع الأجهزة فور حدوث أي تغيير.
     */
    private void getAllFromFirebase() {
        FirebaseDatabase.getInstance().getReference("tasks")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<MyTask> tasks = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        MyTask task = postSnapshot.getValue(MyTask.class);
                        if (task != null) tasks.add(task);
                    }
                    myTaskAdapter.setTasksList(tasks);
                    myTaskAdapter.notifyDataSetChanged(); // تحديث الواجهة فوراً
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "فشل جلب البيانات السحابية", Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * تهيئة Gemini AI:
     * الوظيفة: ربط حقل الإدخال بمساعد جوجل الذكي.
     */
    private void setupGeminiUI() {
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);
        progressBar = findViewById(R.id.progressBar);

        sendButton.setOnClickListener(v -> {
            String query = inputText.getText().toString();
            if (!query.isEmpty()) callGemini(query);
        });
    }

    private void callGemini(String query) {
        progressBar.setVisibility(View.VISIBLE);
        GeminiHelper.getInstance().sendMessage(PromptBuilder.buildReportPrompt(query), new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> {
                    responseText.setText(response);
                    progressBar.setVisibility(View.GONE);
                });
            }
            @Override
            public void onError(Throwable t) {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تسجيل مراقب وضع الطيران عند العودة للتطبيق
        registerReceiver(airplaneReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // إلغاء التسجيل لتوفير البطارية عند الخروج
        unregisterReceiver(airplaneReceiver);
    }
}
