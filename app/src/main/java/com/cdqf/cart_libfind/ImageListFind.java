package com.cdqf.cart_libfind;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImageListFind {
    //多张图片
    private List<String> imageList = new CopyOnWriteArrayList<>();

    public ImageListFind(List<String> imageList) {
        this.imageList = imageList;
    }
}
