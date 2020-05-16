package com.cdqf.cart_city;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.cdqf.cart.R;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.picker.DateTimePicker;
import cn.addapp.pickers.picker.SinglePicker;
import cn.addapp.pickers.picker.TimePicker;


/**
 * Created by liu on 2017/8/5.
 */

public class CityJSON {
    private String TAG = CityJSON.class.getSimpleName();
    //省
    private ArrayList<JsonBean> options1Items = new ArrayList<>();

    //市
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    //区
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private ArrayList<String> valueItems = new ArrayList<>();

    private ArrayList<String> jsonBeanList = new ArrayList<>();

    private Thread thread = null;

    private OnCityPickListener onCityPickListener = null;

    private OnWitePickListener onWitePickListener = null;

    private OnTimerPickListener onTimerPickListener = null;

    private OnYearMonthDayTimePickerListener onYearMonthDayTimePickerListener = null;

    private Activity activity = null;

    public void setOnCityPickListener(OnCityPickListener listener) {
        onCityPickListener = listener;
    }

    public void setOnWitePickListener(OnWitePickListener listener) {
        onWitePickListener = listener;
    }

    public void setOnTimerPickListener(OnTimerPickListener listener) {
        onTimerPickListener = listener;
    }

    public void setOnYearMonthDayTimePickerListener(OnYearMonthDayTimePickerListener listener) {
        onYearMonthDayTimePickerListener = listener;
    }

