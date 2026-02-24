package com.example.finaproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/**
 * كلاس SplashScreen: هو أول شاشة تظهر للمستخدم عند فتح التطبيق.
 * وظيفته عرض شعار التطبيق لفترة زمنية محددة قبل الانتقال لشاشة تسجيل الدخول.
 */
public class SplashScreen extends AppCompatActivity {

    // تحديد مدة عرض الشاشة بالملي ثانية (3000 ملي ثانية = 3 ثوانٍ)
    private static final long SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ربط الكود بملف التصميم الخاص بشاشة الترحيب
        setContentView(R.layout.activity_splash_screen);

        /**
         * استخدام Handler لإيقاف التنفيذ مؤقتاً.
         * Looper.getMainLooper(): يضمن تنفيذ الكود على "الخيط الرئيسي" لضمان استقرار الواجهة.
         * postDelayed: تقوم بتنفيذ الأوامر الموجودة بداخلها بعد مرور الوقت المحدد (SPLASH_DELAY).
         */
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // 1. إنشاء "نية" (Intent) للانتقال من شاشة الترحيب الحالية إلى شاشة التسجيل (login)
            Intent intent = new Intent(SplashScreen.this, login.class);

            // 2. أمر النظام ببدء تشغيل الشاشة الجديدة
            // ملاحظة: قمت بتكرار startActivity مرتين في كودك، يجب حذف واحدة منها لتجنب فتح الشاشة مرتين.
            startActivity(intent);

            // 3. إنهاء شاشة الترحيب الحالية (finish)
            // هذا يمنع المستخدم من العودة لشاشة الترحيب عند الضغط على زر "الرجوع" من شاشة تسجيل الدخول.
            finish();

            // 4. تطبيق تأثير حركة (Animation) عند الانتقال بين الشاشتين
            // android.R.anim.fade_in: ظهور تدريجي للشاشة الجديدة.
            // android.R.anim.fade_out: اختفاء تدريجي للشاشة القديمة.
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }, SPLASH_DELAY);
    }
}