package com.cdqf.cart_state;

public class CartAddaress {

    //线上
    public static final String ADDRESS = "https://stay.deelock.net:4433";

    //线上-2
    public static final String ADDRESS_TWO = "http://app.deelock.net:9888";

    //线上-3
    public static final String ADDRESS_THREE = "http://47.96.25.85:7884";

    //登陆
    public static final String LOGIN = ADDRESS + "/robot/home/login";

    //轮播图图片
    public static final String BANNER = ADDRESS + "/robot/home/banner";

    //人证对比
    public static final String CARDIMAGE = ADDRESS_TWO + "/hardserver/system/kuangshiCompare.json";

    //硬件交互
    public static final String READ =ADDRESS+"/robot/home/upInfo";

    //下载(用于内部更新)
    public static String DOWNLOAD = ADDRESS + "/staff/versions";


}
