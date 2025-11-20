package com.example.finaproject.data.MyProfileTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyProfQr {
    @Query("SELECT * FROM Profile")
    List<Profile> getAll();

    @Query("SELECT * FROM Profile WHERE email = :myEmail AND passw = :myPassw LIMIT 1")
    Profile checkEmailPassw(String myEmail, String myPassw);

    @Query("SELECT * FROM Profile WHERE email=:myEmail LIMIT 1")
    Profile checkEmail(String myEmail);

    @Insert
// اضافة مستعمل او مجموعة مستعملين
    void insertAll(Profile... users);

    @Delete
// حذف
    void delete(Profile user);

    //حذف حسب الرقم المميز id

    @Insert//اضافة مستعمل واحد
    void insert(Profile myUser);

    @Update
        //تعديل مستعمل او قائمة مستعملين
    void update(Profile...values);
}
