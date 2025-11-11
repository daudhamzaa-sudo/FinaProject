package com.example.finaproject.data.MyTaskTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyTask {
    @PrimaryKey
    public long keyid;
    public String taskName;
    public String taskDescription;
    public String taskDate;
    public String taskTime;
    public String taskPriority;
    public String taskStatus;

    @Override
    public String toString() {
        return "MyTask{" +
                "keyid=" + keyid +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskDate='" + taskDate + '\'' +
                ", taskTime='" + taskTime + '\'' +
                ", taskPriority='" + taskPriority + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }

    public long getKeyid() {
        return keyid;
    }

    public void setKeyid(long keyid) {
        this.keyid = keyid;
    }

    public MyTask(long keyid) {
        this.keyid = keyid;
    }
}
