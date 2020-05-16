package com.cdqf.cart_txmap;

/**
 * Created by liu on 2018/7/4.
 */

public class LoadFind {
    public String load;

    public int i;

    public double longitude;

    public double latitude;

    public LoadFind(String load, int i) {
        this.load = load;
        this.i = i;
    }

    public LoadFind(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
