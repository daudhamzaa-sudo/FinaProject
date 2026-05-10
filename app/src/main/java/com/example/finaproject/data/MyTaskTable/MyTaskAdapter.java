package com.example.finaproject.data.MyTaskTable;

// استيراد المكتبات اللازمة لعرض القوائم، الصور، والتعامل مع Firebase
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * كلاس MyTaskAdapter: هو "الجسر" الذي يربط بين قائمة البيانات (ArrayList) وشكل العناصر في واجهة المستخدم (RecyclerView).
 */
public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.TaskViewHolder> {

    private ArrayList<MyTask> tasksList; // قائمة تخزن كافة البلاغات القادمة من قاعدة البيانات
    private Context context;            // سياق التطبيق لاستخدامه في فتح الشاشات وإظهار الرسائل
    private boolean isAdmin;            // متغير لتحديد هل المستخدم الحالي "مدير" أم لا

    /**
     * الباني (Constructor): يجهز المحول بالبيانات ويفحص صلاحية المستخدم.
     */
    public MyTaskAdapter(Context context, ArrayList<MyTask> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
        // فحص حالة الأدمن فور إنشاء المحول
        this.isAdmin = checkIfAdmin();
    }
    
    /**
     * دالة للتحقق من هوية المستخدم الحالي عبر Firebase Authentication.
     */
    private boolean checkIfAdmin() {
        try {
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && user.getEmail() != null) {
                // نعتبر المستخدم "مدير" إذا كان بريده الإلكتروني هو admin@gmail.com
                return user.getEmail().equalsIgnoreCase("admin@gmail.com");
            }
        } catch (Exception e) {
            // في حال وجود خطأ في الاتصال، نفترض أنه مستخدم عادي للأمان
        }
        return false;
    }

    /**
     * كلاس TaskViewHolder: يقوم بحفظ مراجع عناصر الواجهة لكل عنصر في القائمة (لتحسين الأداء).
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView task_title, task_description, task_status;
        ImageView task_image;
        Switch switchConfirm; // زر التبديل لحذف البلاغ (يظهر للمدير فقط)

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            // ربط المتغيرات بالعناصر في ملف xml الخاص بالعنصر الواحدة (task_item_layout)
            task_status = itemView.findViewById(R.id.task_status);
            task_description = itemView.findViewById(R.id.task_description);
            task_title = itemView.findViewById(R.id.task_title);
            task_image = itemView.findViewById(R.id.task_image);
            switchConfirm = itemView.findViewById(R.id.switchConfirm);
        }
    }

    /**
     * دالة onCreateViewHolder: تنشئ شكلاً جديداً للعنصر في القائمة عند الحاجة.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تحويل ملف الـ XML (task_item_layout) إلى كائن مرئي (View)
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new TaskViewHolder(itemView);
    }

    /**
     * دالة onBindViewHolder: تضع البيانات الحقيقية داخل عناصر الواجهة لكل عنصر في القائمة.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        MyTask current = tasksList.get(position); // جلب بيانات البلاغ بناءً على موقعه في القائمة
        
        // تعبئة النصوص
        holder.task_status.setText("الحالة: " + (current.getTaskStatus() ? "مكتمل" : "قيد المعالجة"));
        holder.task_description.setText(current.getTaskDescription());
        holder.task_title.setText(current.getTaskTitle());

        // منطق التعامل مع صلاحيات المدير (Admin)
        if (isAdmin) {
            holder.switchConfirm.setVisibility(View.VISIBLE); // إظهار زر الحذف
            holder.switchConfirm.setChecked(false);           // التأكد أنه غير مفعل افتراضياً
            
            // عند قيام المدير بتفعيل المفتاح (Switch) يتم حذف البلاغ
            holder.switchConfirm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    deleteTask(current, position);
                }
            });
        } else {
            holder.switchConfirm.setVisibility(View.GONE); // إخفاء زر الحذف للمستخدم العادي
        }

        // تحميل صورة البلاغ إذا كانت موجودة باستخدام مكتبة Glide
        if (current.getImageUrl() != null && !current.getImageUrl().isEmpty()) {
            holder.task_image.setVisibility(View.VISIBLE);
            Glide.with(context).load(current.getImageUrl()).into(holder.task_image);
        } else {
            holder.task_image.setVisibility(View.GONE); // إخفاء عنصر الصورة إذا لم توجد صورة
        }

        // عند النقر على البلاغ بالكامل، ننتقل لشاشة التفاصيل
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailsActivity.class);
            intent.putExtra("TASK_EXTRA", current); // تمرير كائن البلاغ بالكامل
            context.startActivity(intent);
        });
    }

    /**
     * دالة حذف البلاغ من قاعدة بيانات Firebase السحابية.
     */
    private void deleteTask(MyTask task, int position) {
        if (task.getKid() != null) {
            // الوصول لفرع "tasks" وحذف السجل الذي يطابق المفتاح (kid)
            FirebaseDatabase.getInstance().getReference("tasks")
                    .child(task.getKid())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "تم حذف البلاغ نهائياً", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "فشل الحذف: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * جلب عدد العناصر الإجمالي في القائمة.
     */
    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    /**
     * دالة لتحديث القائمة بالكامل عند جلب بيانات جديدة من الإنترنت.
     */
    public void setTasksList(ArrayList<MyTask> tasksList) {
        this.tasksList = tasksList;
        notifyDataSetChanged(); // إبلاغ القائمة بضرورة إعادة الرسم (Refresh)
    }

    public ArrayList<MyTask> getTasksList() {
        return tasksList;
    }
}
