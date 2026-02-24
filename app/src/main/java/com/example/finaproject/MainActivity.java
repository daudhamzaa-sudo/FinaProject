package com.example.finaproject;

// --- استيراد المكتبات الأساسية من Android ---import android.annotation.SuppressLint;      // لتجاهل تحذيرات معينة من المحلل، مثل التحذير من ID غير موجود.
import android.annotation.SuppressLint;
import android.content.Intent;              // لإدارة عمليات الانتقال بين شاشات (Activities) التطبيق.
import android.os.Bundle;                  // لتمرير البيانات عند إنشاء الشاشات أو استعادتها.
import android.util.Log;                    // لتسجيل رسائل في نافذة Logcat، مفيدة جدًا لتصحيح الأخطاء.
import android.view.View;                  // الفئة الأساسية لجميع عناصر واجهة المستخدم (Buttons, TextViews, etc.).
import android.widget.Button;              // عنصر زر قابل للنقر.
import android.widget.EditText;             // حقل إدخال نص عادي. (مستورد هنا لواجهة Gemini).
import android.widget.ImageView;            // لعرض الصور.
import android.widget.ProgressBar;         // شريط تقدم دائري أو خطي، يستخدم لإظهار أن هناك عملية جارية.
import android.widget.TextView;             // لعرض نصوص غير قابلة للتعديل.
import android.widget.Toast;               // لعرض رسائل قصيرة ومؤقتة للمستخدم (مثل رسائل الخطأ أو النجاح).

// --- استيراد مكتبات AndroidX لدعم الميزات الحديثة والتوافق ---
import androidx.activity.EdgeToEdge;       // لتفعيل وضع العرض من الحافة إلى الحافة، مما يجعل التطبيق يستخدم كامل الشاشة.
import androidx.annotation.NonNull;          // للإشارة إلى أن قيمة معلمة أو دالة لا يمكن أن تكون null، مما يساعد في تجنب أخطاء NullPointerException.
import androidx.appcompat.app.AppCompatActivity; // الفئة الأساسية للأنشطة التي تستخدم ميزات حديثة مثل شريط الأدوات (Toolbar).
import androidx.core.graphics.Insets;        // للتعامل مع هوامش النظام (System Insets) التي تسببها أشرطة الحالة والتنقل.
import androidx.core.view.ViewCompat;         // فئة مساعدة لتوفير وظائف متوافقة مع إصدارات Android المختلفة.
import androidx.core.view.WindowInsetsCompat;  // للتعامل مع هوامش النوافذ بشكل متوافق.
import androidx.recyclerview.widget.LinearLayoutManager; // لترتيب عناصر RecyclerView في قائمة خطية (عمودية أو أفقية).
import androidx.recyclerview.widget.RecyclerView;           // عنصر واجهة مستخدم فعال ومتقدم لعرض القوائم الكبيرة والديناميكية.

// --- استيرادات خاصة بمكونات التطبيق الداخلية (قاعدة البيانات، Gemini, etc.) ---
import com.example.finaproject.data.AppDatabase;          // الفئة الرئيسية لقاعدة بيانات Room المحلية.
import com.example.finaproject.data.GeminiHelper;         // فئة مساعدة تم إنشاؤها للتفاعل مع Gemini API.
import com.example.finaproject.data.MyTaskTable.MyTask;       // كائن البيانات (Model) الذي يمثل "مهمة" أو "تقرير" واحد.
import com.example.finaproject.data.MyTaskTable.MyTaskAdapter; // الـ Adapter المسؤول عن ربط بيانات التقارير بـ RecyclerView.
import com.example.finaproject.data.ResponseCallback;     // واجهة (Interface) مخصصة للتعامل مع الردود القادمة من Gemini.

// --- استيرادات مكتبة Material Design و Firebase ---
import com.google.android.material.textfield.TextInputEditText; // حقل إدخال نص متقدم من مكتبة Material يوفر ميزات إضافية.
import com.google.android.material.textfield.TextInputLayout;   // حاوية لـ TextInputEditText تضيف ميزات مثل العنوان العائم وعرض الأخطاء.
import com.google.firebase.database.DataSnapshot;           // يمثل "لقطة" من البيانات من Firebase Realtime Database في لحظة معينة.
import com.google.firebase.database.DatabaseError;            // يمثل خطأ حدث أثناء محاولة قراءة البيانات من Firebase.
import com.google.firebase.database.DatabaseReference;        // يمثل مرجعًا لموقع معين في قاعدة بيانات Firebase.
import com.google.firebase.database.FirebaseDatabase;         // الفئة الرئيسية للتفاعل مع Firebase Realtime Database.
import com.google.firebase.database.ValueEventListener;       // مستمع يتم تشغيله عند قراءة البيانات أو عند حدوث أي تغيير عليها.

