package com.cdqf.cart.wxapi;

import android.content.Context;


import com.cdqf.cart_state.CartState;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by liu on 2017/7/19.
 */

public class WxLogin {

    private final static String TOAST = "用户未安装微信";

    //微信登录
    private static IWXAPI WXapi = null;

    private static CartState direState = CartState.getCartState();

    /**
     * 微信登录
     */
    public static void loginWx(Context context) {
        WXapi = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, true);
        WXapi.registerApp(Constants.WX_APP_ID);
        if (WXapi != null && WXapi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo";
            WXapi.sendReq(req);
        } else {
            direState.initToast(context, TOAST, true, 0);
        }
    }
}
