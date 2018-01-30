package com.atgc.hd.client.tasklist;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.client.tasklist.patrolfrag.PatrolContract;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GetTaskRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.utils.DateUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */

public class TaskHandModel implements TaskHandContract, PatrolContract.OnTaskActionListener {

    private OnCurrentTaskListener onCurrentTaskListener;
    private OnAllTaskLlistener onAllTaskLlistener;

    private Map<String, TaskListResponse.TaskInfo> mapTaskInfos;

    private Map<String, TimerTask> mapStopTimerTask;

    public TaskHandModel() {
        mapTaskInfos = new HashMap<>();
        mapStopTimerTask = new HashMap<>();
    }

    @Override
    public void initData() {
        registerOnReceiveListener();
        requestTaskList();
    }

    @Override
    public void registerOnCurrentTaskListener(OnCurrentTaskListener listener) {
        this.onCurrentTaskListener = listener;
    }

    @Override
    public void registerOnAllTaskListener(OnAllTaskLlistener listener) {
        this.onAllTaskLlistener = listener;
    }


    /**
     * 请求网关发送巡更报文
     */
    private void requestTaskList() {
        GetTaskRequest taskRequest = new GetTaskRequest();
        String deviceID = "10012017020000000000";
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
     */
    private void registerOnReceiveListener() {
        TcpSocketClient.getInstance().registerOnReceiveListener(new TcpSocketClient.OnReceiveListener() {
            @Override
            public void onReceive(String cmd, String[] jsonData) {
                Logger.json("巡更任务", jsonData[0]);
                // 注销监听
                TcpSocketClient.getInstance().unregisterOnReceiveListener(DeviceCmd.PAT_SEND_TASK);

                TaskListResponse taskListResponse = JSON.parseObject(jsonData[0], TaskListResponse.class);

                handTaskData(taskListResponse);

            }
        }, DeviceCmd.PAT_SEND_TASK);
    }

    public void handTaskData(TaskListResponse taskListResponse) {

        TaskListResponse.TaskInfo currentTaskInfo = null;

        if (taskListResponse == null) {
            return;
        }

        // 按任务开始时间进行排序
        Collections.sort(taskListResponse.getTaskArray());

        Date currentTime = currentTime();
        int position = 0;
        for (TaskListResponse.TaskInfo taskInfo : taskListResponse.getTaskArray()) {
            mapTaskInfos.put(taskInfo.getTaskID(), taskInfo);

            taskInfo.initTaskPeriod();

            String taskStatus = taskInfo.taskStatus(currentTime);
            // 任务未开始
            if (TaskListResponse.TaskInfo.STATUS_UNDO.equals(taskStatus)) {
                // TODO 计时
                startTaskCountDown(currentTime, taskInfo);
                stopTaskCountDown(currentTime, taskInfo);
            }
            // 任务进行中
            else if (TaskListResponse.TaskInfo.STATUS_DOING.equals(taskStatus)) {
                currentTaskInfo = taskInfo;
                stopTaskCountDown(currentTime, taskInfo);
            }
            // 任务已过最后时间
            else {
            }

            position++;
        }

        onCurrentTaskListener.onReceiveCurrentTask(currentTaskInfo);
        onAllTaskLlistener.onReceiveAllTask(taskListResponse.getTaskArray());
        onAllTaskLlistener.onCurrentTask(currentTaskInfo.getTaskID());

    }

    private Timer countDownTimer = new Timer();

    /**
     * 任务开始倒计时
     *
     * @param lineDate yyyy-MM-dd HH:mm:ss
     * @param taskInfo
     */
    private void startTaskCountDown(Date lineDate,
                                    final TaskListResponse.TaskInfo taskInfo) {

        Date date = DateUtil.dateParse(taskInfo.getStartTime(), DateUtil.DATE_TIME_PATTERN);

        long delay = date.getTime() - lineDate.getTime();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                onCurrentTaskListener.onReceiveCurrentTask(taskInfo);
                onAllTaskLlistener.onCurrentTask(taskInfo.getTaskID());
            }
        };

        countDownTimer.schedule(timerTask, delay);
    }

    /**
     * 任务结束倒计时，任务结束更新界面
     *
     * @param lineDate
     * @param taskInfo
     */
    private void stopTaskCountDown(Date lineDate,
                                   final TaskListResponse.TaskInfo taskInfo) {
        Date date = DateUtil.dateParse(taskInfo.getEndTime(), DateUtil.DATE_TIME_PATTERN);

        long delay = date.getTime() - lineDate.getTime();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                actionStopTask(taskInfo.getTaskID());
            }
        };

        countDownTimer.schedule(timerTask, delay);

        mapStopTimerTask.put(taskInfo.getTaskID(), timerTask);
    }

    private void actionStopTask(String taskId) {
        // 巡更任务时间已到，上交巡更任务状态数据
        TaskListResponse.TaskInfo finalTask = onCurrentTaskListener.stopTask();
        mapTaskInfos.put(finalTask.getTaskID(), finalTask);

        //
        List<TaskListResponse.TaskInfo> newTaskInfos = new ArrayList<>(mapTaskInfos.values());
        Collections.sort(newTaskInfos);
        onAllTaskLlistener.onReceiveAllTask(newTaskInfos);
        onAllTaskLlistener.onCurrentTask("");

        TimerTask timerTask = mapStopTimerTask.get(taskId);
        timerTask.cancel();
        mapStopTimerTask.remove(taskId);
    }

    private Date currentTime() {

//        String currentTime = DateUtil.currentTime();
        String currentTime = "2018-01-27 09:00:05";
        Date currentDate = DateUtil.dateParse(currentTime, DateUtil.HOUR_PATTERN);
        return currentDate;
    }

    // 当PatrolPresenter的所有点都巡查完毕，将会回调此方法
    @Override
    public void onTaskFinish(String taskId) {
        actionStopTask(taskId);
    }
}
