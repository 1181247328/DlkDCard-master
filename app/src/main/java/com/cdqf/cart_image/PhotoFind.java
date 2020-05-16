package com.cdqf.cart_image;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liu on 2017/12/25.
 */

public class PhotoFind {
    public List<String> photoList= new CopyOnWriteArrayList<>();

    public PhotoFind(List<String> photoList) {
        this.photoList = photoList;
    }
}
