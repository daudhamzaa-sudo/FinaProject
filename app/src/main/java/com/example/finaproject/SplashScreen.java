package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

/**
 * شاشة البداية (Splash Screen): تظهر عند فتح التطبيق لأول مرة لعرض شعار التطبيق.
 */
public class SplashScreen extends AppCompatActivity {
    // تحديد مدة بقاء الشاشة (3 ثوانٍ) قبل الانتقال للشاشة التالية
    private static final long SPLASH_DELAY = 3000; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تعيين التصميم الخاص بشاشة البداية
        setContentView(R.layout.activity_splash_screen);
// thread
        // استخدام Handler لتأخير عملية الانتقال
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // الانتقال من شاشة البداية إلى شاشة تسجيل الدخول (login)
            Intent intent = new Intent(SplashScreen.this, login.class);
            startActivity(intent);
            
            // إغلاق شاشة البداية لكي لا يعود إليها المستخدم عند الضغط على زر الرجوع
            finish();
            
            // إضافة حركة انتقال ناعمة (تلاشي) بين الشاشات
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DELAY);
    }
}