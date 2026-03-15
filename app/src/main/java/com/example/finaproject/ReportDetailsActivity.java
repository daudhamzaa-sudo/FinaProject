package com.example.finaproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.finaproject.data.MyTaskTable.MyTask;

/**
 * شاشة تفاصيل البلاغ: تم تحسينها لضمان استلام البيانات بشكل صحيح وتفادي خطأ "Not Found".
 */
public class ReportDetailsActivity extends AppCompatActivity {

    private ImageView detailImage;
    private TextView detailTitle, detailDescription, detailStatus, detailLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        // ربط عناصر الواجهة
        detailImage = findViewById(R.id.detail_image);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailStatus = findViewById(R.id.detail_status);
        detailLocation = findViewById(R.id.detail_location);

        // محاولة استلام الكائن بطرق مختلفة لضمان النجاح
        MyTask task = null;
        if (getIntent().hasExtra("TASK_EXTRA")) {
            task = (MyTask) getIntent().getSerializableExtra("TASK_EXTRA");
        }

        if (task != null) {
            // ملء البيانات
            detailTitle.setText(task.getTaskTitle());
            detailDescription.setText(task.getTaskDescription());
            detailStatus.setText("Status: " + task.getTaskStatus());

            if (task.getLatitude() != 0 || task.getLongitude() != 0) {
                detailLocation.setText("Location: " + task.getLatitude() + " , " + task.getLongitude());
                detailLocation.setVisibility(View.VISIBLE);
            }

            if (task.getImageUrl() != null && !task.getImageUrl().isEmpty()) {
                Glide.with(this).load(task.getImageUrl()).into(detailImage);
                detailImage.setVisibility(View.VISIBLE);
            }
        } else {
            // طباعة خطأ في الـ Log للمساعدة في التتبع
            Log.e("DetailsError", "The Intent does not contain TASK_EXTRA");
            Toast.makeText(this, "خطأ: لم يتم استلام بيانات البلاغ", Toast.LENGTH_LONG).show();
            // finish(); // اختيارياً: إغلاق الشاشة إذا لم تتوفر بيانات
        }
    }
}
