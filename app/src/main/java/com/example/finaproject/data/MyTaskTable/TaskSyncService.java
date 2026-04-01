package com.example.finaproject.data.MyTaskTable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * خدمة (Service) خلفية لمزامنة بيانات المهام مع Firebase.
 * تضمن هذه الخدمة رفع البيانات حتى لو أغلق المستخدم التطبيق.
 */
public class TaskSyncService extends Service {
    public TaskSyncService() {
    }

    /**
     * يتم استدعاء هذه الدالة عند بدء تشغيل الخدمة.
     * @param intent يحمل البيانات الممررة (كائن المهمة).
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // قراءة البيانات المستلمة من الـ Intent
        if (intent != null && intent.hasExtra("task_extra")) {
             MyTask task = (MyTask) intent.getSerializableExtra("task_extra");
             // بدء عملية الحفظ في Firebase
            saveMyTaskToFirebase(task);
        }
        
        // START_NOT_STICKY: لا تقم بإعادة تشغيل الخدمة تلقائياً إذا قتلت من قبل النظام.
        return START_NOT_STICKY;
    }

    /**
     * دالة لإرسال بيانات المهمة إلى Firebase Realtime Database.
     * @param task كائن المهمة المراد حفظه.
     */
    private void saveMyTaskToFirebase(MyTask task) {
        // الحصول على مرجع لعقدة "tasks"
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("tasks");
        
        // إنشاء مفتاح فريد (Key) للمهمة الجديدة
        String key = myRef.push().getKey();

        // تعيين المفتاح في حقل kid لسهولة الوصول إليه لاحقاً
        task.setKid(key);

        // حفظ الكائن بالكامل في Firebase تحت المفتاح المنشأ
        myRef.child(key).setValue(task).addOnCompleteListener(fbTask -> {
            if (fbTask.isSuccessful()) {
                // عرض رسالة نجاح (باستخدام سياق التطبيق العام داخل الخدمة)
                Toast.makeText(getApplicationContext(), "تمت المزامنة بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                // في حال الفشل (مثل مشكلة Permission Denied)
                Toast.makeText(getApplicationContext(), "فشلت المزامنة: " + fbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            
            // إيقاف الخدمة تلقائياً بعد انتهاء المهمة لتوفير الموارد
            stopSelf();
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // هذه الخدمة لا تدعم الربط (Binding)، لذا نعيد null.
        return null; 
    }
}
