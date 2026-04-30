package com.example.finaproject.data;

import androidx.annotation.NonNull;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * فئة GeminiHelper:
 * تم تحديثها لاستخدام الطريقة الرسمية والأكثر أماناً (ResultKt) للتعامل مع نتائج Kotlin في Java.
 * هذا يمنع الانهيار (Crash) عند محاولة الوصول لنتائج الذكاء الاصطناعي.
 */
public class GeminiHelper {
    public static final String GEMINI_Version = "gemini-1.5-flash";
    private static final String GEMINI_API_KEY = "AIzaSyCJ_7dTIZSo0NY0D-2SF5YgCXgp4Dn6Kxs";
    private static GeminiHelper instance;
    private final GenerativeModel gemini;

    private GeminiHelper() {
        gemini = new GenerativeModel(GEMINI_Version, GEMINI_API_KEY);
    }

    public static synchronized GeminiHelper getInstance() {
        if (instance == null) {
            instance = new GeminiHelper();
        }
        return instance;
    }

    public void sendMessage(String prompt, ResponseCallback callback) {
        gemini.generateContent(prompt, new Continuation<GenerateContentResponse>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object result) {
                try {
                    // هذه هي الطريقة السحرية في Java للتعامل مع نتائج Kotlin:
                    // إذا كان هناك فشل، ستقوم هذه الدالة برمي استثناء (Throw Exception)
                    // وإذا كان نجاحاً، ستكمل الكود بشكل طبيعي.
                    ResultKt.throwOnFailure(result);

                    // إذا وصلنا هنا، فهذا يعني أن العملية نجحت
                    GenerateContentResponse response = (GenerateContentResponse) result;
                    if (callback != null) {
                        callback.onResponse(response.getText());
                    }
                } catch (Throwable e) {
                    // إذا حدث فشل، سنقوم بتمريره للـ callback
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
