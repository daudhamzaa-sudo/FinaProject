package com.example.finaproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ReportDetailsActivity extends AppCompatActivity {

    private ImageView detailImage;
    private TextView detailTitle;
    private TextView detailDescription;
    private TextView detailStatus;
    private TextView detailLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        // ربط العناصر من XML
        detailImage = findViewById(R.id.detail_image);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailStatus = findViewById(R.id.detail_status);
        detailLocation = findViewById(R.id.detail_location);

        // --- الكود المصحح ---

        // أولاً، تحقق من وجود معرّف البلاغ، فهو الأهم
        long taskId = getIntent().getLongExtra("TASK_id", -1L); // نستخدم نفس المفتاح من MainActivity

        // التحقق من أننا استلمنا المعرّف بنجاح
        if (taskId == -1L) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرّف البلاغ.", Toast.LENGTH_SHORT).show();
            finish(); // أغلق الشاشة إذا لم نجد المعرّف
            return;
        }

        // بما أن المعرّف موجود، يمكننا جلب باقي البيانات
        String title = getIntent().getStringExtra("TITLE");
        String description = getIntent().getStringExtra("DESCRIPTION");
        String status = getIntent().getStringExtra("STATUS");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        double lat = getIntent().getDoubleExtra("LAT", 0);
        double lon = getIntent().getDoubleExtra("LON", 0);

        // عرض البيانات (تم إزالة التكرار)
        detailTitle.setText(title);
        detailDescription.setText(description);
        detailStatus.setText(status);

        // عرض بيانات الموقع إن وجدت
        if (lat != 0 && lon != 0) {
            detailLocation.setVisibility(View.VISIBLE);
            detailLocation.setText("الموقع: " + lat + ", " + lon);
        } else {
            detailLocation.setVisibility(View.GONE);
        }

        // تحميل الصورة باستخدام Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            detailImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // صورة مؤقتة
                    .error(R.drawable.ic_launcher_foreground) // صورة عند الخطأ
                    .into(detailImage);
        } else {
            detailImage.setVisibility(View.GONE);
        }
    }
}
