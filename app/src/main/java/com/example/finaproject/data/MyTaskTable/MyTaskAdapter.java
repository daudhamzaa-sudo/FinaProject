package com.example.finaproject.data.MyTaskTable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch; // استيراد الـ Switch
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.finaproject.R;
import com.example.finaproject.ReportDetailsActivity;

import java.util.ArrayList;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.TaskViewHolder> {

    private ArrayList<MyTask> tasksList;
    private Context context;
    private boolean isAdmin; // متغير لتخزين صلاحية المدير

    // الدالة البناءة للمحوّل (Constructor)
    public MyTaskAdapter(Context context, ArrayList<MyTask> tasksList) {
        this.context = context;
        this.tasksList = tasksList;

        // --- تم نقل الكود إلى هنا (المكان الصحيح) ---
        // قراءة صلاحية المدير من الذاكرة المؤقتة
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        this.isAdmin = prefs.getBoolean("IS_ADMIN", false);
    }

    // الـ ViewHolder الذي يحمل عناصر الواجهة لكل عنصر في القائمة
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task_title;
        TextView task_description;
        TextView task_status;
        ImageView task_image;
        Switch switchConfirm; // زر تأكيد الحذف (للمدير)

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            task_status = itemView.findViewById(R.id.task_status);
            task_description = itemView.findViewById(R.id.task_description);
            task_title = itemView.findViewById(R.id.task_title);
            task_image = itemView.findViewById(R.id.task_image);
            // تأكد من أن لديك Switch في ملف التصميم بهذا الـ ID
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
        holder.task_status.setText("" + current.getTaskStatus());
        holder.task_description.setText(current.getTaskDescription());
        holder.task_title.setText("Importance:" + current.getTaskTitle());

        // --- المنطق الجديد للتحكم بظهور زر التأكيد ---
        if (isAdmin) {
            // إذا كان المستخدم هو المدير
            holder.switchConfirm.setVisibility(View.VISIBLE); // أظهر الزر
        } else {
            // إذا كان المستخدم عادياً
            holder.switchConfirm.setVisibility(View.GONE); // أخفِ الزر
        }

        // التحقق من وجود رابط للصورة
        if (current.getImageUrl() != null && !current.getImageUrl().isEmpty()) {
            holder.task_image.setVisibility(View.VISIBLE);
            // استخدام مكتبة Glide لتحميل الصورة
            Glide.with(context)
                    .load(current.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.task_image);
        } else {
            // إخفاء الصورة في حال عدم وجود رابط
            holder.task_image.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), ReportDetailsActivity.class);

            // نمرر كل تفاصيل البلاغ
            intent.putExtra("TITLE", current.getTaskTitle());
            intent.putExtra("DESCRIPTION", current.getTaskDescription());
            intent.putExtra("STATUS", current.getTaskStatus());
            intent.putExtra("IMAGE_URL", current.getImageUrl());
            intent.putExtra("LAT", current.getLatitude());
            intent.putExtra("LON", current.getLongitude());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void setTasksList(ArrayList<MyTask> tasksList) {
        this.tasksList = tasksList;
        notifyDataSetChanged();
    }
    /**
     * دالة لإرجاع قائمة البلاغات الحالية الموجودة في الـ Adapter.
     * @return قائمة من نوع ArrayList<MyTask>
     */
    public ArrayList<MyTask> getTasksList() {
        return tasksList; // 'tasksList' هو اسم القائمة الموجودة لديك بالفعل في الـ Adapter
    }

}
