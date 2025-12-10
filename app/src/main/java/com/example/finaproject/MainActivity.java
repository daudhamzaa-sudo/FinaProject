package com.example.finaproject;
import com.example.finaproject.data.AppDatabase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaproject.data.MyTaskTable.MyTask; // Make sure to import your MyTask class
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList; // Import ArrayList
import java.util.List;      // Import List
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.AppDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextInputLayout inputSearchLayout;
    private TextInputEditText inputSearch;
    private Button btnSearch;
    private Button btnAddReport;
    private MyTaskAdapter myTaskAdapter;
    private RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        inputSearchLayout = findViewById(R.id.inputSearchLayout);
        inputSearch = findViewById(R.id.inputSearch);
        // btnSearch = findViewById(R.id.btnSearch);
        btnAddReport = findViewById(R.id.btnAddReport);
        recyclerView = findViewById(R.id.recyclerReports);

        // FIX: Initialize an empty list of tasks and pass it to the adapter.

        // TO THIS:
        List<MyTask> myTasks = new ArrayList<>();
        myTaskAdapter = new MyTaskAdapter(this, myTasks); // Pass the initialized list
//lstTasks.setAdapter(myTaskAdapter)
        recyclerView.setAdapter(myTaskAdapter);

        btnAddReport.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewReporScreen.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        List<MyTask> allTasks = AppDatabase.getdb(this).getMyTaskQuery().getAllTasks();
        taskArrayAdapter.clear();
        taskArrayAdapter.addAll(allTasks);
        taskArrayAdapter.notifyDatasetChanged();

    }
}
