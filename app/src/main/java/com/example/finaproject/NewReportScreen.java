package com.example.finaproject;

import androidx.annotation.NonNull;
import com.example.finaproject.data.MyTaskTable.TaskSyncService;
import static android.content.ContentValues.TAG;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

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

public class NewReportScreen extends AppCompatActivity {
    private ImageView ivSelectedImage; 
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> pickImage;
    private ActivityResultLauncher<String> requestReadMediaImagesPermission;
    private ActivityResultLauncher<String> requestReadExternalStoragePermission;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnGetLocation;
    private TextView tvLocation;
    private double latitude = 0;
    private double longitude = 0;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_repor_screen);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        tvLocation = findViewById(R.id.tvLocation);
        ivSelectedImage = findViewById(R.id.imgPreview);

        setupPermissionsLaunchers();
        checkAndRequestPermissions();

        btnGetLocation.setOnClickListener(v -> fetchLocation());

        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                selectedImageUri = result;
                ivSelectedImage.setImageURI(result);
                ivSelectedImage.setVisibility(View.VISIBLE);
            }
        });

        ivSelectedImage.setOnClickListener(v -> pickImage.launch("image/*"));

        btnSubmit.setOnClickListener(view -> {
            if (validateAndExtractData()) {
                // الحفظ يتم داخل الدالة
            }
        });
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        tvLocation.setText("جاري جلب الموقع...");

        // محاولة جلب الموقع الحالي بدقة عالية بدلاً من الاعتماد على آخر موقع مخزن فقط
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        tvLocation.setText("الموقع: " + latitude + " , " + longitude);
                    } else {
                        // إذا فشل getCurrentLocation، نحاول getLastLocation كملاذ أخير
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLoc -> {
                            if (lastLoc != null) {
                                latitude = lastLoc.getLatitude();
                                longitude = lastLoc.getLongitude();
                                tvLocation.setText("الموقع (قديم): " + latitude + " , " + longitude);
                            } else {
                                Toast.makeText(this, "تعذر العثور على الموقع. تأكد من تفعيل الـ GPS", Toast.LENGTH_LONG).show();
                                tvLocation.setText("الموقع غير موجود");
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "خطأ في جلب الموقع: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateAndExtractData() {
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "جميع الحقول مطلوبة", Toast.LENGTH_SHORT).show();
            return false;
        }

        MyTask myTask1 = new MyTask();
        myTask1.setTaskName(title);
        myTask1.setTaskDescription(description);
        myTask1.setLatitude(latitude);
        myTask1.setLongitude(longitude);
        if (selectedImageUri != null) {
            myTask1.setImageUrl(selectedImageUri.toString());
        }

        AppDatabase.getdb(this).getMyTaskQuery().insert(myTask1);
        saveTask(myTask1, this);

        return true;
    }

    public void saveTask(MyTask task, android.content.Context context) {
        com.google.firebase.database.DatabaseReference tasksRef =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("tasks");

        com.google.firebase.database.DatabaseReference newTaskRef = tasksRef.push();
        task.setKid(newTaskRef.getKey());

        newTaskRef.setValue(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "تم حفظ البلاغ بنجاح", Toast.LENGTH_SHORT).show();
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "فشل في الحفظ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupPermissionsLaunchers() {
        requestReadMediaImagesPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});
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
