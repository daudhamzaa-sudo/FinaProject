package com.example.finaproject.data.MyTaskTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyTaskQuery {
    @Query("SELECT * FROM MyTask")
    List<MyTask> getAllTasks();
    @Query("SELECT * FROM MyTask WHERE id = :id LIMIT 1")
    MyTask getTaskById(long id);
    @Query("SELECT * FROM MyTask WHERE taskName = :name LIMIT 1")
    MyTask getTaskByName(String name);
    @Query("SELECT * FROM MyTask WHERE :name LIKE :name LIMIT 1")
    MyTask findByName(String name);
    @Insert
    void insertAll(MyTask... users);
    @Delete
    void delete(MyTask user);
    
    @Insert
    void insert(MyTask myTask);
    @Update
    void update(MyTask...values);


}
