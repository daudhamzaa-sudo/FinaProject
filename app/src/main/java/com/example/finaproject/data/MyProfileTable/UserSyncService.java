package com.example.finaproject.data.MyProfileTable;

// استيراد المكتبات اللازمة لعمل الخدمات (Services) والتعامل مع Firebase
import android.app.Service; // الفئة الأساسية لأي خدمة تعمل في الخلفية
import android.content.Intent; // للتواصل مع الخدمة
import android.os.IBinder; // مطلوب للخدمات التي ترتبط بواجهات (Bound Services)
import androidx.annotation.Nullable; // للسماح بقيم null في الدوال

import com.example.finaproject.data.AppDatabase; // قاعدة البيانات المحلية Room
import com.google.firebase.database.DataSnapshot; // لاستلام البيانات من Firebase
import com.google.firebase.database.DatabaseError; // لمعالجة أخطاء Firebase
import com.google.firebase.database.FirebaseDatabase; // الكلاس الرئيسي لقاعدة البيانات السحابية
import com.google.firebase.database.ValueEventListener; // مستمع لمراقبة تغييرات البيانات لحظياً

/**
 * كلاس UserSyncService: خدمة تعمل في الخلفية لمزامنة بيانات المستخدمين.
 */
public class UserSyncService extends Service {

    /**
     * دالة onStartCommand: يتم استدعاؤها عند بدء تشغيل الخدمة.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // بدء عملية مراقبة البيانات في السحابة
        startSync();
        // START_STICKY: تخبر النظام بإعادة تشغيل الخدمة تلقائياً إذا أُغلقت بسبب نقص الذاكرة
        return START_STICKY;
    }

    /**
     * دالة startSync: تقوم بمراقبة فرع "profiles" في Firebase وتحديث قاعدة البيانات المحلية.
     */
    private void startSync() {
        // الوصول لمجلد "profiles" في Firebase Realtime Database ومراقبته
        FirebaseDatabase.getInstance().getReference("profiles")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // عند حدوث أي تغيير في البيانات السحابية
                        new Thread(() -> {
                            // تحويل كل مستخدم في السحابة إلى كائن Profile وحفظه محلياً في Room
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Profile p = ds.getValue(Profile.class);
                                if (p != null) {
                                    // تحديث أو إضافة بيانات المستخدم في الهاتف لضمان المطابقة
                                    AppDatabase.getdb(getApplicationContext()).getProfile().update(p);
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // في حال فشل الاتصال بالسيرفر
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // لا نحتاج للربط (Binding) هنا لأنها خدمة تعمل بشكل مستقل
        return null;
    }
}
