package com.cdqf.cart_state;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Telephone {

    public static String TAG = Telephone.class.getSimpleName();

    /**
     * SIM卡是中国移动
     */
    public static boolean isChinaMobile(Context context) {
        String imsi = getSimOperator(context);
        Log.e(TAG, "--移动---" + imsi);
        if (imsi == null) return false;
        return imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007");
    }

    /**
     * SIM卡是中国联通
     */
    public static boolean isChinaUnicom(Context context) {
        String imsi = getSimOperator(context);
        Log.e(TAG, "--联通---" + imsi);
        if (imsi == null) return false;
        return imsi.startsWith("46001");
    }

    /**
     * SIM卡是中国电信
     */
    public static boolean isChinaTelecom(Context context) {
        String imsi = getSimOperator(context);
        Log.e(TAG, "--电信---" + imsi);
        if (imsi == null) return false;
        return imsi.startsWith("46003");
    }

    private static String getSimOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimOperator();
    }
}
