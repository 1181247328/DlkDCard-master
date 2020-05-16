package com.cdqf.cart_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.cdqf.cart.R;
import com.cdqf.cart_dilog.WhyDilogFragment;
import com.cdqf.cart_face.Constants;
import com.cdqf.cart_face.DrawHelper;
import com.cdqf.cart_face.DrawInfo;
import com.cdqf.cart_face.FaceRectView;
import com.cdqf.cart_find.ContrastFind;
import com.cdqf.cart_find.TabCardReadFind;
import com.cdqf.cart_okhttp.OKHttpRequestWrap;
import com.cdqf.cart_okhttp.OnHttpRequest;
import com.cdqf.cart_state.BaseActivity;
import com.cdqf.cart_state.CartAddaress;
import com.cdqf.cart_state.CartState;
import com.cdqf.cart_state.StaturBar;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * 人证对比
 */
public class ContrastActivity extends BaseActivity {

    //当前打印名称
    private String TAG = ContrastActivity.class.getSimpleName();

    //上下文
    private Context context = null;

    //中间逻辑层
    private CartState cartState = CartState.getCartState();

    //图片显示对象(用的第三方)
    private ImageLoader imageLoader = ImageLoader.getInstance();

    //事件通用总线(用的第三方)
    private EventBus eventBus = EventBus.getDefault();

    /***************组件注册***************/

    //返回
    @BindView(R.id.rl_contrast_return)
    public RelativeLayout rlContrastReturn = null;

    //相机预览显示数据
    @BindView(R.id.tv_contrast_view)
    public TextureView tvContrastView = null;

    //人脸框
    @BindView(R.id.frv_contrast_view)
    public FaceRectView faceRectView = null;

    //证件
    @BindView(R.id.iv_contrast_card)
    public ImageView ivContrastCard = null;

    //拍摄
    @BindView(R.id.iv_contrast_photo)
    public ImageView ivContrastPhoto = null;

    //姓名
    @BindView(R.id.tv_contrast_name)
    public TextView tvContrastName = null;

    //号码
    @BindView(R.id.tv_contrast_card)
    public TextView tvContrastCard = null;

    //人脸识别类
    private FaceEngine faceEngine = null;

    //相机类，用于预览用户头像
    private Camera camera = null;

    //判断是否已经获取了当前用户的头像了
    private boolean isFace = false;

    //人脸追综辅助类
    private DrawHelper drawHelper = null;

    private Bitmap faceBitmap = null;

    //人证对比不成功时，有三次再次向服务器请求
    private int request = 0;

    //播放请正式摄像头
    private MediaPlayer mediaPlayer = null;

    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(8);

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //加载布局
        setContentView(R.layout.activity_contrast);

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
        faceEngine = new FaceEngine();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {

    }

