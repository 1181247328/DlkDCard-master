package com.cdqf.cart_ble;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BleFind {

    public List<String> bleList = new CopyOnWriteArrayList<>();

    public BleFind(List<String> bleList) {
        this.bleList = bleList;
    }
}
