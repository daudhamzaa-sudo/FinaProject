package com.example.finaproject.data.MyTaskTable;

// استيراد المكتبات اللازمة لعمل قاعدة بيانات Room والتعامل مع البيانات
import androidx.annotation.NonNull; // للإشارة إلى أن الحقل لا يمكن أن يكون فارغاً
import androidx.room.Entity;      // لتعريف هذا الكلاس كجدول في قاعدة البيانات
import androidx.room.PrimaryKey;    // لتحديد المفتاح الأساسي (المعرف الوحيد) لكل سجل

import java.io.Serializable; // للسماح بتمرير الكائن بالكامل بين الشاشات عبر Intent

/**
 * كلاس الكيان (Entity): يمثل هيكل جدول "MyTask" في قاعدة بيانات Room المحلية.
 * نستخدم "Serializable" لكي نتمكن من إرسال بيانات البلاغ من القائمة إلى شاشة التفاصيل بسهولة.
 */
@Entity
public class MyTask implements Serializable {

    // تحديد المعرف (ID) كمفتاح أساسي وجعله يتولد تلقائياً (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    long id;

    // متغير لتخزين وقت التذكير بالملي ثانية
    private long reminderTime;

    // اسم المهمة أو عنوان البلاغ (لا يمكن أن يكون فارغاً)
    @NonNull
    public String taskName = "";

    // المعرف الخاص بالبلاغ في قاعدة بيانات Firebase (للمزامنة السحابية)
    public String kid;

    // دوال الـ Getter والـ Setter للوصول وتعديل البيانات الخاصة بالمنبه
    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    // دوال الوصول للمعرف السحابي (Firebase Key)
    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    // وصف تفصيلي للمشكلة أو المهمة
    public String taskDescription;

    // إحداثيات الموقع الجغرافي (خط العرض وخط الطول)
    private double latitude;
    private double longitude;

    // تاريخ إنشاء أو تنفيذ المهمة
    public String taskDate;

    // المنطقة الجغرافية التابع لها البلاغ
    public String Region;

    // حالة المهمة (true = مكتملة، false = قيد الانتظار)
    public boolean taskStatus;

    // رابط الصورة المخزنة (سواء في الهاتف أو في السحاب)
    public String imageUrl;

    // دوال ضبط وجلب البيانات (Setters & Getters):
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }

    // دالة إضافية لجلب العنوان (مفيدة في العرض)
    public String getTaskTitle() {
        return taskName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * دالة toString: تستخدم لتحويل الكائن إلى نص، مفيدة جداً عند فحص الأخطاء (Debugging).
     */
    @Override
    public String toString() {
        return "MyTask{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", kid='" + kid + '\'' +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
