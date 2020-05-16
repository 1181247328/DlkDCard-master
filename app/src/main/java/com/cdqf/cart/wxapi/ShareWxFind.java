package com.cdqf.cart.wxapi;

public class ShareWxFind {
    //标题
    public String shareTitle = null;

    //摘要
    public String shareSummary = null;

    //图片
    public String shareURL = null;

    public ShareWxFind(String shareTitle, String shareSummary, String shareURL) {
        this.shareTitle = shareTitle;
        this.shareSummary = shareSummary;
        this.shareURL = shareURL;
    }
}
