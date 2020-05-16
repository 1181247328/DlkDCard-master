package com.cdqf.cart_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdqf.cart.R;
import com.cdqf.cart_adapter.ShopFragmentAdapter;
import com.cdqf.cart_state.CartState;
import com.cdqf.cart_view.ViewPageSwipeRefreshLayout;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhengsr.viewpagerlib.indicator.TabIndicator;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 报表
 */
public class ReoprtFragment extends Fragment {

    private String TAG = ReoprtFragment.class.getSimpleName();

    private CartState cartState = CartState.getCartState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    private View view = null;

    private int position = 0;

    private Fragment[] orderList = new Fragment[]{

    };

    private List<String> orderName = Arrays.asList("日报", "周报", "月报");
    //刷新
    @BindView(R.id.srl_report_pull)
    public ViewPageSwipeRefreshLayout srlReportPull = null;

    @BindView(R.id.ti_report_dicatior)
    public TabIndicator tiReportDicatior = null;

    @BindView(R.id.vp_report_screen)
    public ViewPager vpReportScreen = null;

    private ShopFragmentAdapter shopFragmentAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "---创建---");

        view = inflater.inflate(R.layout.fragment_report, null);

        initAgo();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        ButterKnife.bind(this, view);
    }

    private void initListener() {
        tiReportDicatior.setViewPagerSwitchSpeed(vpReportScreen, 600);
        tiReportDicatior.setTabData(vpReportScreen, orderName, new TabIndicator.TabClickListener() {
            @Override
            public void onClick(int i) {
                position = i;
                vpReportScreen.setCurrentItem(i);
            }
        });
        vpReportScreen.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                position = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initAdapter() {
        shopFragmentAdapter = new ShopFragmentAdapter(getChildFragmentManager(), orderList);
        vpReportScreen.setAdapter(shopFragmentAdapter);
        vpReportScreen.setOffscreenPageLimit(1);
    }

    private void initBack() {

    }

    private void forIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    @OnClick({})
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
    }
}
