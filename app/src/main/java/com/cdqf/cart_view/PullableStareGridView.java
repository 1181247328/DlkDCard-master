package com.cdqf.cart_view;

import android.content.Context;
import android.util.AttributeSet;

import com.android.staggered.StaggeredGridView;
import com.jingchen.pulltorefresh.Pullable;

/**
 * Created by liu on 2017/11/16.
 */

public class PullableStareGridView extends StaggeredGridView implements Pullable {
    public PullableStareGridView(Context context) {
        super(context);
    }

    public PullableStareGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableStareGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {

        return false;
    }

    @Override
    public boolean canPullUp() {
        return false;
    }
}
