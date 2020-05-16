package com.cdqf.cart_okhttp;

import android.content.Context;
import android.util.Log;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;

public class OKHttpStringCallback extends StringCallback {

    private static String TAG = OKHttpStringCallback.class.getSimpleName();

    private XProgressDialog xProgressDialog = null;

    private Context context = null;

    private OnOkHttpResponseHandler onOkHttpResponseHandler = null;

    private boolean isDialog = true;

    private String them = "";

    public OKHttpStringCallback(Context context, boolean isDialog, String them, OnOkHttpResponseHandler onOkHttpResponseHandler) {
        this.context = context;
        this.isDialog = isDialog;
        this.them = them;
        this.onOkHttpResponseHandler = onOkHttpResponseHandler;
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
        Log.e(TAG, "---最后---");
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
        Log.e(TAG, "---开始---");
        if (isDialog) {
            xProgressDialog = new XProgressDialog(context, them, 1);
            xProgressDialog.show();
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        Log.e(TAG, "---错误---" + e.getMessage());
        if (xProgressDialog != null) {
            xProgressDialog.dismiss();
        }
        if (onOkHttpResponseHandler != null) {
            onOkHttpResponseHandler.onOkHttpError(e.getMessage());
        }
    }

    @Override
    public void onResponse(String response, int id) {
        Log.e(TAG, "---结果---" + response);
        if (xProgressDialog != null) {
            xProgressDialog.dismiss();
        }

        if (onOkHttpResponseHandler != null) {
            onOkHttpResponseHandler.onOkHttpResponse(response, id);
        }
    }
}
