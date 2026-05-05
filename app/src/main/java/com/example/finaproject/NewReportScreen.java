package com.example.finaproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewReportScreen extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private Uri selectedImageUri;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLocation;
    private double latitude = 0;
    private double longitude = 0;
    private MaterialButton btnSubmit;
    private Button btnSetReminder;
    private TextView tvReminderTime;
    private long selectedReminderTime = 0;

    private ActivityResultLauncher<String> pickImage;
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher = 
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (!isGranted) Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_repor_screen);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initViews();
        setupLaunchers();
        handleSystemBars();
    }

    private void initViews() {
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        tvLocation = findViewById(R.id.tvLocation);
        ivSelectedImage = findViewById(R.id.imgPreview);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        tvReminderTime = findViewById(R.id.tvReminderTime);

        btnSetReminder.setOnClickListener(v -> showDateTimePicker());
        findViewById(R.id.btnGetLocation).setOnClickListener(v -> fetchLocation());
        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSubmit.setOnClickListener(v -> saveReportLogic());
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
            .addOnSuccessListener(location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tvLocation.setText("Location: " + latitude + " , " + longitude);
                }
            });
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            date.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hour);
                date.set(Calendar.MINUTE, minute);
                selectedReminderTime = date.getTimeInMillis();
                tvReminderTime.setText(date.getTime().toString());
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveReportLogic() {
        String title = inputTitle.getText().toString().trim();
        String desc = inputDescription.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        MyTask task = new MyTask();
        task.setTaskName(title);
        task.setTaskDescription(desc);
        task.setLatitude(latitude);
        task.setLongitude(longitude);
        task.setReminderTime(selectedReminderTime);
        if (selectedImageUri != null) task.setImageUrl(selectedImageUri.toString());

        new Thread(() -> {
            // الحفظ المحلي أولاً للحصول على ID فريد للمنبه
            long id = AppDatabase.getdb(this).getMyTaskQuery().insert(task);
            task.setId(id);
            
            runOnUiThread(() -> {
                if (selectedReminderTime > System.currentTimeMillis()) {
                    scheduleAlarm(task);
                }
                syncWithFirebase(task);
            });
        }).start();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlarm(MyTask task) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TaskReminderReceiver.class);
        intent.putExtra("title", task.getTaskName());
        intent.putExtra("text", task.getTaskDescription());

        PendingIntent pi = PendingIntent.getBroadcast(this, (int) task.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (am != null) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.getReminderTime(), pi);
    }

    private void syncWithFirebase(MyTask task) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks").push();
        task.setKid(ref.getKey());
        ref.setValue(task).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Saved and Synced!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupLaunchers() {
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                ivSelectedImage.setImageURI(uri);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
