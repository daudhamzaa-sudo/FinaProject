package com.example.finaproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
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

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private Button button11;
    private EditText etTaskTopic;
    private TextView tvAiResponse;
    private Button btnAddReport;
    private MyTaskAdapter myTaskAdapter;
    private GenerativeModelFutures model;

    private final BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
                boolean isOn = intent.getBooleanExtra("state", false);
                if (btnAddReport != null) {
                    btnAddReport.setBackgroundColor(isOn ? Color.RED : Color.parseColor("#2E7D32"));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // تهيئة الذكاء الاصطناعي بشكل مستقر
        try {
            //GenerativeModel gm = FirebaseVertexAI.getInstance().generativeModel("gemini-2.5-flash");
            //model = GenerativeModelFutures.from(gm);
        } catch (Exception e) {
            Toast.makeText(this, "AI initialization failed", Toast.LENGTH_SHORT).show();
            model = null;
        }

        initViews();
         getAllFromFirebase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        button11 = findViewById(R.id.button11);
        etTaskTopic = findViewById(R.id.etTaskTopic);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        btnAddReport = findViewById(R.id.btnAddReport);
        RecyclerView rv = findViewById(R.id.recyclerReports);
        ImageView imgPreview = findViewById(R.id.imgPreview);

        if (button11 != null) {
            button11.setOnClickListener(v -> {
                String q = etTaskTopic.getText().toString().trim();
                if (!q.isEmpty()) askAi(q);
            });
        }

        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
            rv.setAdapter(myTaskAdapter);
        }

        if (btnAddReport != null) btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
        if (imgPreview != null) imgPreview.setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));
    }
    
    private void askAi(String prompt) {
        if (model == null) return;
        tvAiResponse.setText("Thinking...");
        button11.setEnabled(false);

        Content content = new Content.Builder().addText(prompt).build();
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
                    tvAiResponse.setText("Error: " + t.getMessage());
                    button11.setEnabled(true);
                });
            }
        }, executor);
    }

    private void getAllFromFirebase() {
        FirebaseDatabase.getInstance().getReference("tasks").addValueEventListener(new ValueEventListener() {
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
            @Override public void onCancelled(@NonNull DatabaseError error) {}
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
