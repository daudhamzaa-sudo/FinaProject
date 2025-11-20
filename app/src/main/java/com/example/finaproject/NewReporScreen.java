package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
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
inputRegion = findViewById(R.id.inputRegion);
tvTitle = findViewById(R.id.tvTitle);
tvSubtitle = findViewById(R.id.tvSubtitle);
btnSubmit.setOnClickListener(view -> {
    Intent intent = new Intent(NewReporScreen.this, MainActivity.class);
    startActivity(intent);
});
    }

    private boolean isValidReportFields() {
        boolean isValid = true;
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String region = inputRegion.getText().toString().trim();

        if (title.isEmpty()) {
            inputTitle.setError("Title is required");
            isValid = false;
        } else {
            inputTitle.setError(null);
        }

        if (description.isEmpty()) {
            inputDescription.setError("Description is required");
            isValid = false;
        } else {
            inputDescription.setError(null);
        }

        if (region.isEmpty()) {
            inputRegion.setError("Region is required");
            isValid = false;
        } else {
            inputRegion.setError(null);
        }

        return isValid;
    }


    private void addNewReport() {
        if (isValidReportFields()) {
            String title = inputTitle.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String region = inputRegion.getText().toString().trim();

            // Add your code here to add a new task using the provided fields

            //AppDatabase.getdb(this).getTaskQuery().insert(myTask);
        }
    }
}