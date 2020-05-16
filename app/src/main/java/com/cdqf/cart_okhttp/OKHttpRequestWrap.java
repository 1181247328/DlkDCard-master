package com.cdqf.cart_okhttp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cdqf.cart_state.WIFIGpRs;
import com.cdqf.cart_utils.JSONValidator;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.io.File;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;

public class OKHttpRequestWrap {

    private static final String TAG = OKHttpRequestWrap.class.getSimpleName();

    private Context context = null;

    private JSONValidator jsonValidator = null;

    private OnHttpRequest http = null;

    private Gson gson = new Gson();

    //异常提示
    private String[] code = {
            "请检查网络",
            "数据请求失败,请重新请求",
            "JSON格式不正确"
    };

    public OKHttpRequestWrap(Context context) {
        this.context = context;
        jsonValidator = new JSONValidator();
    }

    /**
     * post请求（表单形式）
     *
     * @param url
     * @param params
     */
    public void post(String url, boolean isDialog, String them, Map<String, Object> params, OnHttpRequest onHttpRequest) {
        http = onHttpRequest;
        PostFormBuilder postFormBuilder = new PostFormBuilder();
        postFormBuilder.url(url);
        Set<String> keys = params.keySet();
        for (String key : keys) {
            Object value = params.get(key);
            if (value.getClass() == String.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Integer.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Boolean.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Double.class) {
                postFormBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == File.class) {
                Log.e(TAG,"---图片---"+String.valueOf(value));
                postFormBuilder.addFile(key, key + ".jpg", new File(String.valueOf(value)));
            } else {
                //TODO
            }
        }
        postFormBuilder.build().execute(new OKHttpStringCallback(context, isDialog, them, new OnOkHttpResponseHandler() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---JSON---" + response);
                if (!WIFIGpRs.isNetworkConnected(context)) {
                    Log.e(TAG, "---无网---");
                    Toast.makeText(context, code[0], Toast.LENGTH_SHORT).show();
                    http.onOkHttpCode(code[0], 0);
                    return;
                }
                if (response == null) {
                    Toast.makeText(context, code[1], Toast.LENGTH_SHORT).show();
                    http.onOkHttpCode(code[1], 1);
                    return;
                }
                if (!jsonValidator.validate(response)) {
                    Toast.makeText(context, code[2], Toast.LENGTH_SHORT).show();
                    http.onOkHttpCode(code[2], 2);
                    return;
                }
                if (http != null) {
                    http.onOkHttpResponse(response, id);
                }
            }

            /**
             * 失败
             * @param error
             */
            @Override
            public void onOkHttpError(String error) {
                if (http != null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    http.onOkHttpError(error);
                }
            }
        }));
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     */
    public void get(String url, boolean isDialog, String them, Map<String, Object> params, OnHttpRequest onHttpRequest) {
        http = onHttpRequest;
        GetBuilder getBuilder = new GetBuilder();
        getBuilder.url(url);
        Set<String> keys = params.keySet();
        for (String key : keys) {
            Object value = params.get(key);
            if (value.getClass() == String.class) {
                getBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Integer.class) {
                getBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Boolean.class) {
                getBuilder.addParams(key, String.valueOf(value));
            } else if (value.getClass() == Double.class) {
                getBuilder.addParams(key, String.valueOf(value));
            } else {
                //TODO
            }
        }
        getBuilder.build().execute(new OKHttpStringCallback(context, isDialog, them, new OnOkHttpResponseHandler() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---JSON---" + response);
                if (!WIFIGpRs.isNetworkConnected(context)) {
                    Log.e(TAG, "---无网---");
                    Toast.makeText(context, code[0], Toast.LENGTH_SHORT).show();
                    http.onOkHttpCode(code[0], id);
                    return;
                }
                if (response == null) {
                    Toast.makeText(context, code[1], Toast.LENGTH_SHORT).show();
                    http.onOkHttpCode(code[1], id);
                    return;
                }
                if (!jsonValidator.validate(response)) {
                    Toast.makeText(context, code[2], Toast.LENGTH_SHORT).show();
                    http.onOkHttpCode(code[2], id);
                    return;
                }
                if (http != null) {
                    http.onOkHttpResponse(response, id);
                }
            }

            /**
             * 失败
             * @param error
             */
            @Override
            public void onOkHttpError(String error) {
                if (http != null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    http.onOkHttpError(error);
                }
            }
        }));
    }

    /**
     * 发送JSON形式
     *
     * @param url
     * @param isDialog
     * @param them
     * @param params
     */
    public void postString(String url, boolean isDialog, String them, Map<String, Object> params, OnHttpRequest onHttpRequest) {
        http = onHttpRequest;
        String logn = gson.toJson(params);
        Log.e(TAG, "---http---" + url + "---postString---json---" + logn);
        OkHttpUtils
                .postString()
                .url(url)
                .content(logn)
                .mediaType(MediaType.parse("application/json;charset=utf-8"))
                .build()
                .execute(new OKHttpStringCallback(context, isDialog, them, new OnOkHttpResponseHandler() {
                    @Override
                    public void onOkHttpResponse(String response, int id) {
                        Log.e(TAG, "---JSON---" + response);
                        if (!WIFIGpRs.isNetworkConnected(context)) {
                            Log.e(TAG, "---无网---");
                            Toast.makeText(context, code[0], Toast.LENGTH_SHORT).show();
                            http.onOkHttpCode(code[0], id);
                            return;
                        }
                        if (response == null) {
                            Toast.makeText(context, code[1], Toast.LENGTH_SHORT).show();
                            http.onOkHttpCode(code[1], id);
                            return;
                        }
                        if (!jsonValidator.validate(response)) {
                            Toast.makeText(context, code[2], Toast.LENGTH_SHORT).show();
                            http.onOkHttpCode(code[2], id);
                            return;
                        }
                        if (http != null) {
                            http.onOkHttpResponse(response, id);
                        }
                    }

                    /**
                     * 失败
                     * @param error
                     */
                    @Override
                    public void onOkHttpError(String error) {
                        if (http != null) {
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            http.onOkHttpError(error);
                        }
                    }
                }));
    }
}
