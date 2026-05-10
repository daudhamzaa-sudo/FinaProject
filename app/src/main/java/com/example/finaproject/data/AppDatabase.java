package com.example.finaproject.data;

// استيراد المكتبات الخاصة بمكتبة Room لإدارة قواعد البيانات المحلية
import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// استيراد الجداول والواجهات الخاصة بالاستعلامات
import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskQuery;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.example.finaproject.data.MyProfileTable.MyProfileQuery;

/**
 * كلاس AppDatabase: هو المصدر الرئيسي للبيانات في التطبيق (Local Data Source).
 * نستخدم وسم @Database لتحديد الجداول (Entities) وإصدار قاعدة البيانات.
 */
@Database(entities = {Profile.class, MyTask.class}, version = 6) 
public abstract class AppDatabase extends RoomDatabase {

    // متغير ثابت (Static) لحفظ نسخة واحدة فقط من قاعدة البيانات لضمان عدم استهلاك موارد الهاتف
    private static AppDatabase db;

    /**
     * دالة getdb: تتبع نمط "Singleton" لضمان وجود نسخة واحدة فقط من قاعدة البيانات في كل التطبيق.
     * @param context سياق التطبيق لإنشاء ملف قاعدة البيانات في ذاكرة الهاتف.
     * @return نسخة من AppDatabase.
     */
    public static AppDatabase getdb(Context context) {
        if (db == null) {
            // بناء قاعدة البيانات باستخدام اسم الملف "HamzaDataBase"
            db = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,"HamzaDataBase")
                    .fallbackToDestructiveMigration() // في حال تغيير رقم الإصدار (Version)، يقوم بمسح البيانات القديمة لضمان التوافق
                    .allowMainThreadQueries() // السماح بالعمليات البسيطة على الخيط الرئيسي (يستخدم بحذر)
                    .build();
        }
        return db;
    }

    // تعريف الدوال المجردة (Abstract Methods) التي تعطينا الوصول لعمليات الجداول
    
    // للوصول لعمليات جدول الملفات الشخصية (Profile)
    public abstract MyProfileQuery myProfileQuery();

    // للوصول لعمليات جدول المهام أو البلاغات (MyTask)
    public abstract MyTaskQuery getMyTaskQuery();

    // دالة مساعدة لتبسيط استدعاء عمليات الملف الشخصي
    public MyProfileQuery getProfile() {
         return myProfileQuery();
    }
}
