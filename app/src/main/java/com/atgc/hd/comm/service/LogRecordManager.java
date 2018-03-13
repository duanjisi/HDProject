package com.atgc.hd.comm.service;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.entity.EventMessage;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：记录日志管理
 * <p>作者：liangguokui 2018/3/13
 */
public class LogRecordManager {

    private StringBuilder logBuilder;

    private boolean isGPSLogOff = true;
    private boolean isLogOff = false;

    public LogRecordManager() {
        logBuilder = new StringBuilder();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void receiveSwitchStatus(EventMessage message) {
        if (message.checkTag("receive_switch_status")) {
            String[] temp = ((String) message.object).split("##");
            isLogOff = "1".equals(temp[0]);
            isGPSLogOff = "1".equals(temp[1]);

            if ("0".equals(temp[2])) {
                logBuilder = new StringBuilder();
            }
        }
    }

    @Subscribe
    public void receiveLog(EventMessage message) {
        if (isLogOff) {
            return;
        }

        if (message.checkTag("socket_log")) {
            String time = DateUtil.currentTime("HH:mm:ss");

            String[] temp = ((String) message.object).split("##");

            // 过滤心跳日志
            if (DeviceCmd.HEART_BEAT.equals(temp[0])) {
            }
            // 过滤GPS日志
            else if (DeviceCmd.PAT_UPLOAD_GPS.equals(temp[0])) {
                if (isGPSLogOff) {
                } else {
                    showLog(time + "【" + temp[1] + "-" + temp[0] + "】：" + temp[2]);
                }
            } else {
                showLog(time + "【" + temp[1] + "-" + temp[0] + "】：" + temp[2]);
            }
        }
    }

    private void showLog(final String log) {

        logBuilder.insert(0, log);
        logBuilder.insert(0, "\n*******************\n");

        EventBus.getDefault().post(new EventMessage("show_net_log", logBuilder.toString()));
    }

    public void onDestory() {
        EventBus.getDefault().unregister(this);
    }
}
