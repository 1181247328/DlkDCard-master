package com.cdqf.cart_okhttp;

public interface OnHttpRequest {

    void onOkHttpResponse(String response, int id);

    void onOkHttpError(String error);

    void onOkHttpCode(String code, int state);
}
