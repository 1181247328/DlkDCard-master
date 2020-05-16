package com.cdqf.cart_pay;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.cdqf.cart_utils.HttpRequestWrap;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 支付宝
 * Created by liu on 2017/7/19.
 */

public class HttpZFBPayWrap {

    private static HttpRequestWrap httpRequestWrap = null;

    private static String TAG = HttpZFBPayWrap.class.getSimpleName();

    private static ZFBPay zfbPay = null;

    private static EventBus eventBus = EventBus.getDefault();

    //上下文
    private static Context context = null;

    //返回参数提示语句
    private static String[] resultToast = new String[]{
            "支付成功",
            "订单正在处理",
            "订单支付失败",
            "重复请求",
            "你已取消订单",
            "网络连接出错",
            "支付结果未知",
            "未知错误"
    };

    /**
     * 初始化
     *
     * @param zfbContext
     */
    private static void init(Context zfbContext) {
        context = zfbContext;
        httpRequestWrap = new HttpRequestWrap(context);
    }

    /**
     * 请求调起支付宝
     *
     * @param context
     * @param app_id
     * @param date
     * @param monery
     * @param title
     * @param OutTradeNo
     */
    public static void zfbPayParamss(final Context context, String app_id, String date, String monery, String title, String OutTradeNo) {
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(app_id, date, monery, title, OutTradeNo, true);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, ZfbConstants.RSA_PRIVATE, true);
        final String orderInfo = orderParam + "&" + sign;
        Log.e(TAG,"----orderInfo----"+orderInfo+"--sign---"+sign);
        zfbPayParamss(context,orderInfo);
    }

    /**
     * 发送返回状态
     *
     * @param state 状态
     */
    private static void postToast(int state) {
        eventBus.post(new ZFBPayFind(resultToast[state]));
    }

    /**
     * 解析支付宝
     *
     * @param resultInfo
     */
    private static void payJSON(String resultInfo) {
        //支付外部内容
        JSONObject result = JSON.parseObject(resultInfo);
        //支付私钥
        String sign = result.getString("sign");
        //签约方式
        String sign_type = result.getString("sign_type");
        //支付详细情总
        JSONObject response = result.getJSONObject("alipay_trade_app_pay_response");
        //支付代码
        String code = response.getString("code");
        //支付的app_id
        String app_id = response.getString("app_id");
        //支付自己订单号
        String out_trade_no = response.getString("out_trade_no");
        //支付内部订单号
        String trade_no = response.getString("trade_no");
        //支付金额
        String total_amount = response.getString("total_amount");
        //支付卖家id
        String seller_id = response.getString("seller_id");
        //支付时间
        String timestamp = response.getString("timestamp");
        zfbPay = new ZFBPay(sign, sign_type, code, app_id, out_trade_no, trade_no, total_amount, seller_id, timestamp);
        eventBus.post(new ZFBFind(zfbPay));
    }

    public static void zfbPayParamss(final Context context,final String orderInfo) {
        Log.e(TAG, "---orderInfo---" + orderInfo);
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);
                // 调用支付接口，获取支付结果
                Map<String, String> result = alipay.payV2(orderInfo, true);
                PayResult payResult = new PayResult(result);
                String resultInfo = payResult.getResult();
                String resultStatus = payResult.getResultStatus();
                Log.e(TAG,"---"+resultInfo+"---resultStatus---"+resultStatus);
                if (TextUtils.equals(resultStatus, "9000")) {
                    postToast(0);
                    payJSON(resultInfo);
                } else if (TextUtils.equals(resultStatus, "8000")) {
                    postToast(1);
                } else if (TextUtils.equals(resultStatus, "4000")) {
                    postToast(2);
                } else if (TextUtils.equals(resultStatus, "5000")) {
                    postToast(3);
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    postToast(4);
                    eventBus.post(new CancelFind());
                } else if (TextUtils.equals(resultStatus, "6002")) {
                    postToast(5);
                } else if (TextUtils.equals(resultStatus, "6004")) {
                    postToast(6);
                } else {
                    postToast(7);
                }
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
}
