package com.example.finaproject;

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

import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * MainActivity: الشاشة الرئيسية للتطبيق بعد إزالة ميزات الذكاء الاصطناعي.
 * تركز الشاشة الآن على عرض قائمة البلاغات وإدارة الإعدادات.
 */
public class MainActivity extends AppCompatActivity {
private Button button11;
private EditText etTaskTopic;
private TextView tvAiResponse;
    private Button btnAddReport; // زر إضافة بلاغ جديد
    private RecyclerView recyclerReports; // القائمة لعرض البلاغات
    private MyTaskAdapter myTaskAdapter; // محول القائمة
    private ImageView imgPreview; // أيقونة الإعدادات

    // مستقبل البث لمراقبة وضع الطيران
    private final BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if (btnAddReport != null) {
                    // تغيير لون الزر للتنبيه عند تفعيل وضع الطيران
                    btnAddReport.setBackgroundColor(isAirplaneModeOn ? Color.RED : Color.parseColor("#2E7D32"));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // تهيئة عناصر الواجهة
        initViews();

        // ضبط هوامش النظام
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // جلب البيانات من Firebase
        getAllFromFirebase();
    }

    /**
     * تهيئة العناصر وربط الأزرار.
     */
    private void initViews() {
        btnAddReport = findViewById(R.id.btnAddReport);
        recyclerReports = findViewById(R.id.recyclerReports);
        imgPreview = findViewById(R.id.imgPreview);
        button11= findViewById(R.id.button11);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        etTaskTopic = findViewById(R.id.etTaskTopic);

        // إعداد القائمة
        if (recyclerReports != null) {
            recyclerReports.setLayoutManager(new LinearLayoutManager(this));
            myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
            recyclerReports.setAdapter(myTaskAdapter);
            
            // إضافة مستمع للنقر على البلاغات
            recyclerReports.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerReports, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ArrayList<MyTask> currentTasks = myTaskAdapter.getTasksList();
                    if (currentTasks != null && position < currentTasks.size()) {
                        MyTask clickedTask = currentTasks.get(position);
                        Intent intent = new Intent(MainActivity.this, ReportDetailsActivity.class);
                        intent.putExtra("TASK_EXTRA", clickedTask);
                        startActivity(intent);
                    }
                }
                @Override public void onLongItemClick(View view, int position) {}
            }));
        }

        // زر إضافة بلاغ جديد
        if (btnAddReport != null) {
            btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
        }

        // أيقونة الإعدادات
        if (imgPreview != null) {
            imgPreview.setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));
        }
    }
    ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
            .generativeModel("gemini-3-flash-preview");
    model = GenerativeModelFutures.from(ai);
    private void askFirebaseAiGeminiForSteps(String topic) {
        pbLoading.setVisibility(View.VISIBLE);
        tvAiResponse.setText("");
        btnSuggestSteps.setEnabled(false);


        String promptStr = "I want to perform the following task: '" + topic + "'. " +
                "Can you suggest a clear, step-by-step checklist to complete this task effectively?";


        Content prompt = new Content.Builder()
                .addText(promptStr)
                .build();


        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Executor executor = this::runOnUiThread;
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                pbLoading.setVisibility(View.GONE);
                btnSuggestSteps.setEnabled(true);
                tvAiResponse.setText(result.getText());
            }


            @Override
            public void onFailure(Throwable t) {
                pbLoading.setVisibility(View.GONE);
                btnSuggestSteps.setEnabled(true);
                Toast.makeText(SmartTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, executor);
    }



    /**
     * جلب قائمة البلاغات من قاعدة البيانات السحابية.
     */
    private void getAllFromFirebase() {
        FirebaseDatabase.getInstance().getReference("tasks")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<MyTask> tasks = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        MyTask t = ds.getValue(MyTask.class);
                        if (t != null) tasks.add(t);
                    }
                    if (myTaskAdapter != null) {
                        myTaskAdapter.setTasksList(tasks);
                        myTaskAdapter.notifyDataSetChanged();
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "فشل جلب البيانات", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(airplaneReceiver, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(airplaneReceiver);
    }
}
