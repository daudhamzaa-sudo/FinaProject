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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        detailImage = findViewById(R.id.detail_image);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailStatus = findViewById(R.id.detail_status);

        String title = getIntent().getStringExtra("TITLE");
        String description = getIntent().getStringExtra("DESCRIPTION");
        String status = getIntent().getStringExtra("STATUS");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        if (title == null) {
            Toast.makeText(this, "Error loading report", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        detailTitle.setText(title);
        detailDescription.setText(description);
        detailStatus.setText("Status: " + status);

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