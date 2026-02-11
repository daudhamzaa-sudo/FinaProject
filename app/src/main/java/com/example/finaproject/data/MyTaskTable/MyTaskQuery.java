package com.example.finaproject.data.MyTaskTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * واجهة (Interface) لتعريف عمليات الاستعلام الخاصة بجدول المهام (MyTask) في قاعدة بيانات Room.
 */
@Dao
public interface MyTaskQuery {
    /**
     * جلب جميع المهام من قاعدة البيانات.
     * @return قائمة بجميع المهام.
     */
    @Query("SELECT * FROM MyTask")
    List<MyTask> getAllTasks();

    /**
     * جلب مهمة محددة باستخدام الرقم التعريفي (ID).
     * @param id الرقم التعريفي للمهمة.
     * @return كائن المهمة.
     */
    @Query("SELECT * FROM MyTask WHERE id = :id LIMIT 1")
    MyTask getTaskById(long id);

    /**
     * جلب مهمة محددة باستخدام اسمها.
     * @param name اسم المهمة.
     * @return كائن المهمة.
     */
    @Query("SELECT * FROM MyTask WHERE taskName = :name LIMIT 1")
    MyTask getTaskByName(String name);

    /**
     * البحث عن مهمة باستخدام نمط معين للاسم.
     * @param name نمط الاسم المراد البحث عنه.
     * @return كائن المهمة الأول الذي يطابق البحث.
     */
    @Query("SELECT * FROM MyTask WHERE :name LIKE :name LIMIT 1")
    MyTask findByName(String name);

    /**
     * إدراج مجموعة من المهام دفعة واحدة.
     * @param users مصفوفة من المهام.
     */
    @Insert
    void insertAll(MyTask... users);

    /**
     * حذف مهمة محددة من قاعدة البيانات.
     * @param user كائن المهمة المراد حذفه.
     */
    @Delete
    void delete(MyTask user);
    
    /**
     * إدراج مهمة واحدة جديدة.
     * @param myTask كائن المهمة.
     */
    @Insert
    void insert(MyTask myTask);

    /**
     * تحديث بيانات مهمة أو أكثر موجودة مسبقاً.
     * @param values المهام المراد تحديثها.
     */
    @Update
    void update(MyTask...values);
}
