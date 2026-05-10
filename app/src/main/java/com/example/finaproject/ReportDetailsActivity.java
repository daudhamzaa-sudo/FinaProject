package com.example.finaproject;

// استيراد المكتبات اللازمة لعرض النصوص والصور والتعامل مع البيانات
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide; // مكتبة خارجية لتحميل الصور من الروابط بكفاءة عالية
import com.example.finaproject.data.MyTaskTable.MyTask;

/**
 * شاشة تفاصيل البلاغ: تعرض كافة المعلومات المتعلقة ببلاغ معين اختاره المستخدم.
 */
public class ReportDetailsActivity extends AppCompatActivity {

    // تعريف متغيرات عناصر الواجهة
    private ImageView detailImage;      // لعرض الصورة المرفقة بالبلاغ
    private TextView detailTitle;       // لعرض عنوان البلاغ
    private TextView detailDescription; // لعرض وصف المشكلة
    private TextView detailStatus;      // لعرض حالة البلاغ (مكتمل أو قيد الانتظار)
    private TextView detailLocation;    // لعرض الإحداثيات الجغرافية

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // تعيين ملف التصميم (Layout) الخاص بهذه الشاشة
        setContentView(R.layout.activity_report_details);

        // ربط عناصر الواجهة بمتغيرات الجافا عبر المعرفات (IDs)
        detailImage = findViewById(R.id.detail_image);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailStatus = findViewById(R.id.detail_status);
        detailLocation = findViewById(R.id.detail_location);

        // استلام كائن البلاغ (MyTask) الذي تم إرساله من القائمة الرئيسية
        MyTask task = null;
        // فحص ما إذا كان الـ Intent يحتوي على البيانات المطلوبة
        if (getIntent().hasExtra("TASK_EXTRA")) {
            // استخراج الكائن (يجب أن يكون Serializable لكي يتم نقله بين الشاشات)
            task = (MyTask) getIntent().getSerializableExtra("TASK_EXTRA");
        }

        // في حال نجاح استلام البيانات، نبدأ بعرضها في الواجهة
        if (task != null) {
            // تعيين النصوص في حقول العرض
            detailTitle.setText(task.getTaskTitle());
            detailDescription.setText(task.getTaskDescription());
            // عرض الحالة بناءً على قيمة المتغير المنطقي (boolean)
            detailStatus.setText("الحالة: " + (task.getTaskStatus() ? "مكتمل" : "قيد المعالجة"));

            // إذا كانت هناك إحداثيات موقع مسجلة، نقوم بعرضها
            if (task.getLatitude() != 0 || task.getLongitude() != 0) {
                detailLocation.setText("الموقع: " + task.getLatitude() + " , " + task.getLongitude());
                detailLocation.setVisibility(View.VISIBLE); // جعل عنصر النص مرئياً
            }

            // إذا كان هناك رابط صورة محفوظ، نستخدم مكتبة Glide لتحميلها وعرضها
            if (task.getImageUrl() != null && !task.getImageUrl().isEmpty()) {
                Glide.with(this).load(task.getImageUrl()).into(detailImage);
                detailImage.setVisibility(View.VISIBLE); // إظهار الصورة للمستخدم
            }
        } else {
            // في حالة حدوث خطأ تقني في نقل البيانات
            Log.e("DetailsError", "لم يتم العثور على بيانات TASK_EXTRA");
            Toast.makeText(this, "خطأ: فشل تحميل بيانات البلاغ", Toast.LENGTH_LONG).show();
        }
    }
}
