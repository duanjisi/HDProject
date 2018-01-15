package com.atgc.hd.comm;

import com.atgc.hd.comm.utils.DateUtils;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.util.Date;

import io.netty.buffer.ByteBuf;

/**
 * Created by duanjisi on 2018/1/12.
 */

public class ProtocolDecoder {
    /**
     * byte数组转成字符串
     *
     * @param buffer
     * @param length
     * @return String
     * @Methods Name parseString
     * @Create In 2017年12月20日 By zhangweixian
     */
    public static String parseString(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        String str = null;
        try {
            str = new String(bytes, "utf-8");
        } catch (Exception e) {
            Logger.d("时间为" + DateUtils.getDate(new Date()) + "解码器转换字符串发生异常:" + e.getMessage());
            return null;
        }
        return str;
    }


    /**
     * byte数组转成10进制数字字符串
     *
     * @param buffer
     * @param length
     * @return String
     * @Methods Name parseNumber
     * @Create In 2017年12月20日 By zhangweixian
     */
    public static String parseNumber(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        String result = null;
        try {
            result = StringUtils.binary(bytes, 10);
        } catch (Exception e) {
            Logger.d("时间为" + DateUtils.getDate(new Date()) + "解码器转换数字异常:" + e.getMessage());
            return null;
        }
        return result;
    }
}