    private void initBack() {
        //证件照片
        byte[] cardByte = cartState.getCard().getArrTwoIdPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(cardByte, 0, cardByte.length);
        ivContrastCard.setImageBitmap(bitmap);
        //姓名
        tvContrastName.setText(cartState.getCard().getSzTwoIdName());
        //号码
        tvContrastCard.setText(cartState.getCard().getSzTwoIdNo());
        tvContrastView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });
        //激活SDK
        initActive();
        //初始化引擎
        initSDK();
        //相机处理
        initFace();
        //判断当前是否有人进行人证对比
        initCard();
    }

    private void initActive() {
        int code = faceEngine.active(context, Constants.APP_ID, Constants.SDK_KEY);
        switch (code) {
            case ErrorInfo.MOK:
                Log.e(TAG, "---已激活---" + code);
                break;
            case ErrorInfo.MERR_ASF_ALREADY_ACTIVATED:
                //激活SDK成功
                Log.e(TAG, "---已激活SDK---" + code);
                break;
            case ErrorInfo.MERR_ASF_NOT_ACTIVATED:
                Log.e(TAG, "---未激活SDK---" + code);
                break;
            default:
                //激活SDK失败
                Log.e(TAG, "---激活SDK未知---" + code);
                break;
        }
    }

    private void initSDK() {
        int code = faceEngine.init(context,
                DetectMode.ASF_DETECT_MODE_VIDEO,
                DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, 10,
                FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
        switch (code) {
            case ErrorInfo.MOK:
                //引擎激活成功
                Log.e(TAG, "---引擎激活成功---" + code);
                break;
            default:
                //引擎激活失败
                Log.e(TAG, "---引擎激活失败---" + code);
                break;
        }
    }

    private void initFace() {
        tvContrastView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                camera = Camera.open();
                try {
                    camera.setPreviewTexture(surface);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Camera.Parameters parameters = camera.getParameters();
                drawHelper = new DrawHelper(parameters.getPreviewSize().width, parameters.getPreviewSize().height,
                        250, 250, 0, 1, true);
                parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(0).width, parameters.getSupportedPreviewSizes().get(0).height);
                parameters.setPreviewFormat(ImageFormat.NV21);
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {

                        List<FaceInfo> faceInfoList = new ArrayList<>();
                        int code = faceEngine.detectFaces(data,
                                parameters.getSupportedPreviewSizes().get(0).width,
                                parameters.getSupportedPreviewSizes().get(0).height,
                                FaceEngine.CP_PAF_NV21, faceInfoList);
                        if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                            Log.e(TAG, "---检测到的人脸信息---" + faceInfoList.size());
                            List<DrawInfo> drawInfoList = new CopyOnWriteArrayList<>();
                            for (FaceInfo f : faceInfoList) {
                                drawInfoList.add(new DrawInfo(f.getRect()));
                            }
                            drawHelper.draw(faceRectView, drawInfoList);
                            if (!isFace) {
                                if (faceInfoList.size() == 1) {
                                    isFace = true;
                                    Log.e(TAG, "---获得照片---" + data.length);
                                    faceBitmap = runInPreviewFrame(data, parameters);
                                    ivContrastPhoto.setImageBitmap(faceBitmap);
                                    initPull(true);
                                }
                            }
                        } else {
                            isFace = false;
                            Log.e(TAG, "---检测失败---" + code);
                        }
                    }
                });
                camera.setParameters(parameters);
                camera.startPreview();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                Log.e(TAG, "---onSurfaceTextureDestroyed---");
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void initCard() {
        play(R.raw.validation_head);

        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!isFace) {
                    play(R.raw.validation_head);
                }
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    /**
     * 播放相应的音乐
     *
     * @param raw
     */
    private void play(int raw) {
        mediaPlayer = MediaPlayer.create(context, raw);
        mediaPlayer.start();
    }

    /**
     * 请求人证对比
     *
     * @param isToast
     */
    private void initPull(boolean isToast) {
        Map<String, Object> params = new HashMap<String, Object>();
        //身份证Base64
        byte[] cardByte = cartState.getCard().getArrTwoIdPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(cardByte, 0, cardByte.length);
        String cardBase64 = cartState.bitmapToBase64(bitmap);
        params.put("cardImage", cardBase64);

        //头像Base64
        String faceBase64 = cartState.bitmapToBase64(faceBitmap);
        params.put("faceImage", faceBase64);
        //实例化网络请求接口(PS:封闭的okhttp)
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        //CartAddaress.CARDIMAGE
        okHttpRequestWrap.postString(CartAddaress.CARDIMAGE, isToast, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                //能正确得到json
                Log.e(TAG, "---人证对比---" + response);
                //实例化JSON
                JSONObject responseJSON = JSON.parseObject(response);
                //状态
                int code = responseJSON.getInteger("code");
                String msg = responseJSON.getString("msg");
                if (code == 1) {
                    //人证对比成功
                    JSONObject contextJSON = JSON.parseObject(responseJSON.getString("content"));
                    double confidence = contextJSON.getDoubleValue("confidence");
                    if (confidence >= (double) 75) {
                        play(R.raw.validation_successful);
                        cartState.initToast(context, "人证对比成功", true, 0);
                        initPullOne(true);
                    } else {
                        //失败再次人证对比
                        initRequest();
                    }
                } else {
                    //请求失败(PS:与服务器交互后服务器产生的错误信息)
                    //判断一下错误提示是否为空，为空就人为添加一个错误信息，否则显示为空的话，太过于给用户莫名其妙的感觉
                    if (TextUtils.equals(msg, "")) {
                        msg = "未知错误";
                    }
                    switch (code) {
                        default:
                            //失败再次人证对比
                            initRequest();
                            cartState.initToast(context, msg, true, 0);
                            break;
                    }
                }
            }

            @Override
            public void onOkHttpCode(String code, int state) {
                //无网,json错误
                cartState.initToast(context, code, true, 0);
                //失败再次人证对比
                initRequest();
            }

            @Override
            public void onOkHttpError(String error) {
                //请求失败
                cartState.initToast(context, error, true, 0);
                //失败再次人证对比
                initRequest();
            }
        });
    }

    private void initPullOne(boolean isToast) {
        Map<String, Object> params = new HashMap<String, Object>();
        //门店id
        params.put("store_id", cartState.getUser().getStore_id());
        //身份证
        Card card = new Card(
                cartState.getCard().getSzTwoIdName(),
                cartState.getCard().getSzTwoIdSex(),
                cartState.getCard().getSzTwoIdNation(),
                cartState.getCard().getSzTwoIdBirthday(),
                cartState.getCard().getSzTwoIdAddress(),
                cartState.getCard().getSzTwoIdNo(),
                cartState.getCard().getSzTwoIdSignedDepartment(),
                cartState.getCard().getSzTwoIdValidityPeriodBegin(),
                cartState.getCard().getSzTwoIdValidityPeriodEnd(),
                cartState.getCard().getSzTwoIdNewAddress(),
                cartState.getCard().getArrTwoIdPhotoBase64(),
                cartState.getCard().getSzSNID(),
                cartState.getCard().getSzDNID(),
                cartState.getCard().getSzTwoOtherNO(),
                cartState.getCard().getSzTwoSignNum(),
                cartState.getCard().getSzTwoRemark1(),
                cartState.getCard().getSzTwoType(),
                cartState.getCard().getSzTwoRemark2()
        );
        params.put("cardInfo", gson.toJson(card));
        //头像Base64
        String faceBase64 = cartState.bitmapToBase64(faceBitmap, false, "png");
        params.put("faceImg", faceBase64);
        //实例化网络请求接口(PS:封闭的okhttp)
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        //CartAddaress.CARDIMAGE
        okHttpRequestWrap.postString(CartAddaress.READ, isToast, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                //能正确得到json
                Log.e(TAG, "---最后结果---" + response);
                //实例化JSON
                JSONObject responseJSON = JSON.parseObject(response);
                //状态
                int code = responseJSON.getInteger("code");
                String msg = responseJSON.getString("msg");
                if (code == 0) {
                    //人证对比成功
                    cartState.initToast(context, "验证通过,请拿走您的身份证", true, 0);
                    finish();
                } else {
                    //请求失败(PS:与服务器交互后服务器产生的错误信息)
                    //判断一下错误提示是否为空，为空就人为添加一个错误信息，否则显示为空的话，太过于给用户莫名其妙的感觉
                    if (TextUtils.equals(msg, "")) {
                        msg = "未知错误";
                    }
                    switch (code) {
                        default:
                            //失败再次人证对比
                            initRequest();
                            cartState.initToast(context, msg, true, 0);
                            break;
                    }
                }
            }

            @Override
            public void onOkHttpCode(String code, int state) {
                //无网,json错误
                cartState.initToast(context, code, true, 0);
                //失败再次人证对比
                initRequest();
            }

            @Override
            public void onOkHttpError(String error) {
                //请求失败
                cartState.initToast(context, error, true, 0);
                //失败再次人证对比
                initRequest();
            }
        });
    }

    private void initRequest() {
//        if (request < 3) {
//            request++;
//            //播放验证失败后两秒后再请求
//            play(R.raw.validation_failure);
//            scheduledThreadPool.schedule(new Runnable() {
//
//                @Override
//                public void run() {
//                    initPull(true);
//                }
//            }, 2, TimeUnit.SECONDS);
//
//        } else {
        //播放验证失败后两秒关闭当前界面
        play(R.raw.validation_failure);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cartState.initToast(context, "如果再次人证对比,需要重新刷卡", true, 0);
        finish();
//        }
    }

    public Bitmap runInPreviewFrame(byte[] data, Camera.Parameters parameters) {
//        camera.setOneShotPreviewCallback(null);
        //处理data
//        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                parameters.getSupportedPreviewSizes().get(0).width,
                parameters.getSupportedPreviewSizes().get(0).height,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, parameters.getSupportedPreviewSizes().get(0).width,
                parameters.getSupportedPreviewSizes().get(0).height), 100, baos);// 80--JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        return bitmap;
    }

    /**
     * 退出登陆
     */
    private void contrastBanner() {
        WhyDilogFragment whyDilogFragment = new WhyDilogFragment();
        whyDilogFragment.setInit(2, "提示", "是否放弃人证对比,如果放弃需要重新刷卡才可以进行人证对比,请问是否继续", "否", "是");
        whyDilogFragment.show(getSupportFragmentManager(), "返回轮播图");
    }


    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @OnClick({R.id.rl_contrast_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_contrast_return:
                contrastBanner();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        contrastBanner();
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
        scheduledThreadPool.shutdown();
        mediaPlayer.stop();
        eventBus.unregister(this);
        faceEngine.unInit();
    }

    /**
     * 返回到轮播图界面
     *
     * @param c
     */
    @Subscribe
    public void onEventMainThread(ContrastFind c) {
        eventBus.post(new TabCardReadFind());
        finish();
    }

    class Card {
        private String szTwoIdName; // 姓名
        private String szTwoIdSex; // 性别
        private String szTwoIdNation; // 民族
        private String szTwoIdBirthday; // 出生日期
        private String szTwoIdAddress; // 住址
        private String szTwoIdNo; // 身份证号码
        private String szTwoIdSignedDepartment; // 签发机关
        private String szTwoIdValidityPeriodBegin; // 有效期起始日期 YYYYMMDD
        private String szTwoIdValidityPeriodEnd; // 有效期截止日期 YYYYMMDD 有效期为 长期时存储“长期”
        private String szTwoIdNewAddress; // 最新住址
        private String arrTwoIdPhotoBase64; // 照片信息
        //    private byte[] arrTwoIdFingerprint; // 指纹信息
        private String szSNID;
        private String szDNID;
        private String szTwoOtherNO;// 通行证类号码
        private String szTwoSignNum; // 签发次数
        private String szTwoRemark1; // 预留区
        private String szTwoType; //证件类型标识
        private String szTwoRemark2; // 预留区

        public Card(String szTwoIdName, String szTwoIdSex, String szTwoIdNation, String szTwoIdBirthday, String szTwoIdAddress, String szTwoIdNo, String szTwoIdSignedDepartment, String szTwoIdValidityPeriodBegin, String szTwoIdValidityPeriodEnd, String szTwoIdNewAddress, String arrTwoIdPhotoBase64, String szSNID, String szDNID, String szTwoOtherNO, String szTwoSignNum, String szTwoRemark1, String szTwoType, String szTwoRemark2) {
            this.szTwoIdName = szTwoIdName;
            this.szTwoIdSex = szTwoIdSex;
            this.szTwoIdNation = szTwoIdNation;
            this.szTwoIdBirthday = szTwoIdBirthday;
            this.szTwoIdAddress = szTwoIdAddress;
            this.szTwoIdNo = szTwoIdNo;
            this.szTwoIdSignedDepartment = szTwoIdSignedDepartment;
            this.szTwoIdValidityPeriodBegin = szTwoIdValidityPeriodBegin;
            this.szTwoIdValidityPeriodEnd = szTwoIdValidityPeriodEnd;
            this.szTwoIdNewAddress = szTwoIdNewAddress;
            this.arrTwoIdPhotoBase64 = arrTwoIdPhotoBase64;
            this.szSNID = szSNID;
            this.szDNID = szDNID;
            this.szTwoOtherNO = szTwoOtherNO;
            this.szTwoSignNum = szTwoSignNum;
            this.szTwoRemark1 = szTwoRemark1;
            this.szTwoType = szTwoType;
            this.szTwoRemark2 = szTwoRemark2;
        }

        public String getSzTwoIdName() {
            return szTwoIdName;
        }

        public void setSzTwoIdName(String szTwoIdName) {
            this.szTwoIdName = szTwoIdName;
        }

        public String getSzTwoIdSex() {
            return szTwoIdSex;
        }

        public void setSzTwoIdSex(String szTwoIdSex) {
            this.szTwoIdSex = szTwoIdSex;
        }

        public String getSzTwoIdNation() {
            return szTwoIdNation;
        }

        public void setSzTwoIdNation(String szTwoIdNation) {
            this.szTwoIdNation = szTwoIdNation;
        }

        public String getSzTwoIdBirthday() {
            return szTwoIdBirthday;
        }

        public void setSzTwoIdBirthday(String szTwoIdBirthday) {
            this.szTwoIdBirthday = szTwoIdBirthday;
        }

        public String getSzTwoIdAddress() {
            return szTwoIdAddress;
        }

        public void setSzTwoIdAddress(String szTwoIdAddress) {
            this.szTwoIdAddress = szTwoIdAddress;
        }

        public String getSzTwoIdNo() {
            return szTwoIdNo;
        }

        public void setSzTwoIdNo(String szTwoIdNo) {
            this.szTwoIdNo = szTwoIdNo;
        }

        public String getSzTwoIdSignedDepartment() {
            return szTwoIdSignedDepartment;
        }

        public void setSzTwoIdSignedDepartment(String szTwoIdSignedDepartment) {
            this.szTwoIdSignedDepartment = szTwoIdSignedDepartment;
        }

        public String getSzTwoIdValidityPeriodBegin() {
            return szTwoIdValidityPeriodBegin;
        }

        public void setSzTwoIdValidityPeriodBegin(String szTwoIdValidityPeriodBegin) {
            this.szTwoIdValidityPeriodBegin = szTwoIdValidityPeriodBegin;
        }

        public String getSzTwoIdValidityPeriodEnd() {
            return szTwoIdValidityPeriodEnd;
        }

        public void setSzTwoIdValidityPeriodEnd(String szTwoIdValidityPeriodEnd) {
            this.szTwoIdValidityPeriodEnd = szTwoIdValidityPeriodEnd;
        }

        public String getSzTwoIdNewAddress() {
            return szTwoIdNewAddress;
        }

        public void setSzTwoIdNewAddress(String szTwoIdNewAddress) {
            this.szTwoIdNewAddress = szTwoIdNewAddress;
        }

        public String getArrTwoIdPhotoBase64() {
            return arrTwoIdPhotoBase64;
        }

        public void setArrTwoIdPhotoBase64(String arrTwoIdPhotoBase64) {
            this.arrTwoIdPhotoBase64 = arrTwoIdPhotoBase64;
        }

        public String getSzSNID() {
            return szSNID;
        }

        public void setSzSNID(String szSNID) {
            this.szSNID = szSNID;
        }

        public String getSzDNID() {
            return szDNID;
        }

        public void setSzDNID(String szDNID) {
            this.szDNID = szDNID;
        }

        public String getSzTwoOtherNO() {
            return szTwoOtherNO;
        }

        public void setSzTwoOtherNO(String szTwoOtherNO) {
            this.szTwoOtherNO = szTwoOtherNO;
        }

        public String getSzTwoSignNum() {
            return szTwoSignNum;
        }

        public void setSzTwoSignNum(String szTwoSignNum) {
            this.szTwoSignNum = szTwoSignNum;
        }

        public String getSzTwoRemark1() {
            return szTwoRemark1;
        }

        public void setSzTwoRemark1(String szTwoRemark1) {
            this.szTwoRemark1 = szTwoRemark1;
        }

        public String getSzTwoType() {
            return szTwoType;
        }

        public void setSzTwoType(String szTwoType) {
            this.szTwoType = szTwoType;
        }

        public String getSzTwoRemark2() {
            return szTwoRemark2;
        }

        public void setSzTwoRemark2(String szTwoRemark2) {
            this.szTwoRemark2 = szTwoRemark2;
        }
    }

}
