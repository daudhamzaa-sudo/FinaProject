package com.example.finaproject.data.MyProfileTable;

// استيراد المكتبات الخاصة بمكتبة Room للتعامل مع قاعدة البيانات المحلية
import androidx.room.Dao; // لتعريف هذا الملف كـ "كائن وصول للبيانات" (Data Access Object)
import androidx.room.Delete; // لاستخدام وسم الحذف تلقائياً
import androidx.room.Insert; // لاستخدام وسم الإضافة تلقائياً
import androidx.room.Query; // لكتابة استعلامات SQL مخصصة
import androidx.room.Update; // لاستخدام وسم التحديث تلقائياً

import java.util.List; // لاستقبال النتائج في شكل قائمة

/**
 * واجهة (Interface) MyProfileQuery:
 * هذا الملف هو المكان الذي نكتب فيه أوامر SQL للتواصل مع جدول الملفات الشخصية (Profile).
 */
@Dao 
public interface MyProfileQuery {

    /**
     * جلب جميع الملفات الشخصية:
     * يقوم هذا الأمر بالبحث في جدول Profile وإعادة كافة السجلات المحفوظة فيه.
     */
    @Query("SELECT * FROM Profile")
    List<Profile> getAll();

    /**
     * هل المستعمل موجود حسب الايميل وكلمة السر:
     * يستخدم للتحقق من صحة بيانات تسجيل الدخول.
     */
    @Query("SELECT * FROM Profile WHERE email = :myEmail AND passw = :myPassw LIMIT 1")
    Profile checkEmailPassw(String myEmail, String myPassw);

    /**
     * فحص هل الايميل موجود من قبل:
     * يستخدم عند تسجيل حساب جديد للتأكد من عدم تكرار البريد الإلكتروني.
     */
    @Query("SELECT * FROM Profile WHERE email=:myEmail LIMIT 1")
    Profile checkEmail(String myEmail);

    /**
     * إضافة مجموعة مستعملين دفعة واحدة:
     * تستقبل مصفوفة من الكائنات وتقوم بحفظها جميعاً.
     */
    @Insert
    void insertAll(Profile... users);

    /**
     * حذف مستعمل محدد:
     * يقوم بمطابقة المفتاح الأساسي (uid) وحذف السجل المقابل له.
     */
    @Delete
    void delete(Profile user);

    /**
     * إضافة مستعمل واحد جديد:
     * تقوم هذه الدالة بإدراج كائن واحد في الجدول.
     */
    @Insert
    void insert(Profile myUser);

    /**
     * تعديل بيانات مستعمل أو قائمة مستعملين:
     * يقوم بتحديث القيم بناءً على المفتاح الأساسي (uid).
     */
    @Update
    void update(Profile...values);

}
