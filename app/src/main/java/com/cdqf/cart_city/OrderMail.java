package com.cdqf.cart_city;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2017/8/7.
 */

public class OrderMail {
    private String hotelId;

    private List<OrderShopp> orderShoppList = new CopyOnWriteArrayList<OrderShopp>();

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public List<OrderShopp> getOrderShoppList() {
        return orderShoppList;
    }

    public void setOrderShoppList(List<OrderShopp> orderShoppList) {
        this.orderShoppList = orderShoppList;
    }
}
