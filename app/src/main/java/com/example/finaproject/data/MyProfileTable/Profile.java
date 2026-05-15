package com.example.finaproject.data.MyProfileTable;

// استيراد المكتبات اللازمة لتعريف الجدول وحقوله في Room
import androidx.room.ColumnInfo; // لتعريف خصائص إضافية للعمود
import androidx.room.Entity;     // لتعريف الكلاس كجدول في قاعدة البيانات
import androidx.room.PrimaryKey; // لتعيين المفتاح الأساسي

import java.io.Serializable; // للسماح بتمرير بيانات المستخدم بين الشاشات

/**
 * كلاس Profile: يمثل جدول "Profile" الذي يخزن معلومات حساب المستخدم محلياً.
 */

//Serializable يعني يمكن تفكيكه
@Entity
public class Profile implements Serializable {

    /**
     * المفتاح الأساسي (uid): رقم فريد لكل مستخدم يتولد تلقائياً.
     */
    @PrimaryKey(autoGenerate = true)
    public long uid;

    // المعرف الفريد الخاص بـ Firebase (للمزامنة السحابية)
    public String kid;

    /**
     * حقل isAdmin: لتحديد هل المستخدم مدير (Admin) أم مستخدم عادي.
     * القيمة الافتراضية هي false (مستخدم عادي).
     */
    @ColumnInfo(defaultValue = "false")
    private boolean isAdmin; 

    public String username; // اسم المستخدم
    public String email;    // البريد الإلكتروني
    public String passw;    // كلمة المرور
    public int phone;       // رقم الهاتف

    /**
     * باني فارغ (Constructor): تطلبه مكتبة Room لإنشاء الكائنات.
     */
    public Profile() {
    }

    // --- دوال التحقق والضبط لصلاحية المدير ---
    
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    /**
     * دالة toString: مفيدة جداً عند طباعة بيانات المستخدم في الـ Log لفحص الأخطاء.
     */
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

    // --- دوال الـ Getters والـ Setters للوصول للبيانات وتعديلها ---

    public String getKid() { return kid; }
    public void setKid(String kid) { this.kid = kid; }

    public long getUid() { return uid; }
    public void setUid(long uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassw() { return passw; }
    public void setPassw(String passw) { this.passw = passw; }

    public int getPhone() { return phone; }
    public void setPhone(int phone) { this.phone = phone; }
}
