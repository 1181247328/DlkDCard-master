package com.cdqf.cart_state;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.apkfuns.xprogressdialog.XProgressDialog;
import com.cdqf.cart.R;
import com.cdqf.cart_ble.Ble;
import com.cdqf.cart_class.Card;
import com.cdqf.cart_class.Shop;
import com.cdqf.cart_class.User;
import com.cdqf.cart_libfind.ImageListFind;
import com.cdqf.cart_libfind.PhotoBitmapFind;
import com.cdqf.cart_libfind.PhotoFileFind;
import com.cdqf.cart_service.DownloadUpdateDilogFragment;
import com.cdqf.cart_service.DownloadUpdateFind;
import com.cdqf.cart_service.Province;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.mylhyl.circledialog.CircleDialog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;
import com.zxy.tiny.callback.FileCallback;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import okhttp3.Call;

/**
 * 状态层
 * Created by liu on 2017/7/14.
 */

public class CartState extends BaseActivity {

    private String TAG = CartState.class.getSimpleName();

    //中间层
    private static CartState cartState = new CartState();

    public static CartState getCartState() {
        return cartState;
    }

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private XProgressDialog xProgressDialog = null;

    //头像
    private Bitmap headBitmap = null;

    private boolean isPrssor = true;

    private BluetoothAdapter mBluetoothAdapter = null;

    private Map<Integer, Ble> bleMapList = new HashMap<>();

    private Map<Integer, Boolean> titleList = new HashMap<>();

    public List<String> bleList = new CopyOnWriteArrayList<>();

    //省
    private List<String> options1Items = new ArrayList<>();

    //市
    private List<List<String>> options2Items = new ArrayList<>();

    //区
    private List<List<List<String>>> options3Items = new ArrayList<>();

    //省市区
    private List<Province> provinceList = new CopyOnWriteArrayList<Province>();

    //判断登录
    private boolean isLogin = false;

    private Uri photoUri = null;

    //相机
    private static final int REQUEST_CODE_TAKE_PICTURE = 1;

    //多张图片集合
    private List<LocalMedia> selectList = new CopyOnWriteArrayList<>();

    //多张图片
    private List<String> imageList = new CopyOnWriteArrayList<>();

    private String fileApkName = "";

    private File fileApk = null;

    private boolean isAPk = false;

    private CircleDialog.Builder builder = null;

    private String downLoadrul = "";

    private String authority = "";

    public void register() {
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }


    /***********************以下为本项目所用*******************/
    //门店集合列表
    private List<Shop> shopList = new CopyOnWriteArrayList<>();

    //用户相关类
    private User user = new User();

    //身份证信息
    private Card card = new Card();

    /**
     * 提示信息
     *
     * @param context
     * @param toast
     */
    public void initToast(Context context, String toast, boolean isShort, int type) {
        Toast initToast = null;
        if (isShort) {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        } else {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_LONG);
        }

