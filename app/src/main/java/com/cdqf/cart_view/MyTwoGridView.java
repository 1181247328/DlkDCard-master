package com.cdqf.cart_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by liu on 2017/12/6.
 */

public class MyTwoGridView extends GridView {

    public boolean hasScrollBar = true;

    /**
     * @param context
     */
    public MyTwoGridView(Context context) {
        this(context, null);
    }

    public MyTwoGridView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyTwoGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = heightMeasureSpec;
        if (hasScrollBar) {
            expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}

