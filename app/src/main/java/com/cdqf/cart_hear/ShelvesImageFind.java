package com.cdqf.cart_hear;

import android.graphics.Bitmap;

/**
 * Created by XinAiXiaoWen on 2017/3/28.
 */

public class ShelvesImageFind {
    public Bitmap bitmap;

    public int type;

    public ShelvesImageFind(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ShelvesImageFind(Bitmap bitmap, int type) {
        this.bitmap = bitmap;
        this.type = type;
    }
}
