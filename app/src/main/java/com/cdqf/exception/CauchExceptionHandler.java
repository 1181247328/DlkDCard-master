package com.cdqf.exception;

import android.util.Log;

/**
 * 异常捕获
 */
public class CauchExceptionHandler implements Thread.UncaughtExceptionHandler {

    private String TAG = CauchExceptionHandler.class.getSimpleName();

    private static CauchExceptionHandler cauchExceptionHandler = null;

    private CauchExceptionHandler() {

    }

    public static CauchExceptionHandler getInstance() {

        if (cauchExceptionHandler == null) {

            synchronized (CauchExceptionHandler.class) {

                if (cauchExceptionHandler == null) {
                    cauchExceptionHandler = new CauchExceptionHandler();
                }
            }
        }

        return cauchExceptionHandler;
    }

    public void setDefaultUnCachExceptionHandler() { //在Application开始时调用

        Thread.setDefaultUncaughtExceptionHandler(this); //设置应用默认的全局捕获异常器
    }

    @Override //应用没有捕抓的异常会到这里来,如果我们设置了应用的默认全局捕抓异常为CauchExceptionHandler的话
    public void uncaughtException(Thread thread, Throwable throwable) {
        android.os.Process.killProcess(android.os.Process.myPid());
        Log.e(TAG, throwable.getMessage()); //异常信息
        String exception = throwable.getMessage();
        LogToFile.e(TAG, exception);
    }
}