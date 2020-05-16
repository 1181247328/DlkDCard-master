package com.cdqf.cart.wxapi;

/**
 * Created by XinAiXiaoWen on 2017/4/28.
 */

public class WXFind {
    public String openid;

    public String nickname;

    public String headimgurl;

    public WXFind(String openid, String nickname, String headimgurl) {
        this.openid = openid;
        this.nickname = nickname;
        this.headimgurl = headimgurl;
    }
}
