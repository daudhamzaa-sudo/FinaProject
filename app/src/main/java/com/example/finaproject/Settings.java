package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyProfileTable.Profile;

import java.util.List;

public class Settings extends AppCompatActivity {

    //تعريف الصفات
    private TextView tvTitle;
    private TextView tvSubtitle;
    private Button btnLogout;
    private TextView txtUserEmail;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        super.onCreate(savedInstanceState);
        tvTitle = findViewById(R.id.settingsTitle);
        tvSubtitle = findViewById(R.id.txtUserEmail);
        btnLogout = findViewById(R.id.btnLogout);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        List<Profile> all = AppDatabase.getdb(this).myProfileQuery().getAll();
        if (all != null&& all.isEmpty()==false) {
            txtUserEmail.setText(all.get(0).getEmail());
        } else {
            txtUserEmail.setText("Email not found you have to signup");
        }
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.this, SplashScreen.class);
            startActivity(intent);
        });


    }
}