    /**
     * 解析省市区JSON
     *
     * @param context
     */
    public void dataJSON(final Context context) {
        if (thread == null) {//如果已创建就不再重新创建子线程了

            //Toast.makeText(context, "开始解析数据", Toast.LENGTH_SHORT).show();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 写子线程中的操作,解析省市区数据
                    initJsonData(context);
                }
            });
            thread.start();
        }
    }

    private void initJsonData(Context context) {//解析数据
        String JsonData = new GetJsonDataUtil().getJson(context, "province.json");//获取assets目录下的json文件数据
        Log.e(TAG, "---" + JsonData);
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            jsonBeanList.add(jsonBean.get(i).getName());
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d).getName();
                        String value = jsonBean.get(i).getCityList().get(c).getArea().get(d).getValue();
                        valueItems.add(value);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Log.e(TAG, "---解析出的JSON---" + data);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public ArrayList<JsonBean> getOptions1Items() {
        return options1Items;
    }

    public void setOptions1Items(ArrayList<JsonBean> options1Items) {
        this.options1Items = options1Items;
    }

    public ArrayList<ArrayList<String>> getOptions2Items() {
        return options2Items;
    }

    public void setOptions2Items(ArrayList<ArrayList<String>> options2Items) {
        this.options2Items = options2Items;
    }

    public ArrayList<ArrayList<ArrayList<String>>> getOptions3Items() {
        return options3Items;
    }

    public void setOptions3Items(ArrayList<ArrayList<ArrayList<String>>> options3Items) {
        this.options3Items = options3Items;
    }

    public ArrayList<String> getJsonBeanList() {
        return jsonBeanList;
    }

    public void setJsonBeanList(ArrayList<String> jsonBeanList) {
        this.jsonBeanList = jsonBeanList;
    }

    /**
     * 弹出选择器
     *
     * @param activity
     * @param arrayList
     */
    public void optionsPickerSingle(Activity activity, ArrayList<String> arrayList) {
        SinglePicker<String> picker = new SinglePicker<String>(activity, arrayList);
        //是否循环(false=循环,true=不循环)
        picker.setCanLoop(false);
        //顶部标题栏背景颜色
        picker.setTopBackgroundColor(0xFFEEEEEE);
        //顶部高度
        picker.setTopHeight(50);
        //顶部标题栏下划线颜色
        picker.setTopLineColor(0xFF33B5E5);
        //顶部标题栏下划线高度
        picker.setTopLineHeight(1);
        //标题名
        picker.setTitleText("请选择");
        //顶部标题颜色
        picker.setTitleTextColor(0xFF999999);
        //顶部标题栏文字大小
        picker.setTitleTextSize(12);
        //顶部取消按钮文字颜色
        picker.setCancelTextColor(0xFF33B5E5);
        //顶部取消按钮文字大小
        picker.setCancelTextSize(13);
        //顶部确定按钮文字颜色
        picker.setSubmitTextColor(0xFF33B5E5);
        //顶部确定按钮文字大小
        picker.setSubmitTextSize(13);
        //中间滚动选中颜色
        picker.setSelectedTextColor(0xFFEE0000);
        //中间滚动非选中颜色
        picker.setUnSelectedTextColor(0xFF999999);
        LineConfig config = new LineConfig();
        //滚动线颜色
        config.setColor(0xFFEE0000);
        //线透明度
        config.setAlpha(140);
        //线比例
//        config.setRatio((float) (1.0 / 8.0));
        //设置
        picker.setLineConfig(config);
        //宽度
        picker.setItemWidth(200);
        //背景颜色
        picker.setBackgroundColor(0xFFE1E1E1);
        //默认选项
        picker.setSelectedIndex(0);
        //监听
        picker.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                Log.e("CityJSON", "index=" + index + ", item=" + item);
                if (onCityPickListener != null) {
                    onCityPickListener.onItemPicked(index, item);
                }
            }
        });
        //启动
        picker.show();
    }

    /**
     * 省市区三项联动
     *
     * @param activity
     */
    public void optionsPickerWith(Activity activity, Context context, List<String> options1Items, List<List<String>> options2Items, List<List<List<String>>> options3Items) {
        OptionsPickerView pvOptions = getOptionsPickerView(activity, context);
        pvOptions.setPicker(options1Items, options2Items, options3Items);//添加数据源
//        } else if(options == 2){
//            pvOptions.setPicker(options2Items, options3Items);//添加数据源
//        } else {
//            pvOptions.setPicker(options3Items);//添加数据源
//        }
        pvOptions.show();
    }

    /**
     * 市区联动
     *
     * @param activity
     * @param context
     * @param options1Items
     * @param options2Items
     */
    public void optionsPickerWith(Activity activity, Context context, List<String> options1Items, List<List<String>> options2Items) {
        OptionsPickerView pvOptions = getOptionsPickerView(activity, context);
        pvOptions.setPicker(options1Items, options2Items);//添加数据源
        pvOptions.show();
    }

    /**
     * 区
     *
     * @param activity
     * @param context
     * @param options1Items
     */
    public void optionsPickerWith(Activity activity, Context context, List<String> options1Items) {
        OptionsPickerView pvOptions = getOptionsPickerView(activity, context);
        pvOptions.setPicker(options1Items);//添加数据源
        pvOptions.show();
    }

    private OptionsPickerView getOptionsPickerView(Activity activity, Context context) {
        OptionsPickerView<Object> pvOptions = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if (onWitePickListener != null) {
                    onWitePickListener.onWitePick(options1, options2, options3, v);
                }
            }
        }).setTitleText("城市选择")
                .setDividerColor(ContextCompat.getColor(context, R.color.black))
                .setTextColorCenter(ContextCompat.getColor(context, R.color.black)) //设置选中项文字颜色
                .setSubmitColor(ContextCompat.getColor(context, R.color.black))
                .setCancelColor(ContextCompat.getColor(context, R.color.black))
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();
        return pvOptions;
    }

    /**
     * 二十四小时制
     *
     * @param activity
     */
    public void onTimePicker(Activity activity) {
        timerPicker(activity, 0, 0, 23, 0);
    }

    /**
     * 以整点制
     *
     * @param activity
     * @param startWhen
     * @param endwhen
     */
    public void onTimePicker(Activity activity, int startWhen, int endwhen) {
        timerPicker(activity, startWhen, 0, endwhen, 0);
    }

    /**
     * 时间选择器
     *
     * @param activity    父容器
     * @param startWhen   初始时
     * @param startPoints 初始分
     * @param endWhen     结束时
     * @param endPoints   结分
     */
    public void onTimePicker(Activity activity, int startWhen, int startPoints, int endWhen, int endPoints) {
        timerPicker(activity, startWhen, startPoints, endWhen, endPoints);
    }

    /**
     * 时间选择器
     *
     * @param activity    父容器
     * @param startWhen   初始时 startWhen = 6代表早上6点
     * @param startPoints 初始分 startPoints = 0代表整点(6:00)
     * @param endWhen     结束时 endWhen = 18代表晚上六点
     * @param endPoints   结束分 endPoints = 0代表整点(18:00)
     */
    public void timerPicker(Activity activity, int startWhen, int startPoints, int endWhen, int endPoints) {
        TimePicker picker = new TimePicker(activity, TimePicker.HOUR_24);
        picker.setRangeStart(startWhen, startPoints);
        picker.setRangeEnd(endWhen, endPoints);
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setWheelModeEnable(false);
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                if (onTimerPickListener != null) {
                    onTimerPickListener.ontimerPick(hour, minute);
                }
            }
        });
        picker.show();
    }

    /**
     * 完全默认
     *
     * @param activity
     */
    public void onYearMonthDayTimePicker(Activity activity) {
        Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int endYear = startYear + 10;
        yearMonthDayTimePicker(activity, startYear, 1, 1, 00, 00, endYear, 12, 30, 23, 00);
    }

    /**
     * 默认整点
     *
     * @param activity
     * @param startHour
     * @param endHour
     */
    public void onYearMonthDayTimePicker(Activity activity, int startHour, int endHour) {
        Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int endYear = startYear + 10;
        yearMonthDayTimePicker(activity, startYear, 1, 1, startHour, 00, endYear, 12, 30, endHour, 00);
    }

    /**
     * 自定义时分
     *
     * @param activity
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     */
    public void onYearMonthDayTimePicker(Activity activity, int startHour, int startMinute, int endHour, int endMinute) {
        Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int endYear = startYear + 10;
        yearMonthDayTimePicker(activity, startYear, 1, 1, startHour, startMinute, endYear, 12, 30, endHour, endMinute);
    }

    /**
     * 年月日时分
     *
     * @param activity    依赖的活动界面
     * @param startYear   开始年
     * @param startMonth  开始月
     * @param startDay    开始日
     * @param startHour   开始时
     * @param startMinute 开始分
     * @param endYear     结束年
     * @param endMonth    结束月
     * @param endDay      结束日
     * @param endHour     结束时
     * @param endMinute   结束分
     */
    public void onYearMonthDayTimePicker(Activity activity, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        yearMonthDayTimePicker(activity, startYear, startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
    }

    /**
     * 年月日时分选择器
     *
     * @param activity
     * @param startYear
     * @param startMonth
     * @param startDay
     * @param startHour
     * @param startMinute
     * @param endYear
     * @param endMonth
     * @param endDay
     * @param endHour
     * @param endMinute
     */
    private void yearMonthDayTimePicker(Activity activity, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        DateTimePicker picker = new DateTimePicker(activity, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(startYear, startMonth, startDay);
        picker.setDateRangeEnd(endYear, endMonth, endDay);
        picker.setTimeRangeStart(startHour, startMinute);
        picker.setTimeRangeEnd(endHour, endMinute);
        picker.setWeightEnable(true);
        picker.setWheelModeEnable(true);
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                Log.e(TAG, "---" + year + "---" + month + "---" + day + "---" + hour + ":" + minute);
                if (onYearMonthDayTimePickerListener != null) {
                    onYearMonthDayTimePickerListener.onDateTimePicked(year, month, day, hour, minute);
                }
            }
        });
        picker.show();
    }
}
