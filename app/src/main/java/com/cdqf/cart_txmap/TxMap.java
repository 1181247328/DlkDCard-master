package com.cdqf.cart_txmap;

/**
 * Created by liu on 2018/7/18.
 */

public class TxMap {
    private static TxMap txMap = new TxMap();

    public static TxMap getTxMap() {
        return txMap;
    }

    //省
    private String province;

    //市
    private String city;

    //详情地址
    private String address;

    //经度
    private double longitude;

    //纬度
    private double latitude;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static void setTxMap(TxMap txMap) {
        TxMap.txMap = txMap;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
