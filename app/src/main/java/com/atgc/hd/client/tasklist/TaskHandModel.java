package com.atgc.hd.client.tasklist;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.clock.InnerClock;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.GetTaskRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.TaskListResponse.TaskInfo;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.entity.EventMessage;
import com.atgc.hd.entity.NfcCard;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */

public class TaskHandModel implements TaskHandContract {

    private OnCurrentTaskListener onCurrentTaskListener;
    private OnAllTaskLlistener onAllTaskListener;

    private SparseArray<TaskInfo> arrayTaskInfos;

    private TaskHandContract.IView iView;

    private Timer countDownTimer;

    /***
     * 当前任务索引
     */
    private int currentTaskIndex = -1;

    public TaskHandModel(TaskHandContract.IView iView) {
        this.iView = iView;

        countDownTimer = new Timer();

        arrayTaskInfos = new SparseArray<>();

        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        registerOnReceiveListener();
        requestTaskList();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void handleNfcTag(Tag nfcTag) {
        MifareClassic mfc = MifareClassic.get(nfcTag);
        try {
            mfc.connect();

            NfcCard nfcCard = new NfcCard();
            nfcCard.initCardData(mfc);
//            nfcCard.fastCheck(mfc);

            byte[] bytes = nfcCard.getBlockBytes(0, 0);

            String cardNum = NfcCard.toHexString(bytes);
            Logger.e("卡号：" + cardNum);

            onCurrentTaskListener.onReceiveNfcCardNum(cardNum);
        } catch (IOException e) {
            Logger.e(e, "mfc卡连接异常");
        }
    }

    public void demoNfcCardNum(String cardNum) {
        Logger.e("卡号：" + cardNum);
        onCurrentTaskListener.onReceiveNfcCardNum(cardNum);
    }

    /**
     * 请求网关发送巡更报文
     */
    private void requestTaskList() {
        GetTaskRequest taskRequest = new GetTaskRequest();
        String deviceID = DeviceParams.getInstance().getDeviceId();
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

                iView.dimssProgressDialog();

            }
        }, DeviceCmd.PAT_SEND_TASK);
    }

    private void handTaskData(TaskListResponse taskListResponse) {

        if (taskListResponse == null) {
            return;
        }

        // 按任务开始时间进行排序
        Collections.sort(taskListResponse.getTaskArray());

        currentTaskIndex = -1;
        boolean taskOnGoing = false;
        Date currentTime = currentTime();

        for (int i = 0, count = taskListResponse.getTaskArray().size(); i < count; i++) {
            TaskInfo taskInfo = taskListResponse.getTaskArray().get(i);
            taskInfo.initTaskPeriod();
            taskInfo.initInspectStatus();
            taskInfo.initTaskStatus(currentTime);

            arrayTaskInfos.append(i, taskInfo);

            String taskStatus = taskInfo.getTaskStatus();
            // 任务未开始
            if (TaskInfo.STATUS_UNDO.equals(taskStatus)) {
                if (currentTaskIndex == -1) {
                    currentTaskIndex = i;
                }
            }
            // 任务进行中
            else if (TaskInfo.STATUS_DOING.equals(taskStatus)) {
                currentTaskIndex = i;
                taskOnGoing = true;
            }
            // 任务已过最后时间
            else {
            }
        }

        // 当天任务已全部巡查完毕
        if (currentTaskIndex == -1) {
            noTaskOnGoing();
        }
        // 有任务正在进行中
        else if (taskOnGoing) {
            startTask();
        }
        // 无任务进行中，但有任务未开始
        else {
            // 需将索引减1，否则会跳过一个任务
            currentTaskIndex--;
            moveToNextTask();
        }

        showTaskList();
    }

    /**
     * 移动到下一任务
     */
    private void moveToNextTask() {
        currentTaskIndex++;

        TaskInfo currentTaskInfo = arrayTaskInfos.get(currentTaskIndex);
        Date taskPlanTime = DateUtil.dateParse(currentTaskInfo.getStartTime());
        Date date = currentTime();
        long delay = taskPlanTime.getTime() - date.getTime();

        StringBuilder builder = new StringBuilder();
        builder.append("planTime：" + currentTaskInfo.getStartTime());
        builder.append("\ncurrentTime：" + date.toString());
        builder.append("\ndelay：" + delay);

        Logger.e(builder.toString());

        if (delay <= 0) {
            startTask();
        } else {
            prepareStartTask(delay);
        }
    }


    /**
     * 准备开始任务
     *
     * @param delay yyyy-MM-dd HH:mm:ss
     */
    private void prepareStartTask(long delay) {
        if (delay < 0) {
            Logger.e("delay < 0, 任务倒计时未开始");
            return;
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startTask();
            }
        };

        countDownTimer.schedule(timerTask, delay);
    }

    /**
     * 开始任务
     */
    private void startTask() {
        if (-1 < currentTaskIndex && currentTaskIndex < arrayTaskInfos.size()) {
            TaskInfo currentTaskInfo = arrayTaskInfos.get(currentTaskIndex);
            currentTaskInfo.setTaskStatus(TaskInfo.STATUS_DOING);
            onCurrentTaskListener.onReceiveTask(currentTaskInfo);

            onAllTaskListener.onReceiveAllTask(arrayTaskInfos);
            onAllTaskListener.onCurrentTask(currentTaskInfo.getTaskID());

        } else {
            noTaskOnGoing();
        }
    }

    private void noTaskOnGoing() {
        onCurrentTaskListener.onReceiveTask(null);
        onAllTaskListener.onCurrentTask("");
    }

    private void showTaskList() {
        onAllTaskListener.onReceiveAllTask(arrayTaskInfos);
    }

    private void actionStopTask() {
        // 巡更任务时间已到，上交巡更任务状态数据
        TaskInfo newTaskInfo = onCurrentTaskListener.stopTask();
        newTaskInfo.initInspectStatus();
        newTaskInfo.setTaskStatus(TaskInfo.STATUS_DONE);

        arrayTaskInfos.setValueAt(currentTaskIndex, newTaskInfo);

        noTaskOnGoing();
        showTaskList();
        moveToNextTask();
    }

    private Date currentTime() {
        return InnerClock.instance().nowDate();
    }

    // 当PatrolPresenter的所有点都巡查完毕，将会回调此方法
    @Subscribe
    public void onTaskFinish(EventMessage eventMessage) {
        if ("on_task_finish".equals(eventMessage.eventTag)) {
            actionStopTask();
        }
    }

    @Subscribe
    public void registerListener(EventMessage msg) {
        // PatrolPresenter注册的监听器
        if ("on_current_task_listener".equals(msg.eventTag)) {
            onCurrentTaskListener = (OnCurrentTaskListener) msg.object;
        }
        // TaskListPresenter注册的监听器
        else if ("on_all_task_listener".equals(msg.eventTag)) {
            onAllTaskListener = (OnAllTaskLlistener) msg.object;
        }
    }

}
