package com.cdqf.cart_state;

import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 获取当前年月日时分以及时间秒
 */
public class CartCalender {
    /**
     * 获取年
     *
     * @return
     */
    public static int getYear() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.YEAR);
    }

    /**
     * 获取月
     *
     * @return
     */
    public static int getMonth() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日
     *
     * @return
     */
    public static int getDay() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.DATE);
    }

    /**
     * 获取时
     *
     * @return
     */
    public static int getHour() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分
     *
     * @return
     */
    public static int getMinute() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MINUTE);
    }

    /**
     * 获取秒
     *
     * @return
     */
    public static int getSecond() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.SECOND);
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        return getFormatedDateTime(System.currentTimeMillis());
    }

    private static String getFormatedDateTime(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    /**
     * 转换时间戳(适用于后台为PHP)
     *
     * @param expire 要转换的数据
     * @param mat    要转换的格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getFetureDate(long expire, String mat) {
        if (String.valueOf(expire).length() == 10) {
            expire = expire * 1000;
        }
        Date date = new Date(expire);
        SimpleDateFormat format = new SimpleDateFormat(mat);
        String result = format.format(date);
        if (result.startsWith("0")) {
            result = result.substring(1);
        }
        return result;
    }

    /**
     * 判断两个日期相差多少天
     *
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */

    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 截取时间将年月日与时分秒分开
     *
     * @param timer
     * @param split
     * @return
     */
    public static String[] departureTime(String timer, String split) {
        String[] departureTime = null;
        try {
            departureTime = timer.split(split);
        } catch (NullPointerException e) {
            e.printStackTrace();
            departureTime = timer.split(" ");
        }
        return departureTime;
    }

    /**
     * 将一个日期格式化的转换为Date
     *
     * @param format 格式yyyy-MM-dd HH:mm:ss
     * @param data
     * @return
     */
    public static Date getData(String format, String data) {
        //共多少晚
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(data);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前月有多少天
     */
    public int getMonthNumber(int year, int month) {
        int day = 0;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;
        }
        return 0;
    }
}
