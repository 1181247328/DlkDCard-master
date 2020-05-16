package com.cdqf.cart_utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * �������������������ΪString
 * <p>
 * ����ʱ����ӽ�����
 *
 * @author �Ƽ������޺�
 */
public class RequestHandler extends RequestCallBack<String> {
    private String TAG = RequestHandler.class.getSimpleName();
    private Context context;
    private ProgressDialog progressDialog;
    private XProgressDialog xProgressDialog = null;
    private OnResponseHandler responseHandler;
    private JSONValidator jsonValidator;
    private int style = 0;
    private int theme = 1;
    private String toast;

    public RequestHandler(Context context,
                          OnResponseHandler responseHandler) {
        super();
        this.context = context;
        this.responseHandler = responseHandler;
        jsonValidator = new JSONValidator();
    }

    public RequestHandler(Context context, int style, String toast, OnResponseHandler responseHandler) {
        super();
        this.context = context;
        this.style = style;
        this.toast = toast;
        this.responseHandler = responseHandler;
        jsonValidator = new JSONValidator();
    }

    public RequestHandler(Context context, int style, String toast, int theme, OnResponseHandler responseHandler) {
        super();
        this.context = context;
        this.style = style;
        this.toast = toast;
        this.theme = theme;
        this.responseHandler = responseHandler;
        jsonValidator = new JSONValidator();
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (style) {
            case 1:
                openOneDialog();
                break;
            default:
                break;
        }
    }

    private void openOneDialog(){
        xProgressDialog = new XProgressDialog(context,toast,theme);
        xProgressDialog.show();
    }

    private void  closeOneDialog(){
        if(xProgressDialog != null){
            xProgressDialog.cancel();
        }
    }

    @Override
    public void onLoading(long total, long current, boolean isUploading) {
        super.onLoading(total, current, isUploading);
    }

    @Override
    public void onFailure(HttpException arg0, String arg1) {
        Log.e(TAG, "---onFailure---" + arg0.getMessage());
        if (responseHandler != null) {
            responseHandler.onResponse(null, RequestStatus.FAILURE);
        }
        switch (style) {
            case 1:
                closeOneDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        if (responseHandler != null) {
            String result = responseInfo.result;
            Log.e(TAG,"---onSuccess---"+result);
            if (jsonValidator.validate(result)) {
                responseHandler.onResponse(result, RequestStatus.SUCCESS);
            } else {
                responseHandler.onResponse(null, RequestStatus.BAD_JSON);
            }
        }
        switch (style) {
            case 1:
                closeOneDialog();
                break;
            default:
                break;
        }
    }
}
