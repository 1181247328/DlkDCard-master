package com.cdqf.cart_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.cart.R;
import com.cdqf.cart_class.Shop;
import com.cdqf.cart_dilog.WhyDilogFragment;
import com.cdqf.cart_find.ExitFind;
import com.cdqf.cart_find.LoginFind;
import com.cdqf.cart_okhttp.OKHttpRequestWrap;
import com.cdqf.cart_okhttp.OnHttpRequest;
import com.cdqf.cart_state.BaseActivity;
import com.cdqf.cart_state.CartAddaress;
import com.cdqf.cart_state.CartState;
import com.cdqf.cart_state.StaturBar;
import com.gcssloop.widget.RCRelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.listeners.OnSingleWheelListener;
import cn.addapp.pickers.picker.SinglePicker;
import cn.addapp.pickers.util.ConvertUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * 登陆
 */
public class LoginActivity extends BaseActivity {

    //当前打印名称
    private String TAG = LoginActivity.class.getSimpleName();

    //上下文
    private Context context = null;

    //中间逻辑层
    private CartState cartState = CartState.getCartState();

    //图片显示对象(用的第三方)
    private ImageLoader imageLoader = ImageLoader.getInstance();

    //事件通用总线(用的第三方)
    private EventBus eventBus = EventBus.getDefault();

    //json实例
    private Gson gson = new Gson();

    /*****组件注册*****/

    //账号
    @BindView(R.id.xet_login_account)
    public XEditText xetLoginAccount = null;

    //密码
    @BindView(R.id.xet_login_word)
    public XEditText xetLoginWord = null;

    //登陆
    @BindView(R.id.rcrl_login_enter)
    public RCRelativeLayout rcrlLoginEnter = null;

    //账号
    private String user;

    //密码
    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //加载布局
        setContentView(R.layout.activity_login);

        //沉侵式菜单栏颜色
        StaturBar.setStatusBar(this, R.color.white);

