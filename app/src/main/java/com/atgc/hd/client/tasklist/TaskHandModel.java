package com.atgc.hd.client.tasklist;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GetTaskRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */

public class TaskHandModel implements TaskHandContract {

    private TaskListResponse taskListResponse;

    public TaskHandModel() {
    }

    @Override
    public void initData(OnInitDataListener listener) {
        registerOnReceiveListener(listener);
        requestTaskList();
    }

    @Override
    public TaskListResponse.TaskInfo getCurrentTask() {
        //TODO 需要计算找出当前任务返回
        return taskListResponse.getTaskArray().get(0);
    }

    @Override
    public List<TaskListResponse.TaskInfo> getAllTask() {
        return taskListResponse.getTaskArray();
    }

    /**
     * 请求网关发送巡更报文
     */
    private void requestTaskList() {
        GetTaskRequest taskRequest = new GetTaskRequest();
        String deviceID = "10012017f6d0101be5ed";
        taskRequest.setDeviceID(deviceID);
        taskRequest.send(new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                Logger.e("巡更--发送成功");
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    /**
     * 注册监听网关发送巡更报文
     *
     * @param onInitDataListener
     */
    private void registerOnReceiveListener(final OnInitDataListener onInitDataListener) {
        TcpSocketClient.getInstance().registerOnReceiveListener(new TcpSocketClient.OnReceiveListener() {
            @Override
            public void onReceive(String cmd, String[] jsonData) {
                Logger.json("巡更任务", jsonData[0]);
                // 注销监听
                TcpSocketClient.getInstance().unregisterOnReceiveListener(DeviceCmd.PAT_SEND_TASK);

                taskListResponse = JSON.parseObject(jsonData[0], TaskListResponse.class);

                if (onInitDataListener != null) {
                    onInitDataListener.onInitData(true);
                }
            }
        }, DeviceCmd.PAT_SEND_TASK);
    }

}
