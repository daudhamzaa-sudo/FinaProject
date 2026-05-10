package com.example.finaproject.data.MyTaskTable;

// استيراد المكتبات اللازمة لعمل الخدمة والتعامل مع Firebase
import android.app.Service; // الفئة الأساسية لإنشاء خدمة تعمل في الخلفية
import android.content.Intent; // لنقل البيانات وبدء الخدمة
import android.os.IBinder; // واجهة مطلوبة للخدمات المرتبطة (نحن لا نستخدمها هنا)
import android.widget.Toast; // لعرض رسائل للمستخدم من داخل الخدمة

import androidx.annotation.Nullable; // للسماح بقيم فارغة

import com.google.firebase.database.DatabaseReference; // مرجع قاعدة بيانات Firebase
import com.google.firebase.database.FirebaseDatabase; // الكلاس الرئيسي لـ Firebase

/**
 * كلاس TaskSyncService: خدمة خلفية (Background Service) تضمن رفع بيانات البلاغ إلى Firebase.
 * ميزة هذه الخدمة أنها تستمر في العمل حتى لو خرج المستخدم من شاشة الإضافة.
 */
public class TaskSyncService extends Service {
    
    // باني الخدمة
    public TaskSyncService() {
    }

    /**
     * دالة onStartCommand: يتم استدعاؤها عند تشغيل الخدمة باستخدام startService().
     * @param intent يحمل البيانات الممررة (كائن المهمة/البلاغ).
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // التأكد من أن الـ intent ليس فارغاً ويحتوي على بيانات البلاغ
        if (intent != null && intent.hasExtra("task_extra")) {
             // استلام كائن البلاغ المرسل من الشاشة
             MyTask task = (MyTask) intent.getSerializableExtra("task_extra");
             // بدء عملية الرفع إلى Firebase السحابية
            saveMyTaskToFirebase(task);
        }
        
        // START_NOT_STICKY: تخبر النظام ألا يعيد تشغيل الخدمة إذا أُغلقت بسبب نقص الذاكرة
        return START_NOT_STICKY;
    }

    /**
     * دالة لإرسال بيانات المهمة إلى قاعدة بيانات Firebase Realtime Database.
     * @param task كائن المهمة المراد حفظه سحابياً.
     */
    private void saveMyTaskToFirebase(MyTask task) {
        // الحصول على مرجع للمجلد "tasks" في قاعدة البيانات السحابية
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("tasks");
        
        // إنشاء "مفتاح فريد" (Unique Key) للبلاغ الجديد لضمان عدم تكرار المعرفات
        String key = myRef.push().getKey();

        // حفظ هذا المفتاح داخل حقل kid في الكائن نفسه لسهولة الرجوع إليه عند الحذف أو التعديل
        task.setKid(key);

        // وضع بيانات الكائن بالكامل داخل المفتاح المنشأ في Firebase
        myRef.child(key).setValue(task).addOnCompleteListener(fbTask -> {
            if (fbTask.isSuccessful()) {
                // عرض رسالة نجاح في حال اكتمال الرفع
                Toast.makeText(getApplicationContext(), "تمت المزامنة السحابية بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                // عرض رسالة خطأ في حال فشل الرفع (مثلاً انقطاع الإنترنت)
                Toast.makeText(getApplicationContext(), "فشلت المزامنة: " + fbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            
            // إيقاف الخدمة تلقائياً بعد انتهاء المهمة لتحرير موارد الهاتف والبطارية
            stopSelf();
        });
    }

    /**
     * دالة onBind: مطلوبة برمجياً ولكننا نعيد null لأننا نستخدم "Started Service" وليس "Bound Service".
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; 
    }
}
