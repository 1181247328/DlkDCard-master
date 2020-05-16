package com.cdqf.cart_utils;

import android.content.Context;
import android.widget.Toast;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * 上传图片
 * Created by XinAiXiaoWen on 2017/3/27.
 */

public class RequestImageHandler extends RequestCallBack<String> {
    private Context context;
    private XProgressDialog dialog;
    private OnResponseHandler responseHandler;
    private JSONValidator jsonValidator;
    private String toast;

    public RequestImageHandler(Context context,String toast,
                               OnResponseHandler responseHandler) {
        super();
        this.context = context;
        this.responseHandler = responseHandler;
        this.toast = toast;
        jsonValidator = new JSONValidator();
    }

    private void openDilog(){
        dialog = new XProgressDialog(context,toast);
        dialog.show();
    }

    private void closeDilog(String toast){
      if(dialog != null){
          dialog.setMessage(toast);
          dialog.cancel();
      }
    }

    private void closeDilog(){
        if(dialog != null){
            dialog.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        openDilog();
    }

    @Override
    public void onLoading(long total, long current, boolean isUploading) {

    }

    /**
     * 上传成功
     * @param responseInfo
     */
    @Override
    public void onSuccess(ResponseInfo<String> responseInfo) {
        if(responseHandler != null){
            String result = responseInfo.result;
            if(jsonValidator.validate(result)){
                responseHandler.onResponse(result, RequestStatus.SUCCESS);
            }else{
                responseHandler.onResponse(null, RequestStatus.BAD_JSON);
            }
        }
        closeDilog("设置成功");
    }

    /**
     * 上传图片失败的处理
     * @param e
     * @param s
     */
    @Override
    public void onFailure(HttpException e, String s) {
        if(responseHandler != null){
            responseHandler.onResponse(null,RequestStatus.FAILURE);
        }
        closeDilog();
        Toast.makeText(context,"设置头像失败",Toast.LENGTH_SHORT).show();
    }
}