// --- استيرادات خاصة بالعمليات المتزامنة (Threading) ---
import java.util.ArrayList;   // هيكل بيانات يستخدم كقائمة ديناميكية (يمكن إضافة وحذف العناصر منها).
import java.util.List;        // الواجهة (Interface) العامة للقوائم في Java.
import java.util.concurrent.ExecutorService; // لإدارة وتنفيذ المهام في خيوط (threads) منفصلة عن الخيط الرئيسي.
import java.util.concurrent.Executors;       // فئة مساعدة لإنشاء أنواع مختلفة من ExecutorService.

/**
 * الشاشة الرئيسية للتطبيق (MainActivity).
 * هذه هي الشاشة الأولى التي يراها المستخدم بعد تسجيل الدخول.
 * مسؤولة عن عرض قائمة التقارير من Firebase، وتوفير واجهة للتفاعل مع Gemini، والسماح بالانتقال إلى شاشات أخرى.
 */
public class MainActivity extends AppCompatActivity {

    // --- تعريف متغيرات واجهة عرض التقارير (الكود الأصلي) ---
    /** متغير لعنصر TextView الذي يعرض العنوان الرئيسي للشاشة. */
    private TextView tvTitle;
    /** متغير لعنصر TextView الذي سيعرض الرد القادم من Gemini. */
    private TextView responseText;
    /** متغير لعنصر ImageView الذي يعرض صورة (هنا تستخدم كأيقونة للإعدادات). */
    private ImageView imgPreview;
    /** متغير لعنصر TextView الذي يعرض عنوانًا فرعيًا. */
    private TextView tvSubtitle;
    /** متغير لحاوية حقل البحث، للتحكم في مظهره وحالته. */
    private TextInputLayout inputSearchLayout;
    /** متغير لحقل إدخال نص البحث الفعلي. */
    private TextInputEditText inputSearch;
    /** متغير لزر "إضافة تقرير"، الذي ينقل المستخدم لشاشة إنشاء تقرير جديد. */
    private Button btnAddReport;
    /** متغير لعنصر RecyclerView، وهو القائمة التي ستعرض جميع التقارير. */
    private RecyclerView recyclerReports;
    /** متغير للـ Adapter، وهو الجسر بين بيانات التقارير (ArrayList) وطريقة عرضها (RecyclerView). */
    private MyTaskAdapter myTaskAdapter;

    // --- تعريف متغيرات واجهة Gemini (الكود الجديد المضاف) ---
    /** حقل إدخال النص لإرسال سؤال إلى Gemini. */
    private EditText inputText;
    /** زر لإرسال السؤال إلى Gemini. */
    private Button sendButton;
    /** شريط تقدم يظهر للمستخدم أثناء انتظار الرد من Gemini. */
    private ProgressBar progressBar;

    /**
     * دالة `onCreate` هي نقطة الانطلاق لهذه الشاشة. يتم استدعاؤها مرة واحدة فقط عند إنشاء الشاشة.
     * تستخدم لتهيئة الواجهة، ربط المتغيرات، وإعداد المستمعين.
     * @param savedInstanceState (حالة النشاط المحفوظة) - إذا تم تدمير الشاشة وإعادة إنشائها، يمكن استعادة حالتها من هنا.
     */
    @SuppressLint("MissingInflatedId") // هذا التعليق يخبر المحلل بتجاهل أي تحذير محتمل حول عدم العثور على IDs أثناء التحليل.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // استدعاء الدالة الأم لاستكمال عملية إنشاء الشاشة بشكل صحيح.
        super.onCreate(savedInstanceState);
        // تفعيل وضع العرض من الحافة إلى الحافة لجعل التطبيق يملأ الشاشة بالكامل.
        EdgeToEdge.enable(this);
        // ربط هذا الكلاس (MainActivity.java) بملف التصميم الخاص به (activity_main.xml).
        setContentView(R.layout.activity_main);

        // --- استدعاء دوال التهيئة بالترتيب ---
        // 1. تهيئة كل ما يتعلق بواجهة عرض التقارير.
        setupReportsUI();
        // 2. تهيئة كل ما يتعلق بواجهة التفاعل مع Gemini.
        setupGeminiUI();
        // 3. بدء عملية جلب البيانات من Firebase فورًا بعد تهيئة الواجهات.
        getAllFromFirebase();

