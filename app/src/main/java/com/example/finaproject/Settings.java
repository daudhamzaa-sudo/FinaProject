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
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Settings extends AppCompatActivity {

    //تعريف الصفات
    private TextView tvTitle;
    private TextView tvSubtitle;
    private Button btnLogout;
    private TextView txtUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        tvTitle = findViewById(R.id.settingsTitle);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnLogout = findViewById(R.id.btnLogout);
        
        // Check if user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // User is logged in, get their email from Firebase
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (userEmail != null) {
                txtUserEmail.setText(userEmail);
            } else {
                // Fallback to local database
                List<Profile> all = AppDatabase.getdb(this).myProfileQuery().getAll();
                if (all != null && !all.isEmpty()) {
                    txtUserEmail.setText(all.get(0).getEmail());
                } else {
                    txtUserEmail.setText("Email not found");
                }
            }
        } else {
            // User is not logged in, redirect to login
            Intent intent = new Intent(Settings.this, login.class);
            startActivity(intent);
            finish();
            return;
        }
        
        btnLogout.setOnClickListener(view -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();
            
            // Clear local database if needed
          //  AppDatabase.getdb(this).myProfileQuery().deleteAll();
            
            // Navigate to login screen instead of splash screen
            Intent intent = new Intent(Settings.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


    }
}