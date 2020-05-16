package com.cdqf.cart_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.cdqf.cart.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;


/**
 * 下载版本
 * Created by liu on 2017/6/1.
 */

public class DownloadService extends Service {

    private String TAG = DownloadService.class.getSimpleName();
    private String mDownloadUrl;//APK的下载路径
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private String fileAPk;
    private HttpUtils htpHttpUtils;

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "---创建---");
        mNotificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        createChannelIfNeeded();
        htpHttpUtils = new HttpUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "---onStartCommand---");
        if (intent == null) {
            notifyMsg("温馨提醒", "文件下载失败", 0);
            stopSelf();
        }
        mDownloadUrl = intent.getStringExtra("apkUrl");//获取下载APK的链接

        Log.e(TAG, "---" + mDownloadUrl);
        fileAPk = intent.getStringExtra("file");
        downloadFile(mDownloadUrl);//下载APK
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyMsg(String title, String content, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);//为了向下兼容，这里采用了v7包下的NotificationCompat来构造
        builder.setSmallIcon(R.mipmap.ic_launchers)
                .setLargeIcon(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.ic_launchers))
                .setContentTitle(title);
        if (progress > 0 && progress < 100) {
            //下载进行中
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(content);
        if (progress >= 100) {
            //下载完成
            builder.setContentIntent(getInstallIntent());
        }
        mNotification = builder.build();
        mNotificationManager.notify(0, mNotification);
    }

    /**
     * 8.0以上需要增加channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("12345", "脱狗车宝员工", NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 安装apk文件
     *
     * @return
     */
    private PendingIntent getInstallIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(this, "com.cdqf.dire.fileprovider_cart", new File(fileAPk));
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(fileAPk)), "application/vnd.android.package-archive");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void downloadFile(String url) {
        htpHttpUtils.download(url, fileAPk, new RequestCallBack<File>() {

            /**
             * 下载成功调用的方法
             * @param responseInfo
             */
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                notifyMsg("温馨提醒", "文件下载成功", 100);
            }

            /**
             * 下载失败的调用方法
             * @param e
             * @param s
             */
            @Override
            public void onFailure(HttpException e, String s) {
                notifyMsg("温馨提醒", "文件下载失败", 0);
                stopSelf();
            }

            /**
             * 正在下载调的方法
             * @param total
             * @param current
             * @param isUploading
             */
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                Log.e(TAG, "---" + (current * 100 / total) + "%");
                notifyMsg("温馨提醒", "文件正在下载..", (int) (current * 100 / total));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
    }
}
