package com.cdqf.cart_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.cart_okhttp.OKHttpRequestWrap;
import com.cdqf.cart_okhttp.OnHttpRequest;
import com.cdqf.cart_state.CartAddaress;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 判断是否有必须要更新
 */
public class DownloadDilogService extends Service {

    private String TAG = DownloadDilogService.class.getSimpleName();

    private EventBus eventBus = EventBus.getDefault();

    private Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "---创建---");
        initPull(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        Intent intentService = new Intent("servicereceiver");
        sendBroadcast(intentService);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 用于判断是否要自动更新
     *
     * @param isToast
     */
    private void initPull(boolean isToast) {
        Map<String, Object> params = new HashMap<String, Object>();
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.get(CartAddaress.DOWNLOAD, isToast, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---onOkHttpResponse---下载---" + response);
                JSONObject resultJSON = JSON.parseObject(response);
                int error_code = resultJSON.getInteger("code");
                String msg = resultJSON.getString("message");
                switch (error_code) {
                    //获取成功
                    case 204:
                    case 201:
                    case 200:
                        eventBus.post(new DownloadUpdateFind());
                        break;
                    default:
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOkHttpCode(String code, int state) {

            }
        });
    }
}
