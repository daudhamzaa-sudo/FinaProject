package com.example.finaproject;

// import android.net.Uri; // تعطيل مؤقت
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;// import android.view.View; // تعطيل مؤقت
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// import com.bumptech.glide.Glide; // تعطيل مؤقت
import com.bumptech.glide.Glide;
import com.example.finaproject.data.AppDatabase;
import com.example.finaproject.data.MyTaskTable.MyTask;

public class ReportDetailsActivity extends AppCompatActivity {

    //
     private ImageView detailImage;
    // تعطيل مؤقت
    private TextView detailTitle;
    private TextView detailDescription;
    private AppDatabase database;
    private static final String TAG = "ReportDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);

        // --- تم تعطيل كل ما يتعلق بالصورة بشكل مؤقت ---
         detailImage = findViewById(R.id.detail_image);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);

        database = AppDatabase.getdb(this);

        long taskId = getIntent().getLongExtra("TASK_id",-1);
        Log.d(TAG, "تم استلام اسم المهمة: '" + taskId + "'");

        if (taskId == -1 ) {
            Toast.makeText(this, "خطأ: لم يتم استلام اسم البلاغ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            MyTask task = database.getMyTaskQuery().getTaskById(taskId);

            runOnUiThread(() -> {
                if (task != null) {
                    Log.d(TAG, "تم العثور على المهمة في قاعدة البيانات: " + task.getTaskName());
                    detailTitle.setText(task.getTaskName());
                    detailDescription.setText(task.getTaskDescription());

                    // --- تم تعطيل كل ما يتعلق بالصورة بشكل مؤقت ---

                    String imagePath = task.getImageUrl();
                    Log.d(TAG, "مسار الصورة من قاعدة البيانات: " + imagePath);

                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            detailImage.setImageURI(Uri.parse(imagePath));
//                            Glide.with(ReportDetailsActivity.this)
//                                    .load(Uri.parse(imagePath))
//                                   // .error(R.drawable.ic_launcher_background)
//                                    .into(detailImage);
                        } catch (Exception e) {
                            Log.e(TAG, "خطأ في تحميل الصورة (Glide): ", e);
                            detailImage.setVisibility(View.GONE);
                        }
                    } else {
                        detailImage.setVisibility(View.GONE);
                    }


                } else {
                    Log.e(TAG, "خطأ فادح: لم يتم العثور على المهمة بالاسم '" + taskId + "' في قاعدة البيانات.");
                    Toast.makeText(ReportDetailsActivity.this, "لم يتم العثور على البلاغ.", Toast.LENGTH_LONG).show();
                    // finish();
                }
            });
        }).start();
    }
}
