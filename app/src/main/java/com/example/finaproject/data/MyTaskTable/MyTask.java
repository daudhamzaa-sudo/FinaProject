package com.example.finaproject.data.MyTaskTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyTask {
    @PrimaryKey

    public String taskName;
    public String taskDescription;
    public String taskDate;
    public String taskTime;
    public String taskPriority;
    public String taskStatus;

    @Override
    public String toString() {
        return "MyTask{" +

                ", taskName='" + taskName + '\'' +
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

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public MyTask(String taskName,
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
