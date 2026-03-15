package com.example.finaproject;

import androidx.annotation.NonNull;
import com.example.finaproject.data.MyTaskTable.TaskSyncService;
import static android.content.ContentValues.TAG;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * شاشة إضافة بلاغ جديد: تتيح للمستخدم إدخال تفاصيل البلاغ، اختيار صورة، وتحديد الموقع.
 */
public class NewReportScreen extends AppCompatActivity {
    private ImageView ivSelectedImage; 
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> pickImage;
    private ActivityResultLauncher<String> requestReadMediaImagesPermission;
    private ActivityResultLauncher<String> requestReadMediaVideoPermission;
    private ActivityResultLauncher<String> requestReadExternalStoragePermission;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnGetLocation;
    private TextView tvLocation;
    private double latitude = 0;
    private double longitude = 0;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_repor_screen);
        
        // إعداد واجهة المستخدم وتعديل الهوامش
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            return insets;
        });

        // تهيئة العناصر
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        tvLocation = findViewById(R.id.tvLocation);
        ivSelectedImage = findViewById(R.id.imgPreview);

        // طلب أذونات الصور والموقع
        setupPermissionsLaunchers();
        checkAndRequestPermissions();

        // زر جلب الموقع الجغرافي
        btnGetLocation.setOnClickListener(v -> fetchLocation());

        // إعداد اختيار صورة من المعرض
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                selectedImageUri = result;
                ivSelectedImage.setImageURI(result);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });

        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));

        // زر إرسال البلاغ
        btnSubmit.setOnClickListener(view -> {
            if (validateAndExtractData()) {
                // سيتم الحفظ والرجوع عبر دالة saveTask
            }
        });
    }

    /**
     * دالة لجلب الموقع الحالي للمستخدم.
     */
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                tvLocation.setText("Location: " + latitude + ", " + longitude);
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * دالة للتحقق من البيانات المدخلة وحفظها.
     */
    private boolean validateAndExtractData() {
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (title.length() < 5) {
            inputTitle.setError("Title must be at least 5 characters");
            return false;
        }

        // إنشاء كائن المهمة وتعبئة البيانات
        MyTask myTask1 = new MyTask();
        myTask1.setTaskName(title);
        myTask1.setTaskDescription(description);
        myTask1.setLatitude(latitude);
        myTask1.setLongitude(longitude);
        if (selectedImageUri != null) {
            myTask1.setImageUrl(selectedImageUri.toString());
        }

        // حفظ محلياً في Room
        AppDatabase.getdb(this).getMyTaskQuery().insert(myTask1);
        
        // حفظ في Firebase
        saveTask(myTask1, this);

        return true;
    }

    /**
     * دالة لحفظ المهمة في Firebase Realtime Database.
     */
    public void saveTask(MyTask task, android.content.Context context) {
        com.google.firebase.database.DatabaseReference tasksRef =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("tasks");

        // إنشاء مفتاح فريد
        com.google.firebase.database.DatabaseReference newTaskRef = tasksRef.push();
        
        // تصحيح: حفظ المفتاح في حقل kid بدلاً من تغيير اسم المهمة
        task.setKid(newTaskRef.getKey());

        newTaskRef.setValue(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "تم حفظ البلاغ بنجاح", Toast.LENGTH_SHORT).show();
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MyTask", "خطأ في حفظ المهمة: " + e.getMessage());
                    Toast.makeText(context, "فشل في حفظ المهمة: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupPermissionsLaunchers() {
        requestReadMediaImagesPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
        requestReadMediaVideoPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
        requestReadExternalStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestReadMediaImagesPermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestReadExternalStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }
}
