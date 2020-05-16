package com.cdqf.cart_txmap;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;


import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import de.greenrobot.event.EventBus;

/**
 * 暂用于定位
 * Created by liu on 2018/7/4.
 */

public class TXMapLoad {

    private static String TAG = TXMapLoad.class.getSimpleName();

    private static TencentLocationManager mLocationManager = null;

    private static TencentLocationRequest request = null;

    private static TxTencentLocationListener txTencentLocationListener = null;

    private static EventBus eventBus = EventBus.getDefault();

    private static int interval = 1000;

    private static boolean cache = false;

    private static String qq = "";

    private static int req = TencentLocationRequest.REQUEST_LEVEL_NAME;

    /**
     * 注册监听器失败
     */
    private static String[] manager = {
            "注册位置监听器成功",
            "设备缺少使用腾讯定位SDK需要的基本条件",
            "配置的Key不正确",
            "自动加载libtencentloc.so失败"
    };

    /**
     * 定位失败
     */
    private static String[] load = {
            "定位成功",
            "网络问题引起的定位失败",
            "GPS, Wi-Fi 或基站错误引起的定位失败",
            "无法将WGS84坐标转换成GCJ-02坐标时的定位失败",
            "未知原因引起的定位失败"
    };

    /**
     * 初始化
     *
     * @param activity
     */
    public static String init(Activity activity) {
        return init(activity, interval);
    }

    public static String init(Activity activity, int interval) {
        return init(activity, interval, cache);
    }

    public static String init(Activity activity, int interval, boolean cache) {
        return init(activity, interval, cache, qq);
    }

    /**
     * @param activity
     * @param interval 设置定位周期(位置监听器回调周期), 单位为 ms (毫秒)
     * @param cache    设置是否允许使用缓存, 连续多次定位时建议允许缓存
     * @param qq       设置 QQ 号
     */
    public static String init(Activity activity, int interval, boolean cache, String qq) {
        mLocationManager = TencentLocationManager.getInstance(activity);
        request = TencentLocationRequest.create();
        request.setRequestLevel(req)
                .setInterval(interval)
                .setAllowDirection(cache)
                .setQQ(qq);
        int error = mLocationManager.requestLocationUpdates(request, txTencentLocationListener = new TxTencentLocationListener());
        Log.e(TAG, "error = " + error);
        return manager[error];
    }

    /**
     * @param req REQ_LEVEL_GEO        0	包含经纬度
     *            REQ_LEVEL_NAME	    1	包含经纬度, 位置名称, 位置地址
     *            REQ_LEVEL_ADMIN_AREA	3	包含经纬度，位置所处的中国大陆行政区划
     *            REQ_LEVEL_POI	    4 	包含经纬度，位置所处的中国大陆行政区划及周边POI列表
     */
    public static void setLevel(int req) {
        TXMapLoad.req = req;
    }

    /**
     * 删除监听器
     */
    public static void remove() {
        mLocationManager.removeUpdates(txTencentLocationListener);
    }

    /**
     * 监听器
     */
    static class TxTencentLocationListener implements TencentLocationListener {

        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
            if (TencentLocation.ERROR_OK == i) {
                Log.e(TAG, "---定位成功---" + i);
                TxMap.getTxMap().setLongitude(tencentLocation.getLongitude());
                TxMap.getTxMap().setLatitude(tencentLocation.getLatitude());
                TxMap.getTxMap().setAddress(tencentLocation.getAddress());
                TxMap.getTxMap().setProvince(tencentLocation.getProvince());
                TxMap.getTxMap().setCity(tencentLocation.getCity());
                eventBus.post(new LoadFind(tencentLocation.getLongitude(), tencentLocation.getLatitude()));
                eventBus.post(new LoadsFind(tencentLocation.getLongitude(), tencentLocation.getLatitude()));
//                eventBus.post(new LoadFind(tencentLocation.getAddress(),i));
                eventBus.post(new TXPoiFind(tencentLocation.getPoiList()));
                remove();
            } else {
                Log.e(TAG, "---定位失败---" + i);
                String loads = "";
                if (i == 404) {
                    loads = load[load.length - 1];
                } else {
                    loads = load[i];
                }
                eventBus.post(new LoadFind(loads, i));
                eventBus.post(new LoadsFind(loads, i));
            }
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {
            Log.e(TAG, "---" + s + "---" + i + "---" + s1);
            if (TextUtils.equals(s, "cell")) {
                String status = "";
                switch (i) {
                    case 0:
                        status = "模块关闭";
                        break;
                    case 1:
                        status = "模块开启";
                        break;
                    case 2:
                        status = "定位权限被禁止";
                        break;
                }
                eventBus.post(new StatusFind(s, status, s1));
            } else if (TextUtils.equals(s, "wifi")) {
                String status = "";
                switch (i) {
                    case 0:
                        status = "Wi-Fi开关关闭";
                        break;
                    case 1:
                        status = "Wi-Fi开关打开";
                        break;
                    case 2:
                        status = "权限被禁止";
                        break;
                    case 5:
                        status = "位置信息开关关闭";
                        break;
                }
                eventBus.post(new StatusFind(s, status, s1));
            } else if (TextUtils.equals(s, "gps")) {
                String status = "";
                switch (i) {
                    case 0:
                        status = "GPS开关关闭";
                        break;
                    case 1:
                        status = "GPS开关打开";
                        break;
                    case 3:
                        status = "GPS可用,代表GPS开关打开,且搜星定位成功";
                        break;
                    case 4:
                        status = "GPS不可用";
                        break;
                }
                eventBus.post(new StatusFind(s, status, s1));
            }
        }
    }
}
