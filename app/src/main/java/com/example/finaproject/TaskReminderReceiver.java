package com.example.finaproject;

// استيراد المكتبات اللازمة لإدارة الإشعارات وتنبيهات النظام
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

/**
 * كلاس مستقبل البث (BroadcastReceiver): وظيفته "الاستماع" لحدث معين من النظام (مثل موعد المنبه).
 */
public class TaskReminderReceiver extends BroadcastReceiver {

    // معرف قناة الإشعارات (إلزامي في إصدارات أندرويد الحديثة)
    private static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";

    /**
     * دالة onReceive: يتم استدعاؤها تلقائياً من قبل النظام عندما يحين وقت المنبه المبرمج.
     * @param context سياق التطبيق للوصول للخدمات.
     * @param intent يحمل البيانات المرسلة مع المنبه (مثل العنوان والوصف).
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // 1. استخراج عنوان ووصف المهمة التي تم تمريرها عند ضبط المنبه
        String title = intent.getStringExtra("title");
        String text  = intent.getStringExtra("text");

        // 2. استدعاء دالة إنشاء القناة (مهمة لأندرويد 8 فما فوق)
        createChannel(context);

        // 3. تحديد ماذا يحدث عند الضغط على الإشعار (فتح الشاشة الرئيسية MainActivity)
        Intent openIntent = new Intent(context, MainActivity.class);
        // PendingIntent: تصريح للنظام بفتح شاشة من تطبيقنا بالنيابة عنا
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 4. بناء شكل ومحتوى الإشعار (Builder Pattern)
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // أيقونة الإشعار الصغيرة
                        .setContentTitle("تذكير بـ: " + title)               // عنوان الإشعار
                        .setContentText(text)                                // نص الإشعار (وصف المشكلة)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)       // جعل الإشعار يظهر بوضوح في الأعلى
                        .setContentIntent(pi)                                // ربط الإشعار بالـ Intent لفتحه عند النقر
                        .setAutoCancel(true);                                // حذف الإشعار تلقائياً بعد الضغط عليه

        // 5. الوصول لمدير الإشعارات في الهاتف لإظهار الإشعار فعلياً
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // إرسال الإشعار للنظام ليظهر للمستخدم (نستخدم الوقت الحالي كمعرف فريد للإشعار)
        if (nm != null) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    /**
     * دالة إنشاء قناة إشعارات: مطلوبة في أندرويد 8.0 (Oreo) وما فوق لتنظيم الإشعارات.
     */
    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // تعريف القناة باسم ووصف يراه المستخدم في إعدادات الهاتف
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "منبه المهمات",
                    NotificationManager.IMPORTANCE_HIGH // درجة أهمية عالية (صوت واهتزاز)
            );
            
            // تسجيل القناة في نظام الهاتف
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }
}
