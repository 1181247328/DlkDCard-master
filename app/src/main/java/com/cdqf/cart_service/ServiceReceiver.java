package com.cdqf.cart_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by admin on 2016/4/6.
 */
public class ServiceReceiver extends BroadcastReceiver {

    private String TAG = ServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String start = intent.getAction();
        if (TextUtils.equals(start, "servicereceiver")) {
            context.startService(new Intent(context, DownloadDilogService.class));
            Log.e(TAG, "---重启DownloadDilogService");
        }
    }
}