        switch (type) {
            case 0:
                break;
            //显示中间
            case 1:
                initToast.setGravity(Gravity.CENTER, 0, 0);
                initToast.show();
                break;
            //顶部显示
            case 2:
                initToast.setGravity(Gravity.TOP, 0, 0);
                break;
        }
        initToast.show();
    }

    public void initToast(Context context, String toast, boolean isShort, int type, int timer) {
        Toast initToast = null;
        if (isShort) {
            initToast = Toast.makeText(context, toast, Toast.LENGTH_SHORT);
        } else {
            initToast = Toast.makeText(context, toast, timer);
        }

        switch (type) {
            case 0:
                break;
            //显示中间
            case 1:
                initToast.setGravity(Gravity.CENTER, 0, 0);
                initToast.show();
                break;
            //顶部显示
            case 2:
                initToast.setGravity(Gravity.TOP, 0, 0);
                break;
        }
        initToast.show();
    }

    public String getPlantString(Context context, int resId) {
        return context.getResources().getString(resId);
    }


    /**
     * @param loading 加载图片时的图片
     * @param empty   没图片资源时的默认图片
     * @param fail    加载失败时的图片
     * @return
     */
    public DisplayImageOptions getImageLoaderOptions(int loading, int empty, int fail) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(empty)
                .showImageOnFail(fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .build();
        return options;
    }

    /**
     * 为头像而准备
     *
     * @param loading
     * @param empty
     * @param fail
     * @return
     */
    public DisplayImageOptions getHeadImageLoaderOptions(int loading, int empty, int fail) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loading)
                .showImageForEmptyUri(empty)
                .showImageOnFail(fail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        return options;
    }

    /**
     * 保存图片的配置
     *
     * @param context
     * @param cache   "imageLoaderworld/Cache"
     */
    public ImageLoaderConfiguration getImageLoaderConfing(Context context, String cache) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, cache);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(10)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .discCache(new UnlimitedDiskCache(cacheDir))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .writeDebugLogs()
                .build();
        return config;
    }

    /**
     * 初始化imageLoad
     *
     * @param context
     * @return
     */
    public ImageLoaderConfiguration getConfiguration(Context context) {
        ImageLoaderConfiguration configuration = getImageLoaderConfing(context, "imageLoaderword/Chace");
        return configuration;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本有错";
        }
    }

    /**
     * 权限
     */
    public void permission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.NFC)
                    != PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.NFC,},
                        0
                );
            }
        }
    }

    /**
     * webView处理
     *
     * @param wvDrunkCarrier
     */
    public WebSettings webSettings(WebView wvDrunkCarrier) {
        WebSettings wsDrunkCarrier = wvDrunkCarrier.getSettings();
        //自适应屏幕
        wsDrunkCarrier.setUseWideViewPort(true);
        wsDrunkCarrier.setLoadWithOverviewMode(true);
        //支持网页放大缩小
        wsDrunkCarrier.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        wsDrunkCarrier.setUseWideViewPort(true);
        wsDrunkCarrier.setLoadWithOverviewMode(true);
        wsDrunkCarrier.setSavePassword(true);
        wsDrunkCarrier.setSaveFormData(true);
        wsDrunkCarrier.setJavaScriptEnabled(true);
        wsDrunkCarrier.setGeolocationEnabled(true);
        wsDrunkCarrier.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        wsDrunkCarrier.setDomStorageEnabled(true);
//        wsDrunkCarrier.setBuiltInZoomControls(true);
//        wsDrunkCarrier.setSupportZoom(true);
//        wsDrunkCarrier.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //把图片加载放在最后来加载
        wsDrunkCarrier.setBlockNetworkImage(false);
        //可以加载javascript
        wsDrunkCarrier.setJavaScriptEnabled(true);
        //设置缓存模式
        wsDrunkCarrier.setAppCacheEnabled(true);
        wsDrunkCarrier.setAllowFileAccess(true);
        wsDrunkCarrier.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //开启 DOM storage API 功能
        wsDrunkCarrier.setDomStorageEnabled(true);
        //开启 database storage API 功能
        wsDrunkCarrier.setDatabaseEnabled(true);
        //开启 Application Caches 功能
        wsDrunkCarrier.setAppCacheEnabled(false);
        //是否调用jS中的代码
        wsDrunkCarrier.setJavaScriptEnabled(true);
        wsDrunkCarrier.setJavaScriptCanOpenWindowsAutomatically(true);
        wsDrunkCarrier.setAllowFileAccess(true);
        //支持多点触摸
        wsDrunkCarrier.setBuiltInZoomControls(false);
        wsDrunkCarrier.setDefaultTextEncodingName("UTF-8");
        //自动加载图片
        wsDrunkCarrier.setLoadsImagesAutomatically(true);
        wsDrunkCarrier.setLoadWithOverviewMode(true);
        wsDrunkCarrier.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wsDrunkCarrier.setUseWideViewPort(true);
        wsDrunkCarrier.setSaveFormData(true);
        wsDrunkCarrier.setSavePassword(true);
        return wsDrunkCarrier;
    }

    public ImageLoader getImageLoader(Context context) {
        imageLoader.init(getConfiguration(context));
        return imageLoader;
    }

    /**
     * 判断是不是手机号码
     *
     * @param str
     * @return
     */
    public boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("[1][3456789]\\d{9}"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;

    }

    /**
     * 将Bitmap保存在本地
     *
     * @param bitmap
     */
    public void saveBitmapFile(Bitmap bitmap, String uri) {
        File file = new File(uri);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一张图片
     *
     * @param context
     */
    public void headFail(Context context, String path) {
        cartState.setHeadBitmap(null);
        if (TextUtils.isEmpty(path)) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            file.delete();
        }
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    /**
     * 获取随机数
     *
     * @return
     */
    public int getRandom() {
        Random random = new Random();
        return random.nextInt(1000) + 1;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */

    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码，11位数字，1开通，第二位数必须是3456789这些数字之一 *
     *
     * @param mobileNumber
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            // Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;

        }
        return flag;
    }

    /**
     * 判断是不是网络地址
     *
     * @param url
     * @return
     */
    public boolean isUrl(String url) {
        return url.indexOf("http://") != -1;
    }

    /**
     * 获取年月时分
     *
     * @param onDay
     * @param start
     * @param end
     * @return
     */
    public String getOnDay(String onDay, int start, int end) {
        return onDay.substring(start, end);
    }

    /**
     * 分享
     *
     * @param context
     * @param content
     */
    public void initShar(Context context, String content) {
        Intent intent1 = new Intent(Intent.ACTION_SEND);
        intent1.putExtra(Intent.EXTRA_TEXT, content);
        intent1.setType("text/plain");
        context.startActivity(Intent.createChooser(intent1, getPlantString(context, R.string.share)));
    }

    /**
     * 判断是否是正确的车牌号
     *
     * @param license
     * @return
     */
    public boolean licensePlate(String license) {
        String car_num_Regex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z][A-Z][警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]?[A-Z0-9]{4,5}[A-Z0-9挂学警港澳]$";
        if (TextUtils.isEmpty(license)) {
            return false;
        } else if (license.matches(car_num_Regex)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 生成二维码
     *
     * @param content
     * @param width
     * @param height
     * @return
     */
    public Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 手机号码隐藏中间
     */
    public String phoneEmpty(String pNumber) {
        if (!TextUtils.isEmpty(pNumber) && pNumber.length() > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pNumber.length(); i++) {
                char c = pNumber.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 获取屏幕宽度
     */
    public int getDisPlyWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public int getDisPlyHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 将一个view转化为图片
     *
     * @param view
     */

    public String viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        Bitmap cachebmp = loadBitmapFromView(view);
        FileOutputStream fos;
        String imagePath = "";
        try {
            boolean isHasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                File sdRoot = Environment.getExternalStorageDirectory();
                Log.e(TAG, sdRoot.toString());
                File file = new File(sdRoot, Calendar.getInstance().getTimeInMillis() + ".png");
                fos = new FileOutputStream(file);
                imagePath = file.getAbsolutePath();
            } else throw new Exception("创建文件失败!");
            cachebmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.destroyDrawingCache();
        return imagePath;
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    /**
     * 将一个字符串转化为UTF-8格式
     *
     * @param str
     * @return
     */
    public String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 高德地图中获得SHA1
     *
     * @param context
     * @return
     */
    public static String sHA1(Context context) {
        try {
            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 界面设置状态栏字体颜色
     */
    public void changeStatusBarTextImgColor(Activity activity, boolean isBlack) {
        if (isBlack) {
            //设置状态栏黑色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = activity.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * @param activity 拍摄一张照片
     * @param value    1 = 前置，2 = 后置
     */
    public void photo(Activity activity, int value) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                camera(activity, value);
            }
        } else {
            camera(activity, value);
        }
    }

    /**
     * @param activity
     * @param value    1 = 前置，2 = 后置
     */
    private void camera(Activity activity, int value) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", value);
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    /**
     * 多图效果
     *
     * @param activity
     */
    private void create(Activity activity) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(4)
                .minSelectNum(1)
                .imageSpanCount(4)
                .compress(true)
                .minimumCompressSize(100)
                .imageFormat(PictureMimeType.PNG)
                .selectionMedia(selectList)
                .isCamera(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 将一张图片转换为Base64位
     *
     * @param bitmap
     * @return
     */

    public static String bitmapToBase64(Bitmap bitmap) {
        return bitmapToBase64(bitmap, true, "png");
    }

    public static String bitmapToBase64(Bitmap bitmap, boolean isData, String suffix) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        //转换来的base64码需要加前缀，必须是NO_WRAP参数，表示没有空格。
        if (isData) {
            return "data:image/" + suffix + ";base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        } else {
            //转换来的base64码不需要需要加前缀，必须是NO_WRAP参数，表示没有空格。
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        }
        //转换来的base64码不需要需要加前缀，必须是NO_WRAP参数，表示没有空格。
        //return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * 判断权限状态
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //打开相机，获取一张图片时使用
            case 1:
                if (permissions[0] == Manifest.permission.CAMERA) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //TODO 相机权限同意
                    } else {
                        initToast(this, "请打开相机权限,否则无法使用此功能", true, 0);
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //相机
                case REQUEST_CODE_TAKE_PICTURE:
                    Uri uri = null;
                    if (data != null && data.getData() != null) {
                        Log.e(TAG, "---Uri不为空---");
                        uri = data.getData();
                    } else {
                        Log.e(TAG, "---Uri为空---");
                        uri = photoUri;
                    }
                    Tiny.FileCompressOptions optionsFile = new Tiny.FileCompressOptions();
                    Tiny.getInstance().source(uri).asFile().withOptions(optionsFile).compress(new FileCallback() {
                        @Override
                        public void callback(boolean isSuccess, String outfile, Throwable t) {
                            Log.e(TAG, "---获取的文件名---" + outfile);
                            eventBus.post(new PhotoFileFind(outfile));
                        }
                    });

                    Tiny.FileCompressOptions optionsBitmap = new Tiny.FileCompressOptions();
                    Tiny.getInstance().source(uri).asBitmap().withOptions(optionsBitmap).compress(new BitmapCallback() {
                        @Override
                        public void callback(boolean isSuccess, Bitmap bitmap, Throwable t) {
                            Log.e(TAG, "---已获取bitmap---");
                            eventBus.post(new PhotoBitmapFind(bitmap));
                        }
                    });
                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    imageList.clear();
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.e(TAG, "---1---" + media.getPath() + "---2---" + media.getCutPath() + "---3---" + media.getCompressPath());
                        if (media.isCompressed()) {
                            imageList.add(media.getCompressPath());
                        } else if (media.isCut()) {
                            imageList.add(media.getCutPath());
                        } else {
                            imageList.add(media.getPath());
                        }
                    }
                    eventBus.post(new ImageListFind(imageList));
                    break;
            }
        } else {

        }
    }

    /**
     * 提示更新
     *
     * @param isUpdate 是否强制更新
     */
    public void downDilog(boolean isUpdate, String downLoadrul, String authority) {
        this.downLoadrul = downLoadrul;
        this.authority = authority;
        DownloadUpdateDilogFragment downloadUpdateDilogFragment = new DownloadUpdateDilogFragment();
        downloadUpdateDilogFragment.setInit(isUpdate, "提示", "检测到当前有新版本,是否更新", "否", "是");
        downloadUpdateDilogFragment.show(getSupportFragmentManager(), "是否更新");
    }

    /**
     * @param context
     * @param downLoadrul
     * @param authority   com.cdqf.dire.fileprovider_cart
     */
    public void downLoadur(final Context context, String downLoadrul, final String authority) {
        builder = new CircleDialog.Builder();
        builder.setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setTitle("下载")
                .setProgressText("下载了")
                .show(getSupportFragmentManager());
        OkHttpUtils.get()
                .url(downLoadrul)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), fileApkName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        builder.setProgress(100, (int) (progress * 100)).refresh();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "---onError---");

                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(File response, int id) {
                        Log.e(TAG, "---下载成功---");
                        fileApk = response;
                        isAPk = true;
                        //版本在7.0以上是不能直接通过uri访问的
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                            Uri apkUri = FileProvider.getUriForFile(context, authority, fileApk);
                            //添加这一句表示对目标应用临时授权该Uri所代表的文件
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(fileApk), "application/vnd.android.package-archive");
                            startActivity(intent);
                        }
                    }
                });
    }

    /**
     * 更新
     *
     * @param r
     */
    @Subscribe
    public void onEventMainThread(DownloadUpdateFind r) {
        downLoadur(this, downLoadrul, authority);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    /**
     * 打印数据过长时使用
     *
     * @param log
     * @param showCount 1024
     */
    public void showLogCompletion(String TAG, String log, int showCount) {
        if (log.length() > showCount) {
            String show = log.substring(0, showCount);
            Log.e(TAG, show + "");
            if ((log.length() - showCount) > showCount) {//剩下的文本还是大于规定长度
                String partLog = log.substring(showCount, log.length());
                showLogCompletion(TAG, partLog, showCount);
            } else {
                String surplusLog = log.substring(showCount, log.length());
                Log.e(TAG, surplusLog + "");
            }
        } else {
            Log.e(TAG, log + "");
        }
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public List<Province> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    public List<String> getOptions1Items() {
        return options1Items;
    }

    public void setOptions1Items(List<String> options1Items) {
        this.options1Items = options1Items;
    }

    public List<List<String>> getOptions2Items() {
        return options2Items;
    }

    public void setOptions2Items(List<List<String>> options2Items) {
        this.options2Items = options2Items;
    }

    public List<List<List<String>>> getOptions3Items() {
        return options3Items;
    }

    public void setOptions3Items(List<List<List<String>>> options3Items) {
        this.options3Items = options3Items;
    }

    public List<String> getBleList() {
        return bleList;
    }

    public void setBleList(List<String> bleList) {
        this.bleList = bleList;
    }


    /***************以下为本项目所用*****************/
    public List<Shop> getShopList() {
        return shopList;
    }

    public void setShopList(List<Shop> shopList) {
        this.shopList = shopList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
