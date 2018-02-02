/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm;

/**
 * <p>描述：常量类
 * <p>作者：duanjisi 2018年 01月 16日
 */

public class Constants {
    //    public static final String UP_LOAD_IMAGE = "http://ip:port/VIID/Images";
    public static final String IMAGE_HEADER = "http://192.168.0.246:8888/";
    public static final String UP_LOAD_IMAGE = "http://192.168.0.242:39047/VIID/Images";
//    public static final String UP_LOAD_IMAGE = "http://172.16.10.115:8090/scp-imagemgmtcomponent/VIID/Images";

    /**
     * demo模式，表示数据会从/src/assets文件夹读取
     */
    public static final boolean isDemo = false;
    /**
     * 行为
     */
    public static final class Action {
        public static final String REGISTER_SUCCESSED = "com.atgc.hd.REGISTER_SUCCESSED";
        public static final String HEART_BEAT = "com.atgc.hd.HEART_BEAT";
        public static final String CONNECT_BREAK = "com.atgc.hd.CONNECT_BREAK";
        public static final String CONNECT_FALIED = "com.atgc.hd.CONNECT_FALIED";
        public static final String EXIT_ACTIVITY = "com.atgc.hd.EXIT_ACTIVITY";
    }
}
