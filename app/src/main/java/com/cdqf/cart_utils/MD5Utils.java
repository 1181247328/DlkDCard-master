package com.cdqf.cart_utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 * Created by XinAiXiaoWen on 2017/4/21.
 */

public class MD5Utils {

    public static String getMD5(String code){
        code +="qifengkeji2017-4:zmgx2.0";
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(code.getBytes());
            StringBuffer sb = new StringBuffer();
            for(byte b:digest){
                int i = b&0xFF;
                String hexString = Integer.toHexString(i);
                if(hexString.length() < 2){
                    hexString = "0"+hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMD5Single(String code){
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(code.getBytes());
            StringBuffer sb = new StringBuffer();
            for(byte b:digest){
                int i = b&0xFF;
                String hexString = Integer.toHexString(i);
                if(hexString.length() < 2){
                    hexString = "0"+hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
