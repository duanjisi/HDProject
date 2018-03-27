package com.atgc.hd.comm.utils;

import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES/ECB/PKCS5Padding 算法加解密.
 *
 * @author wangguojun
 * @since 2018年1月26日
 */
public class AESUtils {

    private AESUtils() {
    }

    public static final String ENCODING = "UTF-8";

    /**
     * 密钥算法.
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式.
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    /**
     * 加密.
     *
     * @param src  default
     * @param skey default
     * @return byte.
     * @throws Exception exception
     */
    public static byte[] encrypt(String src, String skey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] key = {};
        if (skey.isEmpty()) {
            Logger.e("Key为空null");
            return key;
        }
        // 判断Key是否为16位
        if (skey.length() != 16) {
            Logger.e("Key长度不是16位");
            return key;
        }
        key = skey.getBytes(ENCODING);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR);// "算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        return cipher.doFinal(src.getBytes(ENCODING));
    }

    public static String decrypt(byte[] src, String skey) {
        try {
            String temp = new String(src, "utf-8");
            return new String(decrypt(temp, skey), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密.
     *
     * @param src  default
     * @param skey default
     * @return byte[]
     * @throws Exception           Exception
     * @throws BadPaddingException byte[]
     */
    public static byte[] decrypt(String src, String skey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] key = {};
        // 判断Key是否正确
        if (skey.isEmpty()) {
            Logger.e("Key为空null");
            return key;
        }
        // 判断Key是否为16位
        if (skey.length() != 16) {
            Logger.e("Key长度不是16位");
            return key;
        }
        key = skey.getBytes(ENCODING);
        SecretKeySpec skeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(parseHexStr2Byte(src));

    }

    /**
     * 将二进制转换成16进制.
     *
     * @param buf defualt
     * @return
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制.
     *
     * @param hexStr default
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        byte[] result = {};
        if (hexStr.length() < 1) {
            return result;
        }
        result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "2017020000000000";
        // 需要加密的字串
        String cSrc = "{\"Command\":\"COM_DEV_REGISTER\",\"Data\":[{\"Type\":\"1\",\"deviceID\":\"10012017020000000000\",\"gateWay\":\"00000000000000000\",\"ip\":\"172.16.10.22\",\"locationAddr\":\"南门停车场入口\",\"mac\":\"00:FF:81:99:2F\",\"macNO\":\"102\",\"manufacturer\":\"XXX厂商\",\"mask\":\"255.255.255.0\",\"name\":\"停车场设备\",\"requestTag\":\"Lod3CBmY3yR1zh2dH1HW\",\"version\":\"V1.0.16_20171225001\"}]}";
        System.out.println("加密前的字串是：" + cSrc);

        // 加密
        byte[] enBytes = AESUtils.encrypt(cSrc, cKey);
        String enString = parseByte2HexStr(enBytes);
        System.out.println("加密后的字串是：" + enString);
//
//        // 解密
//        byte[] DeString = AESUtils.decrypt(enString, cKey);
////        byte[] DeString = AESUtils.decrypt("E7ABDA5C5B1271957F5CD7B038E6C01809F09209A1925E21EFCC64687441F1A68AADDEA78C394EDE3A854FB74263C71F", "M5uxt3pitktguqHJ");
//        System.out.println("解密后的字串是：" + new String(DeString, "utf-8"));

        byte[] aa = AESUtils.decrypt("8b29f3f6a01719ee6e1134a2d8c06df7da1d5066762a77a77d28e04a76a6e5145397a588d83a7f1da8e4725fe3c4b0590c280e2d67a6049e55a19323f3a60ff6b60c4581225d6bb210a04fc20f822fd158ac0b6f8b09d50b722b5eaaaa064ad845a8b2edae9659887f99c5b44a20cc6ba246a9e8ea17ff47b0501278587b6cba", cKey);
        System.out.println("解密后的字串是：" + new String(aa, "utf-8"));

/*        48 44 58 4d
          30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30
          31 30 30 31 32 30 31 37 30 32 30 30 30 30 30 30 30 30 30 30
          01
          00 00 16 34
          00 00 00 80
          00 00
          00 fb*/
    }

}
