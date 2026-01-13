package com.example.finaproject.data;

public interface ResponseCallback {
    /**
     * معالجة جواب الطلب من Gemini
     * @param response جواب الطلب
     */
    public void onResponse(String response);


    /**
     * الرد بحالة وجود خطا
     * @param error
     */
    public void onError(Throwable error);
}

