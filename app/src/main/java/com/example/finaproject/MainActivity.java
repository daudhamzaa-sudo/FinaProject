package com.example.finaproject;
import com.example.finaproject.data.AppDatabase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.activity.EdgeToEdge;import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextInputLayout inputSearchLayout;
    private TextInputEditText inputSearch;
    private Button btnSearch;
    private Button btnAddReport;
    RecyclerView recyclerReports;
    MyTaskAdapter myTaskAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerReports=findViewById(R.id.recyclerReports);
        ArrayList<MyTask> myTasks = (ArrayList<MyTask>)AppDatabase.getdb(this).getMyTaskQuery().getAllTasks();
        myTaskAdapter = new MyTaskAdapter(this,myTasks);
        recyclerReports.setAdapter(myTaskAdapter);
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));

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

     //   List<MyTask> myTasks = new ArrayList<>();
      //  myTaskAdapter = new MyTaskAdapter(this, myTasks);
       // recyclerView.setAdapter(myTaskAdapter);

        btnAddReport.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewReporScreen.class);
            startActivity(intent);
        });
    } // This now correctly closes onCreate

    // في دالة onResume()
    @Override
    protected void onResume() {
        super.onResume();
        // 1. جلب البيانات من قاعدة البيانات
    ArrayList<MyTask> myTasks = (ArrayList<MyTask>)AppDatabase.getdb(this).getMyTaskQuery().getAllTasks();
    myTaskAdapter.setTasksList(myTasks);
    myTaskAdapter.notifyDataSetChanged();
    }

} // This correctly closes the MainActivity class