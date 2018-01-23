package com.atgc.hd.comm;

/**
 * <p>描述：请求命令
 * <p>作者：duanjisi 2018年 01月 15日
 */
public class DeviceCmd {
    public static final String REGISTER = "COM_DEV_REGISTER";
    public static final String HEART_BEAT = "COM_HEARTBEAT";
    // 上报应急事件
    public static final String UP_LOAD_EMERGENCY = "PAT_UPLOAD_EMERGENCY";
    // GPS上报事件
    public static final String PAT_UPLOAD_GPS = "PAT_UPLOAD_GPS";
    public static final String PAT_UPLOAD_EMERGENCY = "PAT_UPLOAD_EMERGENCY";
    // 请求获取巡更任务
    public static final String PAT_GET_TASK_REQUEST = "PAT_GET_TASK_REQUEST";
}
