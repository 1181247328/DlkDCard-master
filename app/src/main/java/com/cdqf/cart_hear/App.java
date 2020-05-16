package com.cdqf.cart_hear;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mob.MobApplication;
import com.wanjian.cockroach.Cockroach;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zxy.tiny.Tiny;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by gaolf on 15/12/24.
 */
public class App extends MobApplication {

    private String TAG = App.class.getSimpleName();
    private static App sInstance = new App();

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Tiny.getInstance().init(this);
//        CauchExceptionHandler.getInstance().setDefaultUnCachExceptionHandler();
//        LogToFile.init(getApplicationContext());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();

        OkHttpUtils.initClient(okHttpClient);

        Cockroach.install(new Cockroach.ExceptionHandler() {
            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e(TAG, "--->CockroachException:" + thread + "<---", throwable);
//                            Toast.makeText(App.this, "Exception Happend\n" + thread + "\n" + throwable.toString(), Toast.LENGTH_SHORT).show();
                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
    }

    public static App getInstance() {
        return sInstance;
    }
}
