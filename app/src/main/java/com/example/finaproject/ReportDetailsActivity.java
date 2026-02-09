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
        detailLocation=findViewById(R.id.detail_location);
        // استلام البيانات من Intent
        String title = getIntent().getStringExtra("TITLE");
        String description = getIntent().getStringExtra("DESCRIPTION");
        String status = getIntent().getStringExtra("STATUS");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        double lat = getIntent().getDoubleExtra("LAT", 0);
        double lon = getIntent().getDoubleExtra("LON", 0);
        // تحقق
        if (title == null) {
            Toast.makeText(this, "لم يتم استلام بيانات البلاغ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // عرض البيانات
        detailTitle.setText(title);
        detailDescription.setText(description);
        detailStatus.setText(status);
        detailTitle.setText(title);
        detailDescription.setText(description);
        detailStatus.setText(status);

        if (lat != 0 && lon != 0) {
            detailLocation.setVisibility(View.VISIBLE);
            detailLocation.setText("Location: " + lat + ", " + lon);
        } else {
            detailLocation.setVisibility(View.GONE); // أو خليها رسالة بدل الإخفاء
        }
        // تحميل الصورة (بدون ما تخرب)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            detailImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(detailImage);
        } else {
            detailImage.setVisibility(View.GONE);
        }
    }
}