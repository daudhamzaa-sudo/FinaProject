package com.example.finaproject.data;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskQuery;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.example.finaproject.data.MyProfileTable.MyProfileQuery;

/**
 * فئة قاعدة بيانات التطبيق (AppDatabase) باستخدام مكتبة Room.
 * تقوم هذه الفئة بتحويل كائنات Java (Entities) إلى جداول في قاعدة بيانات SQL.
 */
// تعريف الكيانات (Entities) المشمولة في قاعدة البيانات وإصدارها.
@Database(entities = {Profile.class, MyTask.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {

    // متغير ثابت يمثل نسخة واحدة من قاعدة البيانات (Singleton Pattern)
    private static AppDatabase db;

    /**
     * دالة للحصول على نسخة من قاعدة البيانات باستخدام Application context.
     * @param application سياق التطبيق.
     * @return نسخة من AppDatabase.
     */
    public static AppDatabase getDB(Application application) {
        if (db == null) {
            db = Room.databaseBuilder(application,
                    AppDatabase.class,"HamzaDataBase")
                    .fallbackToDestructiveMigration() // يسمح بمسح البيانات عند تغيير إصدار قاعدة البيانات دون وجود خطة هجرة
                    .allowMainThreadQueries()         // يسمح بتنفيذ الاستعلامات على خيط الواجهة الرئيسي (للتسهيل)
                    .build();
        }
        return db;
    }

    /**
     * دالة غير مكتملة حالياً للحصول على نسخة (تم إرجاع null حسب الكود الأصلي).
     */
    public static Object getInstance(Context applicationContext) {
        return null;
    }

    /**
     * طريقة مجردة (Abstract) للوصول إلى واجهة الاستعلامات الخاصة بالملف الشخصي.
     */
    public abstract MyProfileQuery myProfileQuery();

    /**
     * طريقة مجردة (Abstract) للوصول إلى واجهة الاستعلامات الخاصة بالمهام.
     */
    public abstract MyTaskQuery getMyTaskQuery();

    /**
     * دالة ثابتة للحصول على النسخة الحالية من قاعدة البيانات.
     */
    public static AppDatabase getDb() {
        return db;
    }

    /**
     * دالة لتعيين نسخة قاعدة البيانات يدوياً.
     */
    public static void setDb(AppDatabase db) {
        AppDatabase.db = db;
    }

    /**
     * دالة أخرى للحصول على قاعدة البيانات باستخدام Context عادي.
     * @param context السياق.
     * @return نسخة من AppDatabase.
     */
    public static AppDatabase getdb(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context,
                    AppDatabase.class,"HamzaDataBase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }

    /**
     * دالة مساعدة للوصول السريع إلى استعلامات الملف الشخصي.
     */
    public MyProfileQuery getProfile() {
         return myProfileQuery();
    }
}
