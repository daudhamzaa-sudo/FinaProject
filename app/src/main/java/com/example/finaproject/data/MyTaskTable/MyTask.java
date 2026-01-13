// الحزمة (Package) التي ينتمي إليها الكلاس
package com.example.finaproject.data.MyTaskTable;

// استيراد المكتبات اللازمة من أندرويد و Room
import androidx.annotation.NonNull; // لاستخدام علامة @NonNull التي تمنع القيم الفارغة
import androidx.room.Entity;      // لتعريف الكلاس كجدول في قاعدة بيانات Room
import androidx.room.PrimaryKey;    // لتعيين حقل كمفتاح أساسي للجدول

/**
 * يمثل هذا الكلاس جدولاً باسم "MyTask" في قاعدة البيانات.
 * كل حقل (متغير) في هذا الكلاس يمثل عمودًا في هذا الجدول.
 */
@Entity
public class MyTask {

    /**
     * اسم المهمة.
     * @PrimaryKey: يحدد هذا الحقل كمفتاح أساسي (Primary Key) للجدول،
     *              مما يعني أن قيمته يجب أن تكون فريدة لكل صف ولا يمكن تكرارها.
     * @NonNull: تضمن أن هذا الحقل لا يمكن أن يكون فارغًا (null).
     */
    @PrimaryKey
    @NonNull
    public String taskName;

    /**
     * وصف تفصيلي للمهمة.
     */
    public String taskDescription;

    /**
     * تاريخ المهمة (مثال: "2024-12-25").
     */
    public String taskDate;

    /**
     * المنطقة المتعلقة بالمهمة.
     * ملاحظة: اسم المتغير "Region" قد يكون مربكًا بعض الشيء لأنه مكتوب بحرف كبير.
     * من الأفضل تسميته "region" ليتوافق مع معايير تسمية المتغيرات في جافا.
     */
    public String Region;

    /**
     * حالة المهمة (مثال: true إذا كانت مكتملة، و false إذا لم تكن).
     */
    public boolean taskStatus;


    public String imageUrl;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    // --- الدوال المساعدة (Getters and Setters) ---
    // هذه الدوال توفر طريقة آمنة ومنظمة للوصول إلى بيانات الكائن وتعديلها.

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NonNull String taskName) {
        this.taskName = taskName;
    }

    public boolean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * دالة بديلة للحصول على عنوان المهمة.
     * هي فعليًا ترجع نفس قيمة getTaskName().
     * @return اسم المهمة.
     */
    public String getTaskTitle() {
        return taskName;
    }


    /**
     * دالة toString() تقوم بإرجاع تمثيل نصي (String) للكائن.
     * هذه الدالة مفيدة جدًا في عمليات التصحيح (Debugging) لطباعة محتويات الكائن بسهولة.
     * @return سلسلة نصية تحتوي على قيم حقول الكائن.
     */
    @Override
    public String toString() {
        return "MyTask{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskDate='" + taskDate + '\'' +
                ", taskTime='" + Region + '\'' + // ملاحظة: التسمية هنا "taskTime" لكن المتغير هو Region
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }


}
