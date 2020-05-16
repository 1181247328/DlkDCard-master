package com.cdqf.cart_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.cart.R;
import com.cdqf.cart_class.Card;
import com.cdqf.cart_dilog.WhyDilogFragment;
import com.cdqf.cart_find.ExitFind;
import com.cdqf.cart_find.TabCardReadFind;
import com.cdqf.cart_okhttp.OKHttpRequestWrap;
import com.cdqf.cart_okhttp.OnHttpRequest;
import com.cdqf.cart_state.BaseActivity;
import com.cdqf.cart_state.CartAddaress;
import com.cdqf.cart_state.CartState;
import com.cdqf.cart_state.StaturBar;
import com.cdqf.cart_view.VerticalSwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readTwoGeneralCard.ActiveCallBack;
import com.readTwoGeneralCard.OTGReadCardAPI;
import com.readTwoGeneralCard.Serverinfo;
import com.readTwoGeneralCard.eCardType;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * 轮播图活动界面
 */
public class TabActivity extends BaseActivity {

    //当前打印名称
    private String TAG = TabActivity.class.getSimpleName();

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

    /****************组件注册*****************/

    //下拉刷新
    @BindView(R.id.srl_plant_pull)
    public VerticalSwipeRefreshLayout srlPlantPull = null;

    //返回
    @BindView(R.id.rl_tab_return)
    public RelativeLayout rlTabReturn = null;

    //进度条显示
    @BindView(R.id.rl_orders_bar)
    public RelativeLayout rlOrdersBar = null;
    @BindView(R.id.pb_orders_bar)
    public ProgressBar pbOrdersBar = null;

    //错误显示
    @BindView(R.id.tv_orders_abnormal)
    public TextView tvOrdersAbnormal = null;

    //轮播图组件注册
    @BindView(R.id.ll_tab_banner)
    public LinearLayout llTabBanner = null;
    @BindView(R.id.mbv_tab_banner)
    public MZBannerView mbvTabBanner = null;


    /**************读取身份证信息***************/

    //实例化
    private OTGReadCardAPI otgReadCardAPI = null;

    //key
    private String appKey = "99ffb2f98a29071107c7a09ad2c6d096";

    //身份证解码服务器列表
    private String oneCardServer = "id.yzfuture.cn";

    //定义一个线程池来保证身份证信息的读取
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

    private boolean isReadCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //加载布局
        setContentView(R.layout.activity_tab);

        StaturBar.setStatusBar(this, R.color.white);

