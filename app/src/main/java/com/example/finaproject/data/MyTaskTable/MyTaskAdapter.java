package com.example.finaproject.data.MyTaskTable;

import android.content.Context;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finaproject.R;
import java.util.List;

/**
 * Adapter لتوصيل قائمة من كائنات MyTask مع RecyclerView.
 */
public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.MyViewHolder> {

    private final Context context;
    private final List<MyTask> tasks;

    /**
     * Constructor للـ adapter.
     * @param context الـ Context الذي يتم استخدام الـ adapter فيه.
     * @param tasks قائمة المهام التي سيتم عرضها.
     */
    public MyTaskAdapter(Context context, List<MyTask> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    /**
     * يتم استدعاء هذه الدالة عندما يحتاج RecyclerView إلى ViewHolder جديد.
     * تقوم بتضخيم (inflate) الـ layout الخاص بعنصر واحد في القائمة.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكد من أن اسم الملف `task_item_layout.xml` صحيح
        View view = LayoutInflater.from(context).inflate(R.layout.task_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * يتم استدعاء هذه الدالة لعرض البيانات في موضع محدد.
     * تقوم بربط البيانات من كائن MyTask مع الـ views الموجودة في ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // احصل على كائن البيانات للموضع الحالي
        MyTask currentTask = tasks.get(position);

        // قم بتعيين البيانات إلى الـ views بشكل صحيح
        holder.taskTitle.setText(currentTask.getTaskName());
        holder.taskDescription.setText(currentTask.getTaskDescription());
        holder.taskStatus.setText(currentTask.getTaskStatus()); // تم تصحيح هذا السطر

        // منطق لعرض أيقونة مختلفة بناءً على أولوية المهمة

         {
            //holder.taskImage.setImageResource(R.drawable.ic_priority_low); // تأكد من وجود هذه الصورة
        }
    }

    /**
     * تُرجع العدد الإجمالي للعناصر في مجموعة البيانات.
     */
    @Override
    public int getItemCount() {
        if (tasks == null) {
            return 0;
        }
        return tasks.size();
    }

    /**
     * كلاس الـ ViewHolder يحتفظ بمراجع الـ views لعنصر واحد في القائمة.
     * هذا يحسن الأداء عن طريق تجنب الاستدعاءات المتكررة لـ findViewById().
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView taskImage;
        public TextView taskTitle;
        public TextView taskDescription;
        public TextView taskStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // ابحث عن الـ views من الـ layout الذي تم تضخيمه
            taskImage = itemView.findViewById(R.id.task_image);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskStatus = itemView.findViewById(R.id.task_status);
        }
    }
}


//public class MyTaskAdapter extends ArrayAdapter<MyTask>
//{
//    private final int itemlayout;
//
//    public MyTaskAdapter(@NonNull Context context, int resource) {
//        super(context, resource);
//        this.itemlayout = resource;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View vitem= convertView;
//        if(vitem==null)
//            vitem= LayoutInflater.from(getContext()).inflate(itemlayout,parent,false);
//        ImageView taskImage = vitem.findViewById(R.id.task_image);
//        TextView taskTitle = vitem.findViewById(R.id.task_title);
//        TextView taskDescription = vitem.findViewById(R.id.task_description);
//        TextView taskStatus = vitem.findViewById(R.id.task_status);
//
////קבלת הנתון (עצם) הנוכחי
//        MyTask current=getItem(position);
//        //הצגת הנתונים על שדות הרכיב הגרפי
//        //taskTitle.setText(current.getShortTitle());
//       // taskDescription.setText(current.getText());
//      //  taskStatus.setText("Importance:"+current.getImportance());
//
//
//        return vitem;
//
//        //return super.getView(position, convertView, parent);
//    }