        // --- إعداد مستمع لمعالجة هوامش النظام ---
        // هذا الكود يضمن أن محتوى التطبيق لن يتداخل مع أشرطة النظام (شريط الحالة في الأعلى وشريط التنقل في الأسفل).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // الحصول على أبعاد هوامش النظام.
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // تطبيق هذه الهوامش كـ "padding" للعنصر الرئيسي في الواجهة (v).
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // إرجاع الـ insets للسماح للنظام بمواصلة معالجتها.
            return insets;
        });
    }

    /**
     * دالة لتنظيم وتهيئة كل ما يتعلق بواجهة عرض التقارير.
     * الهدف منها هو تجميع كل الكود الخاص بتهيئة هذا الجزء في مكان واحد لسهولة القراءة والصيانة.
     */
    private void setupReportsUI() {
        // --- ربط المتغيرات البرمجية بعناصر الواجهة في ملف XML باستخدام الـ ID الخاص بكل عنصر ---
        recyclerReports = findViewById(R.id.recyclerReports);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        inputSearchLayout = findViewById(R.id.inputSearchLayout);
        imgPreview = findViewById(R.id.imgPreview);
        btnAddReport = findViewById(R.id.btnAddReport);
        responseText = findViewById(R.id.responseText); // هذا العنصر مشترك بين الواجهتين

        // --- إعداد مستمع النقر على أيقونة الإعدادات ---
        // عند النقر على الصورة (imgPreview)...
        imgPreview.setOnClickListener(view -> {
            // ... يتم إنشاء Intent للانتقال من هذه الشاشة (MainActivity.this) إلى شاشة الإعدادات (Settings.class).
            Intent intent = new Intent(MainActivity.this, Settings.class);
            // بدء عملية الانتقال.
            startActivity(intent);
        });

        // --- إعداد RecyclerView (قائمة التقارير) ---
        // 1. تحديد مدير التنسيق: `LinearLayoutManager` لترتيب العناصر في قائمة عمودية.
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));
        // 2. إنشاء Adapter جديد: نعطيه السياق (this) وقائمة فارغة (`new ArrayList<>()`) في البداية، سيتم تعبئتها لاحقًا.
        myTaskAdapter = new MyTaskAdapter(this, new ArrayList<>());
        // 3. ربط الـ RecyclerView بالـ Adapter الذي أنشأناه. الآن الـ RecyclerView يعرف من أين سيحصل على بياناته وكيف سيعرضها.
        recyclerReports.setAdapter(myTaskAdapter);

        // --- إعداد مستمع للنقرات على عناصر القائمة ---
        recyclerReports.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerReports, new RecyclerItemClickListener.OnItemClickListener() {
            /**
             * هذه الدالة يتم استدعاؤها عند حدوث نقرة قصيرة على أي عنصر في القائمة.
             * @param view العنصر الرسومي الذي تم النقر عليه داخل الصف.
             * @param position موقع (index) الصف الذي تم النقر عليه في القائمة.
             */
            @Override
            public void onItemClick(View view, int position) {
                // الحصول على القائمة الحالية من الـ Adapter.
                ArrayList<MyTask> currentTasks = myTaskAdapter.getTasksList();
                // التحقق من أن القائمة ليست فارغة وأن الموقع المنقور صالح (لتجنب أخطاء).
                if (currentTasks != null && position >= 0 && position < currentTasks.size()) {
                    // الحصول على كائن "المهمة" المقابل للصف الذي تم النقر عليه.
                    MyTask clickedTask = currentTasks.get(position);

                    // إنشاء Intent للانتقال إلى شاشة تفاصيل التقرير (ReportDetailsActivity).
                    Intent intent = new Intent(MainActivity.this, ReportDetailsActivity.class);

                    // --- إرفاق بيانات التقرير مع الـ Intent كبيانات إضافية (Extras) ---
                    // يتم إرسال كل معلومة مع "مفتاح" (key) فريد لاستقبالها في الشاشة التالية.
                    intent.putExtra("TASK_id", clickedTask.getId());
                    intent.putExtra("TITLE", clickedTask.getTaskTitle());
                    intent.putExtra("DESCRIPTION", clickedTask.getTaskDescription());
                    intent.putExtra("STATUS", clickedTask.getTaskStatus());
                    intent.putExtra("IMAGE_URL", clickedTask.getImageUrl());
                    intent.putExtra("LAT", clickedTask.getLatitude());
                    intent.putExtra("LON", clickedTask.getLongitude());

                    // بدء الانتقال إلى شاشة التفاصيل مع البيانات المرفقة.
                    startActivity(intent);
                }
            }

            /**
             * هذه الدالة يتم استدعاؤها عند حدوث نقرة طويلة على أي عنصر.
             * حاليًا لا تقوم بأي شيء، ولكن يمكن إضافة وظائف هنا في المستقبل (مثل الحذف السريع).
             */
            @Override
            public void onLongItemClick(View view, int position) {
                // يمكنك إضافة كود هنا إذا أردت تفعيل شيء عند الضغط المطول.
            }
        }));

        // --- إعداد مستمع النقر لزر "إضافة تقرير جديد" ---
        btnAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // عند النقر، يتم إنشاء Intent للانتقال إلى شاشة إنشاء تقرير جديد (NewReporScreen).
                Intent intent = new Intent(MainActivity.this, NewReporScreen.class);
                // بدء الانتقال.
                startActivity(intent);
            }
        });
    }

    /**
     * دالة لجلب جميع البيانات من Firebase Realtime Database.
     * تستخدم "مستمع قيمة" (`addValueEventListener`) الذي يعمل مرة واحدة عند الإضافة، ثم يعمل مجددًا كلما تغيرت البيانات.
     */
    private void getAllFromFirebase() {
        // 1. الحصول على نسخة من كائن قاعدة بيانات Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // 2. الحصول على مرجع (Reference) للعقدة (Node) التي نريد قراءة البيانات منها، وهي عقدة "tasks".
        DatabaseReference myRef = database.getReference("tasks");

        // 3. إضافة مستمع دائم لهذا المرجع.
        myRef.addValueEventListener(new ValueEventListener() {
            /**
             * هذه الدالة هي قلب عملية القراءة. يتم استدعاؤها تلقائيًا عند وصول البيانات.
             * @param snapshot (لقطة البيانات) - كائن يحتوي على نسخة من جميع البيانات الموجودة تحت عقدة "tasks".
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // إنشاء قائمة جديدة ومؤقتة لتخزين المهام التي سيتم جلبها.
                ArrayList<MyTask> tasks = new ArrayList<>();
                // المرور على كل "ابن" (child) موجود داخل الـ snapshot. كل ابن يمثل تقريرًا واحدًا.
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    // محاولة تحويل بيانات الابن (التي تكون بصيغة JSON) إلى كائن Java من نوع `MyTask`.
                    MyTask task = postSnapshot.getValue(MyTask.class);
                    // التأكد من أن عملية التحويل نجحت وأن الكائن ليس null.
                    if (task != null) {
                        // إضافة المهمة التي تم تحويلها بنجاح إلى القائمة المؤقتة.
                        tasks.add(task);
                    }
                }

                // --- تحديث واجهة المستخدم بالبيانات الجديدة ---
                // التأكد من أن الـ adapter ليس null (تم تهيئته).
                if (myTaskAdapter != null) {
                    // تمرير القائمة الجديدة المليئة بالبيانات إلى الـ Adapter.
                    myTaskAdapter.setTasksList(tasks);
                    // إعلام الـ Adapter بأن مجموعة بياناته قد تغيرت، ليقوم بإعادة رسم القائمة على الشاشة.
                    myTaskAdapter.notifyDataSetChanged();
                }
            }

            /**
             * هذه الدالة يتم استدعاؤها في حال فشل عملية قراءة البيانات (مثل عدم وجود إذن للوصول).
             * @param error كائن يحتوي على تفاصيل الخطأ الذي حدث.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // عرض رسالة خطأ للمستخدم لإعلامه بفشل عملية جلب البيانات.
                Toast.makeText(MainActivity.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * دالة لتهيئة كل ما يتعلق بواجهة التفاعل مع Gemini.
     */
    private void setupGeminiUI() {
        // --- ربط المتغيرات البرمجية بعناصر واجهة Gemini ---
        inputText = findViewById(R.id.inputText);
        sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);
        progressBar = findViewById(R.id.progressBar);

        // التحقق من أن زر الإرسال ليس null قبل إضافة مستمع له.
        if (sendButton != null) {
            // إعداد مستمع النقر لزر الإرسال.
            sendButton.setOnClickListener(v -> {
                // التحقق من أن حقل الإدخال ليس null.
                if (inputText != null) {
                    // الحصول على النص من حقل الإدخال.
                    String query = inputText.getText().toString();
                    // التحقق من أن النص ليس فارغًا.
                    if (!query.isEmpty()) {
                        // إذا كان هناك نص، يتم استدعاء دالة إرسال الطلب إلى Gemini.
                        callGemini(query);
                    } else {
                        // إذا كان الحقل فارغًا، يتم عرض رسالة للمستخدم.
                        Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * دالة لإرسال الطلب إلى Gemini API ومعالجة الرد.
     * @param query السؤال النصي المراد إرساله إلى Gemini.
     */
    private void callGemini(String query) {
        // جعل شريط التقدم مرئيًا لإعلام المستخدم بأن هناك عملية جارية.
        progressBar.setVisibility(View.VISIBLE);
        // مسح أي نص قديم من حقل عرض الرد.
        responseText.setText("");
        // مسح حقل الإدخال بعد إرسال السؤال.
        inputText.setText("");

        // بناء نص الطلب (prompt) باستخدام فئة مساعدة (هذه الخطوة قد تكون مخصصة لتنسيق السؤال).
        String prompt = PromptBuilder.buildReportPrompt(query);

        // إرسال الطلب إلى Gemini بشكل غير متزامن (في خيط منفصل).
        GeminiHelper.getInstance().sendMessage(prompt, new ResponseCallback() {
            /**
             * يتم استدعاؤها (callback) عند وصول الرد بنجاح من Gemini.
             * @param response النص المستلم من Gemini.
             */
            @Override
            public void onResponse(String response) {
                // يجب تحديث واجهة المستخدم من خلال الخيط الرئيسي (UI Thread).
                runOnUiThread(() -> {
                    // عرض النص المستلم في حقل عرض الرد.
                    responseText.setText(response);
                    // إخفاء شريط التقدم بعد اكتمال العملية.
                    progressBar.setVisibility(View.GONE);
                });
            }

            /**
             * يتم استدعاؤها (callback) في حال حدوث خطأ أثناء الطلب.
             * @param throwable الخطأ الذي حدث.
             */
            @Override
            public void onError(Throwable throwable) {
                // تحديث الواجهة من الخيط الرئيسي.
                runOnUiThread(() -> {
                    // إخفاء شريط التقدم.
                    progressBar.setVisibility(View.GONE);
                    // عرض رسالة خطأ للمستخدم.
                    Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * دالة لجلب المهام من قاعدة البيانات المحلية (Room). هذه الدالة لم تعد مستخدمة حاليًا.
     * تعمل في خيط منفصل (background thread) لتجنب تجميد واجهة المستخدم أثناء القراءة من القرص.
     */
    private void loadTasks() {
        // إنشاء وإدارة خيط واحد لتنفيذ المهمة.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // تنفيذ الكود التالي في الخيط المنفصل.
        executor.execute(() -> {
            // جلب كل المهام من جدول `MyTask` باستخدام استعلام `getAllTasks`.
            List<MyTask> myTasksList = AppDatabase.getdb(getApplicationContext()).getMyTaskQuery().getAllTasks();
            // تحويل القائمة من `List` إلى `ArrayList`.
            ArrayList<MyTask> myTasks = new ArrayList<>(myTasksList);
            // العودة إلى الخيط الرئيسي لتحديث واجهة المستخدم.
            runOnUiThread(() -> {
                // التأكد من أن الـ adapter موجود.
                if (myTaskAdapter != null) {
                    // تحديث بيانات الـ adapter.
                    myTaskAdapter.setTasksList(myTasks);
                    // إعلام الـ adapter بالتغيير.
                    myTaskAdapter.notifyDataSetChanged();
                }
            });
        });
    }

    /**
     * دالة دورة الحياة `onResume`. يتم استدعاؤها في كل مرة تصبح فيها الشاشة مرئية للمستخدم.
     * (مثلاً، عند بدء التشغيل لأول مرة، أو عند العودة إليها من شاشة أخرى).
     */
    @Override
    protected void onResume() {
        // استدعاء الدالة الأم.
        super.onResume();
        // loadTasks(); // تم تعطيل هذا السطر لأننا الآن نعتمد على `addValueEventListener` من Firebase
        // الذي يقوم بتحديث البيانات تلقائيًا عند بدء التشغيل وعند كل تغيير،
        // مما يجعل استدعاء `loadTasks` غير ضروري وقد يسبب تعارضًا.
    }
}
