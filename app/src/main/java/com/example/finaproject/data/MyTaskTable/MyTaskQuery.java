package com.example.finaproject.data.MyTaskTable;

// استيراد أدوات مكتبة Room للتعامل مع قاعدة البيانات
import androidx.room.Dao; // لتعريف هذا الملف كـ "كائن وصول للبيانات" (Data Access Object)
import androidx.room.Delete; // لاستخدام وسم الحذف تلقائياً
import androidx.room.Insert; // لاستخدام وسم الإضافة تلقائياً
import androidx.room.Query; // لكتابة استعلامات SQL مخصصة
import androidx.room.Update; // لاستخدام وسم التحديث تلقائياً

import java.util.List; // لاستقبال النتائج في شكل قائمة

/**
 * واجهة (Interface) MyTaskQuery:
 * هذا الملف هو المكان الذي نكتب فيه أوامر SQL للتواصل مع جدول البلاغات (MyTask).
 * مكتبة Room تقوم بتحويل هذه الأوامر إلى كود برمجي حقيقي عند بناء التطبيق.
 */
@Dao
public interface MyTaskQuery {

    /**
     * جلب جميع المهام:
     * يقوم هذا الأمر بالبحث في جدول MyTask وإعادة كافة السجلات المحفوظة فيه.
     */
    @Query("SELECT * FROM MyTask")
    List<MyTask> getAllTasks();

    /**
     * جلب مهمة برقمها التعريفي (ID):
     * نستخدم :id لتمرير القيمة التي نرسلها في الدالة إلى داخل استعلام SQL.
     */
    @Query("SELECT * FROM MyTask WHERE id = :id LIMIT 1")
    MyTask getTaskById(long id);

    /**
     * جلب مهمة باسمها:
     * يبحث عن أول مهمة تطابق الاسم المدخل بالضبط.
     */
    @Query("SELECT * FROM MyTask WHERE taskName = :name LIMIT 1")
    MyTask getTaskByName(String name);

    /**
     * البحث باستخدام نمط (Like):
     * يستخدم للبحث المرن (مثلاً البحث عن أي مهمة تحتوي على كلمة معينة).
     */
    @Query("SELECT * FROM MyTask WHERE taskName LIKE :name LIMIT 1")
    MyTask findByName(String name);

    /**
     * إضافة بلاغات متعددة دفعة واحدة:
     * تستقبل مصفوفة من الكائنات وتقوم بحفظها جميعاً.
     */
    @Insert
    void insertAll(MyTask... tasks);

    /**
     * حذف بلاغ محدد:
     * يقوم بمطابقة المفتاح الأساسي (Primary Key) وحذف السجل المقابل له.
     */
    @Delete
    void delete(MyTask task);
    
    /**
     * إضافة بلاغ واحد جديد:
     * تعيد القيمة المرجعة (long) الرقم التعريفي (ID) الجديد الذي تم إنشاؤه في قاعدة البيانات.
     */
    @Insert
    long insert(MyTask myTask);

    /**
     * تحديث بيانات بلاغ موجود:
     * يقوم بتحديث القيم (مثل العنوان أو الوصف) بناءً على الرقم التعريفي.
     */
    @Update
    void update(MyTask... values);
}
