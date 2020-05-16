package com.cdqf.cart_txmap;

public class LoadsFind {
    public String load;

    public int i;

    public double longitude;

    public double latitude;

    public LoadsFind(String load, int i) {
        this.load = load;
        this.i = i;
    }

    public LoadsFind(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
