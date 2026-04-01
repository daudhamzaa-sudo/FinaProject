// الحزمة (Package) التي ينتمي إليها الكلاس
package com.example.finaproject.data.MyTaskTable;

// استيراد المكتبات اللازمة من أندرويد و Room
import androidx.annotation.NonNull; // لاستخدام علامة @NonNull التي تمنع القيم الفارغة
import androidx.room.Entity;      // لتعريف الكلاس كجدول في قاعدة بيانات Room
import androidx.room.PrimaryKey;    // لتعيين حقل كمفتاح أساسي للجدول

import java.io.Serializable;

/**
 * يمثل هذا الكلاس جدولاً باسم "MyTask" في قاعدة البيانات.
 * تم تحديثه ليتضمن دالة getKid() اللازمة لعملية الحذف.
 */
@Entity
public class MyTask implements Serializable {

    @PrimaryKey(autoGenerate = true)
    long id;
    
    @NonNull
    public String taskName = "";

    public String kid;

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String taskDescription;
    private double latitude;
    private double longitude;
    public String taskDate;
    public String Region;
    public boolean taskStatus;
    public String imageUrl;

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

    public String getTaskTitle() {
        return taskName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
