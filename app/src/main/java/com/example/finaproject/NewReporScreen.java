package com.example.finaproject;

import android.content.Intent;
import android.net.Uri; // **إضافة جديدة**
import android.os.Bundle;
import android.view.View; // **إضافة جديدة**
import android.widget.AutoCompleteTextView;
import android.widget.ImageView; // **إضافة جديدة**
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher; // **إضافة جديدة**
import androidx.activity.result.contract.ActivityResultContracts; // **إضافة جديدة**
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class NewReporScreen extends AppCompatActivity {
    private TextInputEditText inputTitle;
    private TextInputEditText inputDescription;
    private AutoCompleteTextView inputRegion;

    // **تعديل أنواع المتغيرات لتناسب الواجهة الجديدة**
    private ImageView imgPreview;
    private MaterialButton btnAttachPhoto;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private MaterialButton btnSubmit;

    // **إضافة جديدة: متغير لتخزين مسار الصورة المختارة**
    private Uri selectedImageUri = null;

    // **إضافة جديدة: تعريف الـ Launcher لاستقبال الصورة من المعرض**
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                // هذا الكود يعمل بعد اختيار المستخدم للصورة
                if (uri != null) {
                    // حفظ مسار الصورة وعرضها
                    selectedImageUri = uri;
                    imgPreview.setImageURI(selectedImageUri);
                    imgPreview.setVisibility(View.VISIBLE); // إظهار الصورة
                }
            });


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

        // --- ربط المتغيرات بعناصر الواجهة ---
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);



        imgPreview = findViewById(R.id.imgPreview);
        btnAttachPhoto = findViewById(R.id.btnAttachPhoto);

        // **إضافة جديدة: مستمع النقر لزر إرفاق الصورة**
        btnAttachPhoto.setOnClickListener(view -> {
            // عند الضغط، يتم فتح معرض الصور لاختيار صورة
            galleryLauncher.launch("image/*");
        });


        btnSubmit.setOnClickListener(view -> {
            if (validateAndExtractData()) {
                // **تحسين: إضافة رسالة نجاح وإغلاق الشاشة**
                Toast.makeText(this, "Report saved successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NewReporScreen.this, MainActivity.class);
                startActivity(intent);
                finish(); // إغلاق الشاشة الحالية لمنع تراكمها
            }
        });
    }

    private boolean validateAndExtractData() {
        // **تعديل: إضافة trim() لإزالة المسافات الزائدة**
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        // **إضافة جديدة: قراءة قيمة المنطقة**
        String region = inputRegion.getText().toString().trim();

        // **تعديل: التحقق يشمل الآن حقل المنطقة**
        if (title.isEmpty() || description.isEmpty() || region.isEmpty()) {
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

            // **إضافة جديدة: حفظ المنطقة ومسار الصورة في الكائن**
            myTask1.Region = region; // تأكد من وجود حقل Region في MyTask.java
            if (selectedImageUri != null) {
                // تأكد من وجود حقل imageUri في MyTask.java
                myTask1.imageUri = selectedImageUri.toString();
            }

            AppDatabase.getdb(this).getMyTaskQuery().insert(myTask1);
        }

        return isValid;
    }
}
