package com.jingchen.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

public class PullableLineGridView extends LineGridView implements Pullable
{

    public PullableLineGridView(Context context)
    {
        super(context);
    }

    public PullableLineGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableLineGridView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown()
    {
        if (getCount() == 0)
        {
            // 没有item的时候也可以下拉刷新
            return true;
        } else // 滑到顶部了
            return getFirstVisiblePosition() == 0
                    && getChildAt(0).getTop() >= 0;
    }

    @Override
    public boolean canPullUp()
    {
        if (getCount() == 0)
        {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1))
        {
            // 滑到底部了
            return getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight();
        }
        return false;
    }

}
