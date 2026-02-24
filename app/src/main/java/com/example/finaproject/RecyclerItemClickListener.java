package com.example.finaproject;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * كلاس مساعد (Helper Class) وظيفته التقاط أحداث اللمس (Clicks) واللمس المطول (Long Clicks)
 * على العناصر الموجودة داخل الـ RecyclerView، حيث أن الـ RecyclerView لا يوفر ميزة
 * setOnItemClickListener بشكل افتراضي مثل القوائم القديمة.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    // متغير لحفظ الـ Listener الذي سيتم تنفيذه عند حدوث نقرة (يتم تمريره من الـ Activity)
    private OnItemClickListener mListener;

    // كائن لكشف الإيماءات (مثل النقرة السريعة أو الضغطة المطولة) وفهم نوع حركة الإصبع
    private GestureDetector mGestureDetector;

    /**
     * واجهة (Interface) تحدد العمليات التي يجب تنفيذها عند الضغط.
     * يجب على الـ Activity أو Fragment تنفيذ هذه الميثودات.
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);      // للنقرة العادية
        void onLongItemClick(View view, int position);  // للنقرة المطولة
    }

    /**
     * "الباني" (Constructor): يقوم بإعداد كاشف الإيماءات وربط المستمع.
     * @param context سياق التطبيق
     * @param recyclerView القائمة المستهدفة
     * @param listener الكود الذي سيتم تنفيذه عند الضغط
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        // إعداد الـ GestureDetector للتعرف على نوع اللمسة
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            // يتم استدعاؤها عندما يرفع المستخدم إصبعه بعد نقرة سريعة
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true; // تعني أننا التقطنا النقرة بنجاح
            }

            // يتم استدعاؤها عند استمرار المستخدم بالضغط لفترة طويلة
            @Override
            public void onLongPress(MotionEvent e) {
                // البحث عن "الابن" (العنصر) الموجود تحت إحداثيات الضغطة (X, Y)
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());

                // إذا وجدنا عنصراً وكان هناك مستمع (Listener) مفعل، نقوم باستدعاء ميثود الضغط المطول
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    /**
     * ميثود اعتراض اللمس: هذه أهم ميثود، حيث تقرر هل يجب "سرقة" اللمسة من القائمة ومعالجتها هنا أم لا.
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {
        // تحديد أي عنصر في القائمة تم لمسه بناءً على مكان الإصبع
        View childView = view.findChildViewUnder(e.getX(), e.getY());

        // إذا كان هناك عنصر تحت الإصبع، وتم التأكد من أنها "نقرة" عبر mGestureDetector
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {

            // جلب رقم ترتيب (Position) العنصر داخل الـ Adapter
            int position = view.getChildAdapterPosition(childView);

            // التأكد من أن الترتيب صالح (ليس خارج النطاق)
            if (position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(childView, position); // تنفيذ كود النقرة العادية
            }
            return true; // إخبار النظام بأننا عالجنا اللمسة ولا داعي لمزيد من البحث
        }
        return false; // تجاهل اللمسة إذا لم تكن نقرة صالحة
    }

    /**
     * يتم استدعاؤها إذا تقرر معالجة اللمسة (لكننا نعتمد على onInterceptTouchEvent لذا تترك فارغة).
     */
    @Override
    public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent motionEvent) {
    }

    /**
     * ميثود للتحكم في منع اعتراض اللمس من قبل العناصر الأب (عادة لا نغيرها).
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}