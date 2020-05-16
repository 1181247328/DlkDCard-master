package com.cdqf.cart_image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.cdqf.cart.R;
import com.cdqf.cart_state.CartState;
import com.cdqf.cart_state.StatusBarCompat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 浏览大图
 */
public class PagerActivity extends Activity {

    private String TAG = PagerActivity.class.getSimpleName();

    private Context context = null;

    private List<String> pageList = new ArrayList<>();

    private int position = -1;

    private CartState cartState = CartState.getCartState();

    private LinkedList<PinchImageView> viewCache = null;

    private DisplayImageOptions originOptions = null;

    private ViewPager pager = null;

    private PageListAdapter pageListAdapter = null;

    private RelativeLayout rlPageReturn = null;

    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_pager);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.black));
        }
        initAgo();
        initView();
        initAdapter();
        initListener();
        initBack();
    }

    private void initAgo() {
        Intent intent = getIntent();
        pageList = (List<String>) intent.getSerializableExtra("pageList");
        position = intent.getIntExtra("position", 0);
        type = intent.getIntExtra("type", 0);
        viewCache = new LinkedList<PinchImageView>();
        originOptions = new DisplayImageOptions.Builder().build();
    }

    private void initView() {
        rlPageReturn = this.findViewById(R.id.rl_page_return);
        pager = findViewById(R.id.pager);
    }

    private void initAdapter() {
        pageListAdapter = new PageListAdapter();
        pager.setAdapter(pageListAdapter);
    }

    private void initListener() {
        rlPageReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initBack() {
        pager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "---恢复---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
    }

    class PageListAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PinchImageView piv;
            if (viewCache.size() > 0) {
                piv = viewCache.remove();
                piv.reset();
            } else {
                piv = new PinchImageView(PagerActivity.this);
            }
//                ImageSource image = Global.getTestImage(position);
            String image = "";
            if (type == 1) {
                image = "file://" + pageList.get(position);
            } else if (type == 2) {
                image = pageList.get(position);
            }
            Global.getImageLoader(getApplicationContext()).displayImage(image, piv, cartState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
            container.addView(piv);
            return piv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            PinchImageView piv = (PinchImageView) object;
            container.removeView(piv);
            viewCache.add(piv);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            PinchImageView piv = (PinchImageView) object;
//                ImageSource image = Global.getTestImage(position);
            String image = "";
            if (type == 1) {
                image = "file://" + pageList.get(position);
            } else if (type == 2) {
                image = pageList.get(position);
            }
            Global.getImageLoader(getApplicationContext()).displayImage(image, piv, originOptions);
        }
    }
}