package com.example.finaproject.data.MyTaskTable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finaproject.R;
import com.example.finaproject.ReportDetailsActivity;
import com.example.finaproject.data.AppDatabase;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * المحول (Adapter) الخاص بعرض قائمة البلاغات.
 * تم تعديله ليتيح للأدمن فقط حذف البلاغات عند تفعيل مفتاح التأكيد.
 */
public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.TaskViewHolder> {

    private ArrayList<MyTask> tasksList;
    private Context context;
    private boolean isAdmin;

    public MyTaskAdapter(Context context, ArrayList<MyTask> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
        // قراءة حالة الأدمن من الإعدادات
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.isAdmin = prefs.getBoolean("IS_ADMIN", false);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task_title, task_description, task_status;
        ImageView task_image;
        Switch switchConfirm;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_status = itemView.findViewById(R.id.task_status);
            task_description = itemView.findViewById(R.id.task_description);
            task_title = itemView.findViewById(R.id.task_title);
            task_image = itemView.findViewById(R.id.task_image);
            switchConfirm = itemView.findViewById(R.id.switchConfirm);
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
        
        holder.task_status.setText("Status: " + current.getTaskStatus());
        holder.task_description.setText(current.getTaskDescription());
        holder.task_title.setText(current.getTaskTitle());

        // إظهار/إخفاء زر الحذف بناءً على صلاحية الأدمن
        if (isAdmin) {
            holder.switchConfirm.setVisibility(View.VISIBLE);
            holder.switchConfirm.setChecked(false); // إعادة ضبط الحالة لتجنب أخطاء إعادة التدوير
            
            // مستمع لتغيير حالة الـ Switch (تأكيد الحذف)
            holder.switchConfirm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    deleteTask(current, position);
                }
            });
        } else {
            holder.switchConfirm.setVisibility(View.GONE);
        }

        // تحميل الصورة
        if (current.getImageUrl() != null && !current.getImageUrl().isEmpty()) {
            holder.task_image.setVisibility(View.VISIBLE);
            Glide.with(context).load(current.getImageUrl()).into(holder.task_image);
        } else {
            holder.task_image.setVisibility(View.GONE);
        }

        // فتح التفاصيل عند النقر
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailsActivity.class);
            intent.putExtra("TASK_EXTRA", current);
            context.startActivity(intent);
        });
    }

    /**
     * دالة لحذف البلاغ من Firebase ومن قاعدة البيانات المحلية.
     */
    private void deleteTask(MyTask task, int position) {
        // 1. الحذف من Firebase باستخدام الـ kid
        if (task.getKid() != null) {
            FirebaseDatabase.getInstance().getReference("tasks")
                    .child(task.getKid())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "تم حذف البلاغ من السيرفر", Toast.LENGTH_SHORT).show();
                    });
        }

        // 2. الحذف من قاعدة البيانات المحلية (Room)
        new Thread(() -> {
            AppDatabase.getdb(context).getMyTaskQuery().delete(task);
        }).start();

        // 3. تحديث القائمة في الواجهة
        tasksList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasksList.size());
        Toast.makeText(context, "تم تأكيد وحذف البلاغ بنجاح", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void setTasksList(ArrayList<MyTask> tasksList) {
        this.tasksList = tasksList;
        notifyDataSetChanged();
    }

    public ArrayList<MyTask> getTasksList() {
        return tasksList;
    }
}
