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

        // AI functionality disabled - Vertex AI removed per user request

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

//        if (button11 != null) {
//            button11.setOnClickListener(v -> {
//                String q = etTaskTopic.getText().toString().trim();
//                if (!q.isEmpty()) askAi(q);
//            });
//        }

        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
            rv.setAdapter(myTaskAdapter);
        }

        if (btnAddReport != null) btnAddReport.setOnClickListener(v -> startActivity(new Intent(this, NewReportScreen.class)));
        if (imgPreview != null) imgPreview.setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));
    }

    private void askFirebaseAiGeminiForSteps(String topic) {
        //pbLoading.setVisibility(View.VISIBLE);
        tvAiResponse.setText("");
        button11.setEnabled(false);


        String promptStr = "I want to perform the following task: '" + topic + "'. " +
                "Can you suggest a clear, step-by-step checklist to complete this task effectively?";


//        Content prompt = new Content.Builder()
//                .addText(promptStr)
//                .build();


        // model.generateContent(prompt); // Vertex AI removed
//        Executor executor = this::runOnUiThread;
//        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//            @Override
//            public void onSuccess(GenerateContentResponse result) {
//                //pbLoading.setVisibility(View.GONE);
//                button11.setEnabled(true);
//                tvAiResponse.setText(result.getText());
//            }
//
//
//            @Override
//            public void onFailure(Throwable t) {
//                //pbLoading.setVisibility(View.GONE);
//                button11.setEnabled(true);
//                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }, executor);
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
