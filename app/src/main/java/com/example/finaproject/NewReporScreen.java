package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class NewReporScreen extends AppCompatActivity {
private TextInputEditText inputTitle;
private TextInputEditText inputDescription;
private AutoCompleteTextView inputRegion;
private TextInputEditText attachPhotoLayout;
    private TextView imgPreview;
    private TextView btnAttachPhoto;
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
        btnSubmit = findViewById(R.id.btnSubmit);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);


btnSubmit.setOnClickListener(view -> {
    if (validateAndExtractData()) {
        Intent intent = new Intent(NewReporScreen.this, MainActivity.class);
        startActivity(intent);
    }
});
        }

    private boolean validateAndExtractData() {
        String title = inputTitle.getText().toString();
        String description = inputDescription.getText().toString();


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

            AppDatabase.getdb(this).getMyTaskQuery().insert(myTask1);
        }

        return isValid;
    }


    }