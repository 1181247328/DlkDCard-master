package com.cdqf.cart.wxapi;

/**
 * Created by liu on 2017/6/23.
 */

public class Constants {

    /**
     * 微信
     */
    //订单请求参数222
    public static final String WX_URL_STRING = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //appid 微信分配的公众账号ID
    public static final String WX_APP_ID = "wx04b11fc5b93db89d";

    //商户号 微信分配的公众账号ID
    public static final String WX_MCH_ID = "";

    //登录所用的secret
    public static final String WX_APP_SECRET = "a8271e2ef1cc88ffdf0e283837b87560";

    //API密钥，在商户平台设置
    public static final  String WX_API_KEY= "";

    // 获取第一步的code后，请求以下链接获取access_token
    public static String WX_GET_CODE_REQUEST = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    // 获取用户个人信息
    public static String WX_GET_USER_INFO= "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

    /**
     * QQ
     */
    public static  String QQ_SCOPE = "all";

    public static String QQ_APP_ID="101570424";
}
