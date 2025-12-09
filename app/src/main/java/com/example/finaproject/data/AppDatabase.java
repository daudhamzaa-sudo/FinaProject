package com.example.finaproject.data;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finaproject.data.MyTaskTable.MyTask;
import com.example.finaproject.data.MyTaskTable.MyTaskQuery;
import com.example.finaproject.data.MyProfileTable.Profile;
import com.example.finaproject.data.MyProfileTable.MyProfileQuery;


@Database(entities = {Profile.class, MyTask.class}, version = 1)
    public abstract class AppDatabase extends RoomDatabase {
private static AppDatabase db;

    public static AppDatabase getDB(Application application) {
        if (db == null) {
            db = Room.databaseBuilder(application,
                    AppDatabase.class,"HamzaDataBase")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }

    public static Object getInstance(Context applicationContext) {
        return null;
    }

    public abstract MyProfileQuery myProfileQuery();

public abstract MyTaskQuery getMyTaskQuery();

    public static AppDatabase getDb() {
        return db;
    }

    public static void setDb(AppDatabase db) {
        AppDatabase.db = db;
    }

    public static AppDatabase getdb(Context context) {
    if (db == null) {
        db = Room.databaseBuilder(context,
                AppDatabase.class,"HamzaDataBase")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
    return db;
}

    public MyProfileQuery getProfile() {
         return myProfileQuery();
    }

}




