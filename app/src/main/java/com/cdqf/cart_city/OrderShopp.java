package com.cdqf.cart_city;

/**
 * Created by liu on 2017/8/7.
 */

public class OrderShopp {
    private int HotelCbId;

    private int Quantity;

    private String url;

    private String name;

    private int price;

    public int getCid() {
        return HotelCbId;
    }

    public void setCid(int cid) {
        this.HotelCbId = cid;
    }

    public int getNum() {
        return Quantity;
    }

    public void setNum(int num) {
        this.Quantity = num;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
