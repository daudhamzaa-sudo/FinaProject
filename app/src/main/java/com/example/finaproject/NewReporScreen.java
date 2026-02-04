package com.example.finaproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class NewReporScreen extends AppCompatActivity {
    private ImageView ivSelectedImage; //صفة كمؤشر لهذا الكائن
    private Uri selectedImageUri;//صفة لحفظ عنوان الصورة بعد اختيارها
    private ActivityResultLauncher<String> pickImage;// ‏كائن لطلب الصورة من الهاتف
    private ActivityResultLauncher<String> requestReadMediaImagesPermission;
    private ActivityResultLauncher<String> requestReadMediaVideoPermission;
    private ActivityResultLauncher<String> requestReadExternalStoragePermission;
    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_repor_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // تسجيل مُشغّل لطلب إذن READ_MEDIA_IMAGES
        requestReadMediaImagesPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d(TAG, "READ_MEDIA_IMAGES permission granted");
                Toast.makeText(this, "تم منح إذن قراءة الصور", Toast.LENGTH_SHORT).show();
                // يمكنك الآن المتابعة بالعملية التي تتطلب هذا الإذن
            } else {
                Log.d(TAG, "READ_MEDIA_IMAGES permission denied");
                Toast.makeText(this, "تم رفض إذن قراءة الصور", Toast.LENGTH_SHORT).show();
                // التعامل مع حالة رفض الإذن
            }
        });


// تسجيل مُشغّل لطلب إذن READ_MEDIA_VIDEO
        requestReadMediaVideoPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d(TAG, "READ_MEDIA_VIDEO permission granted");
                Toast.makeText(this, "تم منح إذن قراءة الفيديو", Toast.LENGTH_SHORT).show();
                // يمكنك الآن المتابعة بالعملية التي تتطلب هذا الإذن
            } else {
                Log.d(TAG, "READ_MEDIA_VIDEO permission denied");
                Toast.makeText(this, "تم رفض إذن قراءة الفيديو", Toast.LENGTH_SHORT).show();
                // التعامل مع حالة رفض الإذن
            }
        });


// تسجيل مُشغّل لطلب إذن READ_EXTERNAL_STORAGE
        requestReadExternalStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.d(TAG, "READ_EXTERNAL_STORAGE permission granted");
                Toast.makeText(this, "تم منح إذن قراءة التخزين الخارجي", Toast.LENGTH_SHORT).show();
                // يمكنك الآن المتابعة بالعملية التي تتطلب هذا الإذن
            } else {
                Log.d(TAG, "READ_EXTERNAL_STORAGE permission denied");
                Toast.makeText(this, "تم رفض إذن قراءة التخزين الخارجي", Toast.LENGTH_SHORT).show();
                // التعامل مع حالة رفض الإذن
            }
        });