        //改变沉侵式状态栏文字颜色
        cartState.changeStatusBarTextImgColor(this, true);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();
    }

    private void initAgo() {
        context = this;
        ButterKnife.bind(this);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {
        srlPlantPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initPull(false);
            }
        });
    }

    private void initBack() {
        //将下拉刷新关闭(刚进入界面的时候由进度条(pbOrdersBar)来显示请求服务器等待组件，加强用户体验)
        srlPlantPull.setEnabled(false);
        //开始请求轮播图
        initPull(false);
        //读取身份证相关信息并开启一个延时两秒后执行，目的为：发现进入主界面时会阻塞线程,情景为，
        // 当用户将身份证放在刷卡器上时，如果是从登陆页进入就会阻塞，表现为卡一下界面，体验不好
        scheduledThreadPool.schedule(new Runnable() {

            @Override
            public void run() {
                initReadCard();
            }
        }, 2, TimeUnit.SECONDS);

    }

    /**
     * 轮播
     *
     * @param bannerList
     */
    private void initBanner(List<String> bannerList) {

        mbvTabBanner.setPages(bannerList, new MZHolderCreator<BannerViewHolder>() {

            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        //间隔时间
        mbvTabBanner.setDelayedTime(3000);
        //是否显示指示器
        mbvTabBanner.setIndicatorVisible(false);
        //切换速度
        mbvTabBanner.setDuration(1000);
        //判断是否需要开启轮播
        if (bannerList.size() <= 1) {
            mbvTabBanner.setCanLoop(false);
        } else {
            mbvTabBanner.setCanLoop(true);
        }
        //开始轮播
        mbvTabBanner.start();
    }

    /**
     * 请求banner
     *
     * @param isToast
     */
    private void initPull(boolean isToast) {
        Map<String, Object> params = new HashMap<String, Object>();
        //账号
        params.put("store_id", cartState.getUser().getStore_id());
        //实例化网络请求接口(PS:封闭的okhttp)
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.post(CartAddaress.BANNER, isToast, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                //能正确得到json
                Log.e(TAG, "---轮播图---" + response);
                //下拉刷新状态
                if (srlPlantPull != null) {
                    srlPlantPull.setEnabled(true);
                    srlPlantPull.setRefreshing(false);
                }
                //实例化JSON
                JSONObject responseJSON = JSON.parseObject(response);
                //状态
                int code = responseJSON.getInteger("code");
                String msg = responseJSON.getString("msg");
                if (code == 0) {
                    //组件显示情况
                    viewLity(View.GONE, View.GONE, View.VISIBLE);
                    //获取当前门店的图片
                    String data = responseJSON.getString("data");
                    //保存到当前集合中
                    List<String> bannerList = gson.fromJson(data, new TypeToken<List<String>>() {
                    }.getType());
                    //轮播开始
                    initBanner(bannerList);
                } else {
                    //组件状态显示
                    viewLity(View.GONE, View.VISIBLE, View.GONE);
                    //请求失败(PS:与服务器交互后服务器产生的错误信息)
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
                //下拉刷新状态
                if (srlPlantPull != null) {
                    srlPlantPull.setEnabled(true);
                    srlPlantPull.setRefreshing(false);
                }
                //无网,json错误
                cartState.initToast(context, code, true, 0);
                //组件状态显示
                viewLity(View.GONE, View.VISIBLE, View.GONE);
            }

            @Override
            public void onOkHttpError(String error) {
                //下拉刷新状态
                if (srlPlantPull != null) {
                    srlPlantPull.setEnabled(true);
                    srlPlantPull.setRefreshing(false);
                }
                //请求失败
                cartState.initToast(context, error, true, 0);
                //组件状态显示
                viewLity(View.GONE, View.VISIBLE, View.GONE);
            }
        });
    }

    /**
     * 各组件显示情况
     *
     * @param bar
     */
    private void viewLity(int bar, int mal, int ban) {
        //进度条显示情况
        rlOrdersBar.setVisibility(bar);
        //错误信息组件显示情况
        tvOrdersAbnormal.setVisibility(mal);
        //轮播图显示状况
        llTabBanner.setVisibility(ban);
    }

    /**
     * 退出登陆
     */
    private void exitLogin() {
        WhyDilogFragment whyDilogFragment = new WhyDilogFragment();
        whyDilogFragment.setInit(0, "提示", "是否退出登录", "否", "是");
        whyDilogFragment.show(getSupportFragmentManager(), "退出登录");
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    /**
     * 读取身份证信息
     */
    private void initReadCard() {
        otgReadCardAPI = new OTGReadCardAPI(getApplicationContext(), new ActiveCallBack() {
            @Override
            public void readProgress(int i) {
                Log.e(TAG, "---readProgress---" + i);
            }

            @Override
            public void setUserInfo(String s) {
                Log.e(TAG, "---setUserInfo---" + s);
            }

            @Override
            public void DeviceOpenFailed(boolean b, boolean b1) {
                Log.e(TAG, "---DeviceOpenFailed---" + b + "---" + b1);
            }
        }, false);
        //身份证解码服务器列表
        ArrayList<Serverinfo> serviceInfoList = new ArrayList<>();
        //oneCardServer = https://item.taobao.com/item.htm?id=611230762637
        serviceInfoList.add(new Serverinfo(oneCardServer, 8848));
        //配置
        otgReadCardAPI.setServerInfo(serviceInfoList, null, false);
        //刚开始的时候要执行一次身份证读取(用户有可能在进入界面的时候将身份证放到刷卡器上)，到这一步之前的全是初始化和配置信息
        isReadCode();
        //定义一个长线程池(安全型，保证不会崩死程序),在1S后每过1S执行一次读取身份证信息操作(缺点，比较占用资源，但以现在的机型和配置，这点资源耗得起)
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                isReadCode();
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    /**
     * 判断是否解码成功
     */
    private void isReadCode() {
        //判断是否解码成功
        int code = otgReadCardAPI.NfcReadCard(appKey, null, null, eCardType.eTwoGeneralCard, "", false);
        if (code == 90) {
            //只有当isReadCard=false时才执行身份证读卡操作，目的为保证读卡只有一次
            if (isReadCard) {
                return;
            }
            //保证在现有这一次身份证读卡操作只有一次
            isReadCard = true;
            //解码成功
            Log.e(TAG, "---解码成功---");
            //实例化一个身份证类
            Card card = new Card();
            //姓名
            String cardName = otgReadCardAPI.GetTwoCardInfo().szTwoIdName;
            card.setSzTwoIdName(cardName);
            //性别
            String cardSex = otgReadCardAPI.GetTwoCardInfo().szTwoIdSex;
            card.setSzTwoIdSex(cardSex);
            //民族
            String cardNation = otgReadCardAPI.GetTwoCardInfo().szTwoIdNation;
            card.setSzTwoIdNation(cardNation);
            //出生日期
            String cardBirthday = otgReadCardAPI.GetTwoCardInfo().szTwoIdBirthday;
            card.setSzTwoIdBirthday(cardBirthday);
            //住址
            String cardAddress = otgReadCardAPI.GetTwoCardInfo().szTwoIdAddress;
            card.setSzTwoIdAddress(cardAddress);
            //身份证号码
            String cardNo = otgReadCardAPI.GetTwoCardInfo().szTwoIdNo;
            card.setSzTwoIdNo(cardNo);
            //签发机关
            String cardSignedDepartment = otgReadCardAPI.GetTwoCardInfo().szTwoIdSignedDepartment;
            card.setSzTwoIdSignedDepartment(cardSignedDepartment);
            //有效起始日期
            String cardValidityPeriodBegin = otgReadCardAPI.GetTwoCardInfo().szTwoIdValidityPeriodBegin;
            card.setSzTwoIdValidityPeriodBegin(cardValidityPeriodBegin);
            //有效截止日期
            String cardValidityPeriodEnd = otgReadCardAPI.GetTwoCardInfo().szTwoIdValidityPeriodEnd;
            card.setSzTwoIdValidityPeriodEnd(cardValidityPeriodEnd);
            //最新住址
            String cardNewAddress = otgReadCardAPI.GetTwoCardInfo().szTwoIdNewAddress;
            card.setSzTwoIdNewAddress(cardNewAddress);
            //照片信息
            byte[] cardPhotos = otgReadCardAPI.GetTwoCardInfo().arrTwoIdPhoto;
            card.setArrTwoIdPhoto(cardPhotos);
            //保存为base64位
            Bitmap bitmap = BitmapFactory.decodeByteArray(cardPhotos, 0, cardPhotos.length);
            String cardBase64 = cartState.bitmapToBase64(bitmap,false,"png");
            card.setArrTwoIdPhotoBase64(cardBase64);
//            //指纹信息
//            byte[] cardFingerprint = otgReadCardAPI.GetTwoCardInfo().arrTwoIdFingerprint;
//            card.setArrTwoIdFingerprint(cardFingerprint);

            String cardSNID = otgReadCardAPI.GetTwoCardInfo().szSNID;
            card.setSzSNID(cardSNID);

            String cardDNID = otgReadCardAPI.GetTwoCardInfo().szDNID;
            card.setSzDNID(cardDNID);

            //通行证类号码
            String cardOtherNO = otgReadCardAPI.GetTwoCardInfo().szTwoOtherNO;
            card.setSzTwoOtherNO(cardOtherNO);
            //签发次数
            String cardSignNum = otgReadCardAPI.GetTwoCardInfo().szTwoSignNum;
            card.setSzTwoSignNum(cardSignNum);
            //预留区
            String cardRemark1 = otgReadCardAPI.GetTwoCardInfo().szTwoRemark1;
            card.setSzTwoRemark1(cardRemark1);
            //证件类型标识
            String cardType = otgReadCardAPI.GetTwoCardInfo().szTwoType;
            card.setSzTwoType(cardType);
            //预留区
            String cardRemark2 = otgReadCardAPI.GetTwoCardInfo().szTwoRemark2;
            card.setSzTwoRemark2(cardRemark2);
            //保存身份证信息
            cartState.setCard(card);
            Log.e(TAG, "---姓名---" + cardName + "---性别---" + cardSex + "---身份证号码---" + cardNo);

            //跳转到人脸采集界面
            initIntent(ContrastActivity.class);
        } else if (code == 41) {
            //将本次的身份证读取操作改为false保证下一次用户将身份证上放到读卡器时能够再一次执行身份证信息读取
            isReadCard = false;
            //保证身份证信息在中间逻辑层是为空
            cartState.setCard(null);
            //解码失败
            Log.e(TAG, "---解码失败---");
        } else {
            //TODO 判断解码身份证(default)
            //将本次的身份证读取操作改为false保证下一次用户将身份证上放到读卡器时能够再一次执行身份证信息读取
            isReadCard = false;
            //保证身份证信息在中间逻辑层是为空
            cartState.setCard(null);
        }
    }

    @OnClick({R.id.rl_tab_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_tab_return:
                //判断是否解码成功
                exitLogin();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        exitLogin();
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
        mbvTabBanner.pause();
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
        mbvTabBanner.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        if (scheduledThreadPool != null) {
            scheduledThreadPool.shutdown();
        }
        eventBus.unregister(this);
    }

    /**
     * 退出登录
     *
     * @param r
     */
    @Subscribe
    public void onEventMainThread(ExitFind r) {
        finish();
    }

    /**
     * 复位刷卡
     *
     * @param t
     */
    @Subscribe
    public void onEventMainThread(TabCardReadFind t) {
        isReadCard = false;
    }

    class BannerViewHolder implements MZViewHolder<String> {

        private ImageView ivShopItemImage = null;

        @Override
        public View createView(Context context) {
            //注册view
            View view = LayoutInflater.from(context).inflate(R.layout.item_shop_banner, null);
            //图片组件注册,用于显示轮播图的图片
            ivShopItemImage = view.findViewById(R.id.iv_shop_item_image);
            return view;
        }

        @Override
        public void onBind(Context context, int i, String s) {
            //通过imageLolader第三方显示图片，(PS:此第三方组件能有效防止内存泄露,但并非百分之百)
            imageLoader.displayImage(s, ivShopItemImage, cartState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        }
    }

}

