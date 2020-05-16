package com.cdqf.dire_3des;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by liu on 2017/8/9.
 */

public class DESUtils {
    public static String TAG = DESUtils.class.getSimpleName();

    private static String hexStr = "0123456789ABCDEF";

    /**
     * 加密
     *
     * @param encryptString 待加密字符串
     * @param encryptKey    密钥
     * @return
     * @throws Exception
     */
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        byte[] iv = encryptKey.getBytes();
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        String encrypt = byte2hex(encryptedData);
        Log.e(TAG, "---加密成功---" + encrypt);
        return encrypt;
    }

    /**
     * 解密
     *
     * @param encryptString
     * @param encryptKey
     * @return
     */
    public static String decodeDES(String encryptString, String encryptKey) throws Exception {
        //向量
        byte[] iv = encryptKey.getBytes();
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        Log.e(TAG, "---解密成功1---");
        byte[] encryptedData = cipher.doFinal(hexStringToBinary(encryptString));
        Log.e(TAG, "---解密成功2---");
        String decode = new String(encryptedData, "UTF-8");
        Log.e(TAG, "---解密成功---" + decode);
        return decode;
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBinary(String hexString) {
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        byte high = 0;
        byte low = 0;
        for (int i = 0; i < len; i++) {
            high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
            low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
            bytes[i] = (byte) (high | low);
        }
        return bytes;
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param b
     * @return
     */
    public static final String byte2hex(byte b[]) {
        if (b == null) {
            throw new IllegalArgumentException(
                    "Argument b ( byte array ) is null! ");
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
}