        //改变沉侵式状态栏文字颜色
        cartState.changeStatusBarTextImgColor(this, true);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();
    }

    /**
     * 初始化使用
     */
    private void initAgo() {
        //上下文
        context = this;
        //注册ButterKnife
        ButterKnife.bind(this);
        //初始化ImageLoader
        imageLoader = cartState.getImageLoader(context);
        //注册eventBus
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        cartState.closeKeyboard(this);
    }

    /**
     * 组件初始化使用
     */
    private void initView() {
    }

    /**
     * 适配器通用
     */
    private void initAdapter() {

    }

    /**
     * 监听事件使用
     */
    private void initListener() {

    }

    /**
     * 事件逻辑层使用
     */
    private void initBack() {

    }

    /**
     * 门店信息
     */
    private void initShop(List<Shop> shopList) {
        //实例化数组，用于保存门店内容
        String[] shop = new String[shopList.size()];
        //获取门店名称
        for (int i = 0; i < shopList.size(); i++) {
            shop[i] = shopList.get(i).getStore_name();
        }
        SinglePicker<String> picker = new SinglePicker<>(LoginActivity.this, shop);
        LineConfig config = new LineConfig();
        config.setColor(ContextCompat.getColor(LoginActivity.this, R.color.addstore_one));//线颜色
        config.setThick(ConvertUtils.toPx(LoginActivity.this, 1));//线粗
        config.setItemHeight(20);
        picker.setLineConfig(config);
        picker.setCanLoop(false);//不禁用循环
        picker.setLineVisible(true);
        picker.setTopLineColor(Color.TRANSPARENT);
        picker.setTextSize(18);
        picker.setTitleText("选择门店");
        picker.setTitleTextSize(18);
        picker.setSelectedIndex(0);
        picker.setWheelModeEnable(true);
        picker.setWeightEnable(true);
        picker.setWeightWidth(1);
        picker.setCancelTextColor(ContextCompat.getColor(LoginActivity.this, R.color.house_eight));//顶部取消按钮文字颜色
        picker.setCancelTextSize(18);
        picker.setSubmitTextColor(ContextCompat.getColor(LoginActivity.this, R.color.house_eight));//顶部确定按钮文字颜色
        picker.setSubmitTextSize(18);
        picker.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.white));//背景色
        picker.setSelectedTextColor(ContextCompat.getColor(LoginActivity.this, R.color.house_eight));//前四位值是透明度
        picker.setUnSelectedTextColor(ContextCompat.getColor(LoginActivity.this, R.color.addstore_one));
        picker.setOnSingleWheelListener(new OnSingleWheelListener() {
            @Override
            public void onWheeled(int index, String item) {

            }
        });
        picker.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                //获取当前门店的id和名称,并保存
                String store = cartState.getShopList().get(index).getStore_id();
                String name = cartState.getShopList().get(index).getStore_name();
                cartState.getUser().setStore_id(store);
                cartState.getUser().setName(name);

                //跳转到轮播图的activity
                initIntent(TabActivity.class);
            }
        });
        picker.show();

    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @OnClick({R.id.rcrl_login_enter})
    public void onClick(View v) {
        switch (v.getId()) {
            //登陆按钮
            case R.id.rcrl_login_enter:
                user = xetLoginAccount.getText().toString();
                //判断是否账号为空
                if (user.length() <= 0) {
                    cartState.initToast(context, "请输入账号", true, 0);
                    return;
                }

                //判断是否为手机号码
                if (!cartState.isMobile(user)) {
                    cartState.initToast(context, "请输入正确的手机号码", true, 0);
                    return;
                }

                word = xetLoginWord.getText().toString();
                //判断密码是否为空
                if (word.length() <= 0) {
                    cartState.initToast(context, "请输入密码", true, 0);
                    return;
                }

                WhyDilogFragment whyDilogFragment = new WhyDilogFragment();
                whyDilogFragment.setInit(1, "提示", "您是否需要登陆", "否", "是");
                whyDilogFragment.show(getSupportFragmentManager(), "登陆");
                break;
        }
    }

    /**
     * 返回建关闭当前页面
     */
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
        //注销eventBus
        eventBus.unregister(this);
    }

    /**
     * 事件通用总线信息
     *
     * @param m
     */
    @Subscribe
    public void onEventMainThread(ExitFind m) {

    }

    /**
     * 登陆
     *
     * @param m
     */
    @Subscribe
    public void onEventMainThread(LoginFind m) {

        Map<String, Object> params = new HashMap<String, Object>();
        //账号
        params.put("act", user);
        //密码
        params.put("pwd", word);
        //实例化网络请求接口(PS:封闭的okhttp)
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.post(CartAddaress.LOGIN, true, "登陆中", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                //能正确得到json
                Log.e(TAG, "---登陆---" + response);
                //实例化JSON
                JSONObject responseJSON = JSON.parseObject(response);
                //状态
                int code = responseJSON.getInteger("code");
                String msg = responseJSON.getString("msg");
                if (code == 0) {
                    //登录成功
                    cartState.initToast(context, "登陆成功", true, 0);
                    //保存一下账号和密码
                    cartState.getUser().setUser(user);
                    cartState.getUser().setWord(word);
                    //获取当前门店的JSON
                    JSONObject dataJSON = JSON.parseObject(responseJSON.getString("data"));
                    String store = dataJSON.getString("store");
                    //保存到集合内
                    //先清空，以保证数据为空
                    cartState.getShopList().clear();
                    //保存到当前集合中
                    List<Shop> plantNumberList = gson.fromJson(store, new TypeToken<List<Shop>>() {
                    }.getType());
                    if (plantNumberList.size() <= 0) {
                        cartState.initToast(context, "门店为空,请重新登陆", true, 0);
                        return;
                    }
                    //将门店集合保存到中间逻辑层中
                    cartState.setShopList(plantNumberList);
                    //弹出门店对话框供用户选择
                    initShop(plantNumberList);
                } else {
                    //登录失败(PS:与服务器交互后服务器产生的错误信息)
                    //判断一下错误提示是否为空，为空就人为添加一个错误信息，否则显示为空的话，太过于给用户莫名其妙的感觉
                    if (TextUtils.equals(msg, "")) {
                        msg = "未知错误";
                    }
                    switch (code) {
                        default:
                            cartState.initToast(context, msg, true, 0);
                            break;
                    }
                }
            }

            @Override
            public void onOkHttpCode(String code, int state) {
                //无网,json错误
                cartState.initToast(context, code, true, 0);
            }

            @Override
            public void onOkHttpError(String error) {
                //请求失败
                cartState.initToast(context, error, true, 0);
            }
        });
    }
}
