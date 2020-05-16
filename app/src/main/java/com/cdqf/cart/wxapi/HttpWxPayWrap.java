package com.cdqf.cart.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.cart.R;
import com.cdqf.cart_utils.HttpRequestWrap;
import com.cdqf.cart_utils.OnResponseHandler;
import com.cdqf.cart_utils.RequestHandler;
import com.cdqf.cart_utils.RequestStatus;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信
 * Created by liu on 2017/6/29.
 */

public class HttpWxPayWrap {

    private static String TAG = HttpWxPayWrap.class.getSimpleName();

    private static IWXAPI api = null;

    private static HttpRequestWrap httpRequestWrap = null;

    /**
     * 判断是不是安装了微信
     *
     * @param context 上下文
     * @param appId   申请的APP_ID
     * @return
     */
    public static boolean isWxApp(Context context, String appId) {
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return false;
        }
        return true;
    }

    /**
     * 请求的方式
     *
     * @param context  上下文
     * @param appId    微信开放平台申请的APP_ID
     * @param isPost   请求方式
     * @param orderKey 键
     * @param order    值
     */
    public static void wxPayParamss(final Context context, String appId, String isPost, String orderKey, String order, String address) {
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return;
        }
        Log.e(TAG, "---" + isPost + "---" + orderKey + "---" + order + "---" + address);
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(isPost);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "微信支付中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG, "---微信支付---" + result);
                wxPostJSON(context, result, status);
            }
        }));
        Map<String, Object> paramss = new HashMap<String, Object>();
        paramss.put(orderKey, order);
        httpRequestWrap.send(address, paramss);
    }

    /**
     * 请求参数为两个
     *
     * @param context
     * @param appId
     * @param isPost
     * @param orderKey
     * @param order
     * @param idKey
     * @param id
     */
    public static void wxPayParamss(final Context context, String appId, String isPost, String orderKey, String order, String idKey, String id, String address) {
        api = WXAPIFactory.createWXAPI(context, appId);
        api.registerApp(appId);
        if (!(api != null && api.isWXAppInstalled())) {
            initToast(context, "请安装微信");
            return;
        }
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(isPost);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "微信支付中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG, "---微信支付---" + result);
                wxPostJSON(context, result, status);
            }
        }));
        Map<String, Object> paramss = new HashMap<String, Object>();
        paramss.put(orderKey, order);
        paramss.put(idKey, id);
        httpRequestWrap.send(address, paramss);
    }

    /**
     * 请求参数处理
     *
     * @param context
     * @param result
     * @param status
     */
    private static void wxPostJSON(final Context context, String result, RequestStatus status) {
        if (status == RequestStatus.SUCCESS) {
            if (result != null) {
                JSONObject pay = JSON.parseObject(result);
                String retData = null;
                try {
                    retData = JSON.parseObject(pay.getString("result")).getString("retData");
                    if (retData == null) {
                        initToast(context, "JSON有误!");
                        return;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    initToast(context, "数据返回有误!请检查网络");
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(retData);
                //APP_ID
                String appId = jsonObject.getString("appid");
                String partnerId = jsonObject.getString("partnerid");
                String prepayId = jsonObject.getString("prepayid");
                String packageValue = jsonObject.getString("package");
                String nonceStr = jsonObject.getString("noncestr");
                int timestamp = jsonObject.getInteger("timestamp");
                String sign = jsonObject.getString("sign");
                Log.e(TAG, "---appId---" + appId + "---partnerId---" + partnerId + "---prepayId---" + prepayId + "---nonceStr---" + nonceStr + "---sign---" + sign);
                PayReq wechatPay = new PayReq();
                wechatPay.appId = appId;
                wechatPay.partnerId = partnerId;
                wechatPay.prepayId = prepayId;
                wechatPay.packageValue = packageValue;
                wechatPay.nonceStr = nonceStr;
                wechatPay.timeStamp = timestamp + "";
                wechatPay.sign = sign;
                api.sendReq(wechatPay);
            } else {
                initToast(context, "支付失败,请检查网络");
            }
        } else {
            initToast(context, "支付失败,请检查网络");
        }
    }

    public static void wxPostJSON(String[] wxPay) {
        PayReq wechatPay = new PayReq();
        wechatPay.appId = wxPay[0];
        wechatPay.partnerId = wxPay[3];
        wechatPay.prepayId = wxPay[4];
        wechatPay.packageValue = wxPay[2];
        wechatPay.nonceStr = wxPay[1];
        wechatPay.timeStamp = wxPay[5];
        wechatPay.sign = wxPay[2];
        api.sendReq(wechatPay);
    }

    public static void wxPostJSON(String wxPay) {
        JSONObject jsonObject = JSON.parseObject(wxPay);
        //APP_ID
        String appId = jsonObject.getString("appid");
        String partnerId = jsonObject.getString("partnerid");
        String prepayId = jsonObject.getString("prepayid");
        String packageValue = jsonObject.getString("package");
        String nonceStr = jsonObject.getString("noncestr");
        String timestamp = jsonObject.getString("timestamp");
        String sign = jsonObject.getString("sign");
        Log.e(TAG, "---appId---" + appId + "---partnerId---" + partnerId + "---prepayId---" + prepayId + "---nonceStr---" + nonceStr + "---sign---" + sign);
        PayReq wechatPay = new PayReq();
        wechatPay.appId = appId;
        wechatPay.partnerId = partnerId;
        wechatPay.prepayId = prepayId;
        wechatPay.packageValue = packageValue;
        wechatPay.nonceStr = nonceStr;
        wechatPay.timeStamp = timestamp;
        wechatPay.sign = sign;
        boolean isApi = api.sendReq(wechatPay);
        Log.e(TAG, "---" + isApi);
    }

    private static void initToast(Context context, String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * 分享文字
     *
     * @param shareText
     */
    public static void shareWxFriends(String shareText, int position) {
        WXTextObject obj = new WXTextObject();
        obj.text = shareText;


        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = obj;
        msg.description = shareText;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        switch (position) {
            //微信好友
            case 0:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            //朋友圈
            case 1:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
        }
        api.sendReq(req);
    }

    /**
     * 分享图片(网络图片)
     *
     * @param posiiton
     * @param path
     */
    public static void shareWXImage(Context context, String title, int posiiton, String path) {
        URL url = null;
        Bitmap bitmap = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setConnectTimeout(5000);
        try {
            conn.setRequestMethod("PNG");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            SHareImage(title, posiiton, bitmap);
        } else {
            initToast(context, "图片有误");
        }
    }

    /**
     * 分享图片（当前项目测试用）
     *
     * @param context
     * @param position
     * @param drawable
     */
    public static void shareWXImage(Context context, String title, int position, int drawable) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), drawable);
        SHareImage(title, position, bmp);
    }

    /**
     * 分享图片(本地)
     *
     * @param context
     * @param position
     * @param pathFile
     */
    public static void shareWXImageFile(Context context, String title, int position, String pathFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pathFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        SHareImage(title, position, bitmap);
    }

    /**
     * 网页分享
     *
     * @param context
     * @param webpageUrl  = 你要分享的url地址比如"www.baidu.com"
     * @param title
     * @param description
     */
    public static void shareWXUrl(Context context, String webpageUrl, String title, String description, int position) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = webpageUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launchers);
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        switch (position) {
            //微信好友
            case 0:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            //朋友圈
            case 1:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
        }
        //调用api接口，发送数据到微信
        api.sendReq(req);
    }

    private static void SHareImage(String title, int position, Bitmap bitmap) {
        WXImageObject obj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = obj;
        msg.description = title;
        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
        bitmap.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        switch (position) {
            //微信好友
            case 0:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            //朋友圈
            case 1:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;
        }
        api.sendReq(req);
    }
}
