package com.example.finaproject;

// استيراد المكتبات اللازمة للتعامل مع حركات الإصبع (Gestures) واللمس
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * كلاس RecyclerItemClickListener: 
 * وظيفته إضافة ميزة "الضغط على العناصر" لقائمة RecyclerView، لأنها لا تأتي مدمجة بشكل افتراضي.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    // واجهة (Interface) لتعريف الدوال التي سننفذها في الشاشة الرئيسية (MainActivity)
    private OnItemClickListener mListener;

    // كائن كشف الإيماءات (مثل النقرة السريعة أو المطولة)
    private GestureDetector mGestureDetector;

    /**
     * تعريف الواجهة التي تحدد نوع الضغطات المدعومة
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);      // للنقرة الواحدة
        void onLongItemClick(View view, int position);  // للنقرة المطولة (Long Press)
    }

    /**
     * الباني (Constructor): يجهز كاشف اللمس ويربطه بالقائمة
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        // إعداد كاشف الإيماءات لفهم طبيعة لمسة المستخدم
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            // يتم استدعاؤها عند رفع الإصبع بعد نقرة ناجحة
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            // يتم استدعاؤها عند الضغط المستمر لفترة طويلة
            @Override
            public void onLongPress(MotionEvent e) {
                // تحديد أي عنصر مرئي موجود تحت مكان الضغطة
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                // إذا وجدنا عنصراً، نقوم بتنفيذ دالة الضغط المطول
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    /**
     * دالة اعتراض اللمس: تقرر هل يجب معالجة اللمسة هنا أم تركها للقائمة
     */
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {
        // البحث عن العنصر الموجود تحت إصبع المستخدم
        View childView = view.findChildViewUnder(e.getX(), e.getY());

        // إذا وجدنا عنصراً وكان هناك "نقرة" تم اكتشافها
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            // جلب ترتيب العنصر في القائمة وتنفيذ دالة النقرة العادية
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true; // إخبار النظام بأننا عالجنا اللمسة
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent motionEvent) {
        // تترك فارغة لأننا نعالج اللمس في الدالة السابقة
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // تترك فارغة (تستخدم لمنع تداخل اللمس مع العناصر الأب)
    }
}
