package com.example.finaproject.data.MyProfileTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * يمثل هذا الكلاس جدول "Profile" في قاعدة بيانات Room.
 * تم إرجاع uid لنوع long لدعم الزيادة التلقائية (autoGenerate).
 */
@Entity
public class Profile implements Serializable {

    /**
     * المعرف الفريد للمستخدم، يتم إنشاؤه تلقائياً بواسطة قاعدة البيانات.
     */
    @PrimaryKey(autoGenerate = true)
    public long uid;

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String kid;
    /**
     * التحقق مما إذا كان المستخدم يمتلك صلاحيات مدير.
     */
    @ColumnInfo(defaultValue = "false")
    private boolean isAdmin; 

    public String username;
    public String email;
    public String passw;
    public int phone;

    /**
     * باني فارغ مطلوب لمكتبة Room.
     */
    public Profile() {
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                ", passw='" + passw + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }

    // --- دوال الحصول والتعيين (Getters & Setters) ---

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassw() {
        return passw;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