//استدعاء دالة الفحص (سيتم تطبيقها لاحقا)
        checkAndRequestPermissions();



        // --- ربط المتغيرات بعناصر الواجهة ---
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        ivSelectedImage=findViewById(R.id.imgPreview);
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    getContentResolver().takePersistableUriPermission(result, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedImageUri = result;
                    ivSelectedImage.setImageURI(result);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                }
            }
        });
        ivSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage.launch("image/*"); // Launch the image picker
            }
        });
        btnSubmit.setOnClickListener(view -> {
            if (validateAndExtractData()) {
                // **تحسين: إضافة رسالة نجاح وإغلاق الشاشة**
                Toast.makeText(this, "Report saved successfully!", Toast.LENGTH_SHORT).show();
                finish(); // إغلاق الشاشة الحالية والرجوع إلى الشاشة السابقة
                // Initialize the ActivityResultLauncher for picking images


            }
        });
    }
    // دالة لفحص وطلب الأذونات
    private void checkAndRequestPermissions() {
        // فحص وطلب إذن READ_MEDIA_IMAGES (للإصدارات الحديثة)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // أندرويد 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadMediaImagesPermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                Log.d(TAG, "READ_MEDIA_IMAGES permission already granted");
                Toast.makeText(this, "إذن قراءة الصور ممنوح بالفعل", Toast.LENGTH_SHORT).show();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // أندرويد 10 و 11 و 12// على هذه الإصدارات، READ_EXTERNAL_STORAGE له سلوك مختلف
            // إذا كنت تستخدم Scoped Storage بشكل صحيح، قد لا تحتاج إلى هذا الإذن
            // ولكن إذا كنت تحتاج إلى الوصول إلى جميع الصور، فقد تحتاج إلى READ_EXTERNAL_STORAGE
            // في هذا المثال، سنفحص READ_EXTERNAL_STORAGE للإصدارات الأقدم من 13
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadExternalStoragePermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                Log.d(TAG, "READ_EXTERNAL_STORAGE permission already granted (for older versions)");
                Toast.makeText(this, "إذن قراءة التخزين ممنوح بالفعل (للإصدارات الأقدم)", Toast.LENGTH_SHORT).show();
            }
        } else { // أندرويد 9 والإصدارات الأقدم
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadExternalStoragePermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                Log.d(TAG, "READ_EXTERNAL_STORAGE permission already granted (for older versions)");
                Toast.makeText(this, "إذن قراءة التخزين ممنوح بالفعل (للإصدارات الأقدم)", Toast.LENGTH_SHORT).show();
            }
        }


        // فحص وطلب إذن READ_MEDIA_VIDEO (للإصدارات الحديثة)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // أندرويد 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestReadMediaVideoPermission.launch(android.Manifest.permission.READ_MEDIA_VIDEO);
            } else {
                Log.d(TAG, "READ_MEDIA_VIDEO permission already granted");
                Toast.makeText(this, "إذن قراءة الفيديو ممنوح بالفعل", Toast.LENGTH_SHORT).show();
            }
        }// ملاحظة: إذن INTERNET لا يحتاج إلى فحص أو
    }

    private boolean validateAndExtractData() {
        // **تصحيح:** يجب ربط حقل المنطقة أولاً إذا لم يكن مربوطًا في onCreate


        // **تعديل: إضافة trim() لإزالة المسافات الزائدة**
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();

        // **تعديل: التحقق يشمل الآن حقل المنطقة**
        if (title.isEmpty() || description.isEmpty() ) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isValid = true;
        if (title.length() < 5) {
            inputTitle.setError("Title must be at least 5 characters");
            isValid = false;
        }
        if (description.length() < 10) {
            inputDescription.setError("Description must be at least 10 characters");
            isValid = false;
        }

        if (isValid) {
            MyTask myTask1 = new MyTask();
            myTask1.setTaskName(title);
            myTask1.setTaskDescription(description);

            // --- هنا تم وضع الكود الناقص والمهم ---

            // 1. حفظ المنطقة في الكائن


            // 2. التحقق من وجود صورة مختارة وحفظ مسارها
            if (selectedImageUri != null) {
                // ملاحظة هامة: هذا يحفظ المسار المؤقت. الحل الدائم يتطلب نسخ الملف.
                myTask1.setImageUrl(selectedImageUri.toString());
            }

            // --- نهاية الكود الناقص ---

            // حفظ الكائن في قاعدة البيانات
            AppDatabase.getdb(this).getMyTaskQuery().insert(myTask1);
        }

        return isValid;
    }
    public void saveTask(MyTask task, android.content.Context context) {
        // Get a reference to the database
        com.google.firebase.database.DatabaseReference database =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference();

        // Reference to the "tasks" node
        com.google.firebase.database.DatabaseReference tasksRef = database.child("tasks");

        // Create a new unique key for the task
        com.google.firebase.database.DatabaseReference newTaskRef = tasksRef.push();

        // Set the task ID
        task.seti(Long.parseLong(newTaskRef.get()));

        // Save the task to the database
        newTaskRef.setValue(task)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (context != null) {
                            android.widget.Toast.makeText(context, "تم حفظ المهمة بنجاح",
                                    android.widget.Toast.LENGTH_SHORT).show();
                            if (context instanceof android.app.Activity) {
                                ((android.app.Activity) context).finish();
                            }
                        }
                        android.util.Log.d("MyTask", "تم حفظ المهمة بنجاح: " + task.getId());
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        android.util.Log.e("MyTask", "خطأ في حفظ المهمة: " + e.getMessage(), e);
                        if (context != null) {
                            android.widget.Toast.makeText(context,
                                    "فشل في حفظ المهمة: " + e.getMessage(),
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
