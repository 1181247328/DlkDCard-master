package com.cdqf.cart_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.cart_city.CityThe;
import com.cdqf.cart_state.ACache;
import com.cdqf.cart_state.CartState;
import com.cdqf.cart_utils.HttpRequestWrap;
import com.cdqf.cart_utils.OnResponseHandler;
import com.cdqf.cart_utils.RequestHandler;
import com.cdqf.cart_utils.RequestStatus;
import com.cdqf.dire_3des.Constants;
import com.cdqf.dire_3des.DESUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 获取地址
 * Created by liu on 2017/12/16.
 */

public class DireService extends Service {

    private String TAG = DireService.class.getSimpleName();

    private EventBus eventBus = EventBus.getDefault();

    private CartState direState = CartState.getCartState();

    private HttpRequestWrap httpRequestWrap = null;

    private ACache aCache = null;

    private Context context = null;

    //省
    private List<String> options1Items = new ArrayList<>();

    //市
    private List<List<String>> options2Items = new ArrayList<>();

    //区
    private List<List<List<String>>> options3Items = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "---创建---");
        context = this;
        httpRequestWrap = new HttpRequestWrap(this);
        aCache = ACache.get(this);
        String result = aCache.getAsString("address");
        if (result == null) {
            httpRequestWrap.setMethod(HttpRequestWrap.POST);
            httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                @Override
                public void onResponse(String result, RequestStatus status) {
                    Log.e(TAG, "---获取地址列表---" + result);
                    if (status == RequestStatus.SUCCESS) {
                        if (result != null) {
                            aCache.put("address", result);
                            initAddress(result);
                        } else {
                            direState.initToast(context, "地址列表失败,请检查网络", true, 0);
                        }
                    } else {
                        direState.initToast(context, "请求失败,请检查网络", true, 0);
                    }
                }
            }));
            Map<String, Object> params = new HashMap<String, Object>();
            String random = direState.getRandom() + "";
            params.put("Random", random);
            String sign = random;
            Log.e(TAG, "---明文---" + sign);
            String signEncrypt = null;
            try {
                signEncrypt = DESUtils.encryptDES(sign, Constants.secretKey.substring(0, 8));
                Log.e(TAG, "---加密成功---");
            } catch (Exception e) {
                Log.e(TAG, "---加密成功---");
                e.printStackTrace();
            }
            if (signEncrypt == null) {
                direState.initToast(context, "加密失败", true, 0);
                return;
            }
            params.put("Sign", signEncrypt);
//            httpRequestWrap.send(PlantAddress.USER_REGION, params);
        } else {
            initAddress(result);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initAddress(String result) {
        JSONObject resultJSON = JSON.parseObject(result);
        String data = null;
        boolean isDeconde = true;
        try {
            data = DESUtils.decodeDES(resultJSON.getString("Data"), Constants.secretKey.substring(0, 8));
            Log.e(TAG, "---解密成功---" + data);
        } catch (Exception e) {
            Log.e(TAG, "---解密失败");
            isDeconde = false;
            e.printStackTrace();
        }
        if (!isDeconde) {
            direState.initToast(context, "解密失败", true, 0);
            return;
        }
        JSONArray dataArray = JSON.parseArray(data);
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataJSON = dataArray.getJSONObject(i);
            JSONArray provinceArray = dataJSON.getJSONArray("ChildAreaRegion");
            //获得省
            for (int p = 0; p < provinceArray.size(); p++) {
                List<String> CityList = new ArrayList<>();
                List<List<String>> Province_AreaList = new ArrayList<>();
                //省JSON
                JSONObject provinceJSON = provinceArray.getJSONObject(p);
                //省id
                String idProvince = provinceJSON.getString("Id");
                //省名
                String regionNameProvince = provinceJSON.getString("RegionName");
                //添加省
                options1Items.add(regionNameProvince);
                //
                String regionLevelProvince = provinceJSON.getString("RegionLevel");
                //
                String parentIdProvince = provinceJSON.getString("ParentId");
                //实列化省
                Province province = new Province(idProvince, regionNameProvince, regionLevelProvince, parentIdProvince);
                //添加省
                direState.getProvinceList().add(province);
                //市
                JSONArray cityArray = provinceJSON.getJSONArray("ChildAreaRegion");
                //获取该省下的市
                for (int c = 0; c < cityArray.size(); c++) {
                    JSONObject cityJSON = cityArray.getJSONObject(c);
                    //市id
                    String idCity = cityJSON.getString("Id");
                    //市名
                    String regionNameCity = cityJSON.getString("RegionName");
                    //添加市
                    CityList.add(regionNameCity);
                    //
                    String regionLevelCity = cityJSON.getString("RegionLevel");
                    //
                    String parentIdCity = cityJSON.getString("ParentId");
                    //实例化市
                    CityThe city = new CityThe(idCity, regionNameCity, regionLevelCity, parentIdCity);
                    //添加市
                    direState.getProvinceList().get(p).getCityTheList().add(city);
                    //区
                    JSONArray areaArray = cityJSON.getJSONArray("ChildAreaRegion");
                    ArrayList<String> areaList = new ArrayList<String>();
                    for (int a = 0; a < areaArray.size(); a++) {
                        //区JSON
                        JSONObject areaJSON = areaArray.getJSONObject(a);
                        //区id
                        String idArea = areaJSON.getString("Id");
                        //区名
                        String regionNameArea = areaJSON.getString("RegionName");
                        //添加区
                        areaList.add(regionNameArea);
                        //
                        String regionLevelArea = areaJSON.getString("RegionLevel");
                        //
                        String parentIdArea = areaJSON.getString("ParentId");
                        Area area = new Area(idArea, regionNameArea, regionLevelArea, parentIdArea);
                        direState.getProvinceList().get(p).getCityTheList().get(c).getAreaList().add(area);
                    }
                    Province_AreaList.add(areaList);
                }
                options2Items.add(CityList);
                options3Items.add(Province_AreaList);
            }
        }
        direState.setOptions1Items(options1Items);
        direState.setOptions2Items(options2Items);
        direState.setOptions3Items(options3Items);
    }
}
