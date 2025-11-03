package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    // Splash screen duration in milliseconds
    private static final long SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Using a handler to delay the navigation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start the main activity
            Intent intent = new Intent(SplashScreen.this, login.class);
            startActivity(intent);
            startActivity(intent);
            
            // Close this activity
            finish();
            
            // Apply fade in/out animation between activities
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DELAY);
    }
}