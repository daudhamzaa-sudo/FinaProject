package com.example.finaproject.data;

/**
 * واجهة (Interface) ResponseCallback:
 * تستخدم كـ "عقد" (Contract) لضمان أن الكود الذي يطلب خدمة الذكاء الاصطناعي 
 * سيوفر مكاناً لاستلام النتيجة أو معالجة الخطأ.
 */
public interface ResponseCallback {
    
    /**
     * دالة onResponse: يتم استدعاؤها عندما يعيد الذكاء الاصطناعي جواباً بنجاح.
     * @param response النص المولد من قبل Gemini.
     */
    void onResponse(String response);

    /**
     * دالة onError: يتم استدعاؤها في حال حدوث خطأ (مثل انقطاع الإنترنت).
     * @param error كائن يحتوي على تفاصيل الخطأ التقني.
     */
    void onError(Throwable error);
}
