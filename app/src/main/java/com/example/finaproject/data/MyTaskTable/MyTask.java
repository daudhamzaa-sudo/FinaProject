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
    public String taskTime;
    public String taskPriority;
    public String taskStatus;

    @Override
    public String toString() {
        return "MyTask{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskDate='" + taskDate + '\'' +
                ", taskTime='" + taskTime + '\'' +
                ", taskPriority='" + taskPriority + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NonNull String taskName) { // <-- Also good practice to add it here
        this.taskName = taskName;
    }

    public MyTask(@NonNull String taskName, // <-- And in the constructor parameter
                  String taskDescription, String taskDate, String taskTime, String taskPriority, String taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.taskPriority = taskPriority;
        this.taskStatus = taskStatus;
    }

    public MyTask() {
    }
}
