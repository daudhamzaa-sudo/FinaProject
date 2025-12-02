package com.example.finaproject.data.MyTaskTable;

import androidx.annotation.NonNull; // <-- Import the NonNull annotation
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyTask {
    @PrimaryKey@NonNull // <-- Add this annotation to the primary key
    public String taskName;
    public String taskDescription;
    public String taskDate;
    public String Region;
    //public Image taskPriority;
    public boolean taskStatus;

    @Override
    public String toString() {
        return "MyTask{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskDate='" + taskDate + '\'' +
                ", taskTime='" + Region + '\'' +

                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NonNull String taskName) { // <-- Also good practice to add it here
        this.taskName = taskName;
    }





}
