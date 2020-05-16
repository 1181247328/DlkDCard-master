package com.cdqf.cart_image;

import android.graphics.Bitmap;

/**
 * Created by XinAiXiaoWen on 2017/3/30.
 */

public class Photo {

    //照片名
    private String photoName;

    //照片路径
    private String photoData;

    private Bitmap photoBitmap;

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }
}
