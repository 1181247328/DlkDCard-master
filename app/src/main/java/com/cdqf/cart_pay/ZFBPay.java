package com.cdqf.cart_pay;

/**
 * Created by liu on 2017/7/19.
 */

public class ZFBPay {

    //支付私钥
    private String sign = null;

    //签约方式
    private String sign_type = null;

    //支付代码
    private String code = null;

    //支付的app_id
    private String app_id = null;

    //支付自己订单号
    private String out_trade_no = null;

    //支付内部订单号
    private String trade_no = null;

    //支付金额
    private String total_amount = null;

    //支付卖家id
    private String seller_id = null;

    //支付时间
    private String timestamp = null;

    public ZFBPay() {

    }

    public ZFBPay(String sign, String sign_type,String code, String app_id, String out_trade_no, String trade_no, String total_amount, String seller_id, String timestamp) {
        this.sign = sign;
        this.sign_type = sign_type;
        this.code = code;
        this.app_id = app_id;
        this.out_trade_no = out_trade_no;
        this.trade_no = trade_no;
        this.total_amount = total_amount;
        this.seller_id = seller_id;
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
