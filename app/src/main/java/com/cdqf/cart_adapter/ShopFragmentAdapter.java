package com.cdqf.cart_adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 我的订单适配器
 * Created by liu on 2017/11/20.
 */

public class ShopFragmentAdapter extends FragmentPagerAdapter {

    private Fragment[] myOrderList = null;

    private FragmentManager fm = null;

    public ShopFragmentAdapter(FragmentManager fm, Fragment[] myOrderList) {
        super(fm);
        this.fm = fm;
        this.myOrderList = myOrderList;
    }

    @Override
    public Fragment getItem(int position) {
        return myOrderList[position];
    }

    @Override
    public int getCount() {
        return myOrderList.length;
    }

}