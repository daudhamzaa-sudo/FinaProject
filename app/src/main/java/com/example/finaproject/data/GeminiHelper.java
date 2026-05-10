package com.example.finaproject.data;

// استيراد المكتبات اللازمة للتعامل مع الذكاء الاصطناعي ولغة Kotlin من Java
import androidx.annotation.NonNull; // للتأكد من أن المعاملات ليست فارغة (null)
import com.google.ai.client.generativeai.GenerativeModel; // الكلاس الرئيسي لنموذج الذكاء الاصطناعي
import com.google.ai.client.generativeai.type.GenerateContentResponse; // لاستلام رد الموديل
import kotlin.ResultKt; // أداة للتعامل مع نتائج لغة Kotlin داخل الجافا
import kotlin.coroutines.Continuation; // للتعامل مع العمليات التي تعمل في الخلفية (Coroutines)
import kotlin.coroutines.CoroutineContext; // لتحديد بيئة عمل العملية الخلفية
import kotlin.coroutines.EmptyCoroutineContext; // بيئة عمل فارغة للعمليات البسيطة

/**
 * كلاس GeminiHelper:
 * صُمم لتسهيل إرسال الأسئلة للذكاء الاصطناعي واستقبال الإجابات دون تعقيد الكود في الشاشات.
 */
public class GeminiHelper {
    // تحديد إصدار الموديل المستخدم (فلاش هو الأسرع والأفضل للتطبيقات)
    public static final String GEMINI_Version = "gemini-1.5-flash";
    
    // مفتاح الـ API الخاص بك للوصول لخدمات جوجل (سرّي للغاية)
    private static final String GEMINI_API_KEY = "AIzaSyCJ_7dTIZSo0NY0D-2SF5YgCXgp4Dn6Kxs";
    
    // متغير ثابت لحفظ نسخة واحدة من الكلاس (Singleton Pattern) لتوفير الذاكرة
    private static GeminiHelper instance;
    
    // محرك الذكاء الاصطناعي الفعلي
    private final GenerativeModel gemini;

    /**
     * الباني (Constructor): خاص (private) لمنع إنشاء نسخ عشوائية من الكلاس.
     */
    private GeminiHelper() {
        // تهيئة محرك Gemini باستخدام الإصدار والمفتاح المحددين
        gemini = new GenerativeModel(GEMINI_Version, GEMINI_API_KEY);
    }

    /**
     * دالة getInstance: تعيد النسخة الوحيدة من GeminiHelper لكل التطبيق.
     */
    public static synchronized GeminiHelper getInstance() {
        if (instance == null) {
            instance = new GeminiHelper();
        }
        return instance;
    }

    /**
     * دالة sendMessage: ترسل نصاً للذكاء الاصطناعي وتنتظر الرد.
     * @param prompt السؤال أو المشكلة التي كتبها المستخدم.
     * @param callback الواجهة التي ستستقبل الرد (نجاح أو فشل).
     */
    public void sendMessage(String prompt, ResponseCallback callback) {
        // إرسال الطلب للموديل بشكل "خلفي" (Background)
        gemini.generateContent(prompt, new Continuation<GenerateContentResponse>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                // استخدام بيئة عمل افتراضية
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object result) {
                try {
                    // فحص النتيجة: إذا كانت فاشلة، سيقوم هذا السطر برمي استثناء (Exception)
                    ResultKt.throwOnFailure(result);

                    // إذا وصلنا هنا، فالمهمة نجحت: نحول النتيجة لكائن رد
                    GenerateContentResponse response = (GenerateContentResponse) result;
                    
                    // إرسال النص الناتج للواجهة الرئيسية عبر الـ callback
                    if (callback != null) {
                        callback.onResponse(response.getText());
                    }
                } catch (Throwable e) {
                    // في حال حدوث أي خطأ (انقطاع إنترنت مثلاً)، نبلغ الواجهة بالخطأ
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
