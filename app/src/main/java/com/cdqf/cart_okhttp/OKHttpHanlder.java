package com.cdqf.cart_okhttp;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.cart_state.CartState;
import com.cdqf.dire_3des.Constants;
import com.cdqf.dire_3des.DESUtils;


public class OKHttpHanlder {
    private static String TAG = OKHttpHanlder.class.getSimpleName();

    private static CartState plantState = CartState.getCartState();

    public static String isOKHttpResult(Context context, String result) {
        if (result != null) {
            JSONObject resultJSON = JSON.parseObject(result);
            //状态
            boolean isStatus = resultJSON.getBoolean("Status");
            //提示
            String message = resultJSON.getString("Message");
            if (isStatus) {
                String dataJSON = null;
                int statusCode = resultJSON.getInteger("StatusCode");
                if (!code(context, statusCode)) {
                    if (statusCode == 1001) {
                        return 1001 + "";
                    }
                    return null;
                }
                try {
                    dataJSON = DESUtils.decodeDES(resultJSON.getString("Data"), Constants.secretKey.substring(0, 8));
                    Log.e(TAG, "---解密成功---" + dataJSON);
                } catch (Exception e) {
                    Log.e(TAG, "---解密失败---" + dataJSON);
                    e.printStackTrace();
                }
                if (dataJSON == null) {
                    plantState.initToast(context, "解密失败", true, 0);
                    return null;
                }
                return dataJSON;
            } else {
                Log.e(TAG, "---isResultError---" + message);
//                    plantState.initToast(context, message, true, 0);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 状态码提示
     *
     * @param code
     */
    private static boolean code(Context context, int code) {
        switch (code) {
            //执行中出错
            case 0001:
                plantState.initToast(context, "验证码出错", true, 0);
                return false;
            //执行成功
            case 1000:
                Log.e(TAG, "---执行成功----");
                return true;
            //列表为空
            case 1001:
                Log.e(TAG, "---列表为空----");
                return false;
            default:
                return true;
        }
    }
}
