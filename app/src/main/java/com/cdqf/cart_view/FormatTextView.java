package com.cdqf.cart_view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by XinAiXiaoWen on 2017/4/22.
 */

public class FormatTextView extends TextView{

    private String format = null;

    public FormatTextView(Context context) {
        super(context);
    }

    public FormatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FormatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        char[] f = format.toCharArray();
        String end = "";
        int m = 0;
        for(int i = 0; i < f.length;i++){
            end +=f[i];
            m++;
            if(m == 4){
                end+= " ";
                m = 0;
            }
        }
        setText(end);
    }

    public void setFormatText(String format){
        this.format = format;
        invalidate();
    }
}
