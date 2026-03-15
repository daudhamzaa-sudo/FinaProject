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

/**
 * الشاشة الرئيسية: تم حذف RecyclerItemClickListener لتجنب التضارب مع الـ Adapter.
 */
public class MainActivity extends AppCompatActivity {

    private TextView responseText;
    private ImageView imgPreview;
    private TextInputLayout inputSearchLayout;
    private TextInputEditText inputSearch;
    private Button btnAddReport;
    private RecyclerView recyclerReports;
    private MyTaskAdapter myTaskAdapter;

    private EditText inputText;
    private Button sendButton;
    private ProgressBar progressBar;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isOn = intent.getBooleanExtra("state", false);

            if(isOn){
                btnAddReport.setBackgroundColor(Color.GRAY);
            }else{
                btnAddReport.setBackgroundColor(Color.GREEN);
            }

        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupReportsUI();
        setupGeminiUI();
        getAllFromFirebase();
        IntentFilter filter = new IntentFilter("android.intent.action.AIRPLANE_MODE");
        registerReceiver(receiver, filter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupReportsUI() {
        recyclerReports = findViewById(R.id.recyclerReports);
        imgPreview = findViewById(R.id.imgPreview);
        btnAddReport = findViewById(R.id.btnAddReport);
        responseText = findViewById(R.id.responseText);

        imgPreview.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });

        // إعداد الـ RecyclerView
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
        recyclerReports.setAdapter(myTaskAdapter);

        // --- تم حذف RecyclerItemClickListener من هنا ليعمل كود الـ Adapter بشكل صحيح ---

        btnAddReport.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewReportScreen.class);
            startActivity(intent);
        });
    }

    private void getAllFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tasks");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MyTask> tasks = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MyTask task = postSnapshot.getValue(MyTask.class);
                    if (task != null) {
                        task.setKid(postSnapshot.getKey());
                        tasks.add(task);
                    }
                }
                if (myTaskAdapter != null) {
                    myTaskAdapter.setTasksList(tasks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupGeminiUI() {
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                String query = inputText.getText().toString();
                if (!query.isEmpty()) callGemini(query);
            });
        }
    }

    private void callGemini(String query) {
        progressBar.setVisibility(View.VISIBLE);
        String prompt = PromptBuilder.buildReportPrompt(query);
        GeminiHelper.getInstance().sendMessage(prompt, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                runOnUiThread(() -> {
                    responseText.setText(response);
                    progressBar.setVisibility(View.GONE);
                });
            }
            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        });
    }
}
