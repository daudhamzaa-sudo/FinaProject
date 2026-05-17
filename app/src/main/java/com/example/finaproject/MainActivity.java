package com.example.finaproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private Button btnAddReport;
    private RecyclerView recyclerReports;
    private MyTaskAdapter myTaskAdapter;
    private ImageView imgPreview;

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

        initViews();
        getAllFromFirebase();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        btnAddReport = findViewById(R.id.btnAddReport);
        recyclerReports = findViewById(R.id.recyclerReports);
        imgPreview = findViewById(R.id.imgPreview);

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

    private void getAllFromFirebase() {
        FirebaseDatabase.getInstance().getReference("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MyTask> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MyTask t = ds.getValue(MyTask.class);
                    if (t != null) list.add(t);
                }
                if (myTaskAdapter != null) {
                    myTaskAdapter.setTasksList(list);
                    myTaskAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
