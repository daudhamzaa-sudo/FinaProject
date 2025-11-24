package com.example.finaproject.data.MyProfileTable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Profile {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    public String username;
    public String email;
    public int phone;
    public String passw;

    public Profile() {
        // هذا يستخدمه Room
    }

    @Ignore
    public Profile(String username, String email, String passw) {
        this.username = username;
        this.email = email;
        this.passw = passw;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "uid=" + uid +
                ", fullName='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passw='" + passw + '\'' +
                '}';
    }

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
