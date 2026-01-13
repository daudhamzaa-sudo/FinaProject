package com.example.finaproject.data.MyTaskTable;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaproject.R;
import com.example.finaproject.data.MyTaskTable.MyTask;

import java.util.ArrayList;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.TaskViewHolder> {

    private ArrayList<MyTask> tasksList;//רשימת הנתונים שנציג
    private Context context;// הפניה למסך שיציג את רשימת הפריטים
    //פעולה בונה למתאם
    public MyTaskAdapter(Context context, ArrayList<MyTask> tasksList) {
        this.context = context;
        this.tasksList = tasksList;;
    }


    public class TaskViewHolder  extends RecyclerView.ViewHolder
    {
        //שדות של עיצוב הפריט task_item_layout
        TextView task_title;
        TextView task_description;
        TextView task_status;
        ImageView task_image;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_status = itemView.findViewById(R.id.task_status);
            task_description = itemView.findViewById(R.id.task_description);
            task_title = itemView.findViewById(R.id.task_title);
            task_image= itemView.findViewById(R.id.task_image);
        }
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new TaskViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        MyTask current = tasksList.get(position);
        holder.task_status.setText(""+current.getTaskStatus());
        holder.task_description.setText(current.getTaskDescription());
        holder.task_title.setText("Importance:" + current.getTaskTitle());
//        if (current.getImageUrl()!=null)
//            holder.task_image.setImageURI(new Uri( current.getImageUrl()));
    }


    @Override
    public int getItemCount() {
        return tasksList.size();
    }


    public void setTasksList(ArrayList<MyTask> tasksList) {
        this.tasksList = tasksList;
        notifyDataSetChanged();// מודיעים למתאם שחל שינוי וצריך להבנות מחדש
    }
}

