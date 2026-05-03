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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * MainActivity: تم تحديثها لربط الأزرار بالذكاء الاصطناعي بشكل صحيح.
 */
public class MainActivity extends AppCompatActivity {
    private Button button11;
    private EditText etTaskTopic;
    private GenerativeModel ai;
    private GenerativeModelFutures model;
    private TextView tvAiResponse;
    private Button btnAddReport;
    private RecyclerView recyclerReports;
    private MyTaskAdapter myTaskAdapter;
    private ImageView imgPreview;

    private final BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                if (btnAddReport != null) {
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

        // 1. تهيئة الذكاء الاصطناعي (تم تصحيح اسم الموديل)
        try {
            ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                    .generativeModel("gemini-1.5-flash");
            model = GenerativeModelFutures.from(ai);
        } catch (Exception e) {
            Toast.makeText(this, "خطأ في تهيئة AI", Toast.LENGTH_SHORT).show();
        }

        initViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getAllFromFirebase();
    }

    private void initViews() {
        btnAddReport = findViewById(R.id.btnAddReport);
        recyclerReports = findViewById(R.id.recyclerReports);
        imgPreview = findViewById(R.id.imgPreview);
        button11 = findViewById(R.id.button11);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        etTaskTopic = findViewById(R.id.etTaskTopic);

        // برمجة زر إرسال السؤال
        if (button11 != null) {
            button11.setOnClickListener(v -> {
                String topic = etTaskTopic.getText().toString().trim();
                if (!topic.isEmpty()) {
                    askFirebaseAiGeminiForSteps(topic);
                } else {
                    Toast.makeText(this, "يرجى كتابة المشكلة أولاً", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (recyclerReports != null) {
            recyclerReports.setLayoutManager(new LinearLayoutManager(this));
            myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
            recyclerReports.setAdapter(myTaskAdapter);
        }

        if (btnAddReport != null) {
            btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
        }

        if (imgPreview != null) {
            imgPreview.setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));
        }
    }

    private void askFirebaseAiGeminiForSteps(String topic) {
        if (model == null) return;

        tvAiResponse.setText("جاري التفكير...");
        button11.setEnabled(false);

        // صياغة الطلب بشكل صحيح
        String promptStr = "You are a smart assistant for city issue reporting.\n" +
                "A user reported this issue: \"" + topic + "\".\n\n" +
                "Provide:\n" +
                "- A brief description of the issue\n" +
                "- Step-by-step actions to resolve it\n" +
                "- The responsible authority\n" +
                "- Estimated urgency level (low, medium, high)\n" +
                "- Safety tips if relevant\n\n" +
                "Make the response clear and structured.";

        Content prompt = new Content.Builder()
                .addText(promptStr)
                .build();

        Executor executor = ContextCompat.getMainExecutor(this);
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    button11.setEnabled(true);
                    tvAiResponse.setText(result.getText());
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                runOnUiThread(() -> {
                    button11.setEnabled(true);
                    tvAiResponse.setText("خطأ: " + t.getMessage());
                });
            }
        }, executor);
    }

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
