package com.atgc.hd.client.tasklist;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.SparseArray;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.clock.InnerClock;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.GetTaskRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.TaskListResponse.TaskInfo;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.entity.EventMessage;
import com.atgc.hd.entity.NfcCard;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    private static final String GROUP_TAG = StringUtils.getRandomString(20);

    private OnCurrentTaskListener onCurrentTaskListener;
    private OnAllTaskLlistener onAllTaskListener;

    private SparseArray<TaskInfo> arrayTaskInfos;

    private TaskHandContract.IView iView;

    private Timer countDownTimer;

    private String serialNumTaskRequest;
    /***
     * 当前任务索引
     */
    private int currentTaskIndex = -1;

    public TaskHandModel(TaskHandContract.IView iView) {
        this.iView = iView;

        countDownTimer = new Timer();

        arrayTaskInfos = new SparseArray<>();

        startUploadGps();

        registerOnReceiveListener();

        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        requestTaskList();
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        SocketManager.intance().unRegistertOnActionListener(DeviceCmd.PAT_SEND_TASK);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void handleNfcTag(Tag nfcTag) {
        if (nfcTag == null) {
            return;
        }

        MifareClassic mfc = MifareClassic.get(nfcTag);

        try {
            mfc.connect();

            NfcCard nfcCard = new NfcCard();
            nfcCard.initCardData(mfc);
//            nfcCard.fastCheck(mfc);

            byte[] bytes = nfcCard.getBlockBytes(0, 0);
            byte[] target = new byte[4];
            // 服务端只返回卡号的前4个字节，客户端需要截取前4个字节再比较
            System.arraycopy(bytes, 0, target, 0, target.length);

            String cardNum = NfcCard.toHexString(target);
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
        taskRequest.deviceID = DeviceParams.getInstance().getDeviceId();

        serialNumTaskRequest = SocketManager.intance().launch(GROUP_TAG, taskRequest, null);
    }

    /**
     * 注册监听网关发送巡更报文
     */
    private void registerOnReceiveListener() {

        SocketManager.intance().registertOnActionListener(
                GROUP_TAG,
                DeviceCmd.PAT_SEND_TASK,
                TaskListResponse.class,
                new OnActionAdapter() {
                    @Override
                    public void onResponseSuccess(String cmd, String serialNum, Response response, Bundle bundle) {

                        if (!Constants.isDemo && !serialNum.equals(serialNumTaskRequest)) {
                            return;
                        }

                        List<TaskListResponse> datas = response.dataArray;
                        TaskListResponse taskListResponse = datas.get(0);

                        handTaskData(taskListResponse);

                        iView.dimssProgressDialog();
                        iView.toastMessage("数据已更新");
                    }

                    @Override
                    public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg, Bundle bundle) {

                    }
                });
    }

    private void handTaskData(TaskListResponse taskListResponse) {

        if (taskListResponse == null) {
            return;
        }

        List<TaskInfo> taskInfos = taskListResponse.data;

        // 按任务开始时间进行排序
        Collections.sort(taskInfos);

        // 任务状态为：1、2，但任务结束时间小于当前时间的为异常任务，需要上报任务结束状态
        SparseArray<TaskInfo> exceptionTaskInfos = new SparseArray<>();

        currentTaskIndex = -1;

        int undoTaskIndex = -1;

        int taskDoneNumber = 0;

        Date currentTime = InnerClock.instance().nowDate();

        for (int i = 0, count = taskInfos.size(); i < count; i++) {
            TaskInfo taskInfo = taskInfos.get(i);
            taskInfo.initTaskPeriod();
            taskInfo.initInspectStatus();
//            taskInfo.initTaskStatus(currentTime);

            arrayTaskInfos.append(i, taskInfo);

            String taskStatus = taskInfo.getTaskStatus();
            // 任务未执行
            if ("1".equals(taskStatus)) {
                if (undoTaskIndex == -1) {
                    undoTaskIndex = i;
                }

                if (taskInfo.taskEndTime().before(currentTime)) {
                    exceptionTaskInfos.append(exceptionTaskInfos.size(), taskInfo);
                    taskInfo.setTaskStatus("4");
                    undoTaskIndex = -1;
                }
            }
            // 任务正在执行
            else if ("2".equals(taskStatus)) {
                currentTaskIndex = i;

                if (taskInfo.taskEndTime().before(currentTime)) {
                    exceptionTaskInfos.append(exceptionTaskInfos.size(), taskInfo);
                    taskInfo.setTaskStatus("4");
                    currentTaskIndex = -1;
                }
            }
            // 其他状态
            // 0-未下发 3-时间范围内结束任务 4-强制结束任务 5-解除异常
            else {
                taskDoneNumber++;
            }
        }

        // 当天任务已全部巡查完毕
        if (taskDoneNumber == taskInfos.size()) {
            currentTaskDone();
        }
        // 有任务正在进行中
        else if (currentTaskIndex != -1) {
            startTask();
        }
        // 无任务进行中，也无未开始的任务
        else if (undoTaskIndex == -1) {
        }
        // 无任务进行中，但有任务未开始
        else {
            // 需将索引减1，否则会跳过一个任务
            currentTaskIndex = undoTaskIndex - 1;
            moveToNextTask();
        }

        onCurrentTaskListener.onReceiveExceptionTask(exceptionTaskInfos);

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
            onCurrentTaskListener.onReceiveCurrentTask(currentTaskInfo);

            onAllTaskListener.onReceiveAllTask(arrayTaskInfos);
            onAllTaskListener.onCurrentTask(currentTaskInfo.getTaskID());

            this.currentTaskInfo = currentTaskInfo;
        } else {
            currentTaskDone();
        }
    }

    private void currentTaskDone() {
        this.currentTaskInfo = null;
        onCurrentTaskListener.onReceiveCurrentTask(null);
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

        currentTaskDone();
        moveToNextTask();
        showTaskList();
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


    private Timer timer = new Timer();
    private TimerTask timerTask;
    private String srcLongitude = "113.62";
    private String srcLatitude = "23.30";
    private TaskInfo currentTaskInfo;
    private boolean isOK = false;

    String[] lngs = {"113.622274",
            "113.622979",
            "113.622598",
            "113.622387",
            "113.62328",
            "113.62607"};


    String[] lats = {"23.305673",
            "23.305926",
            "23.306254",
            "23.305656",
            "23.305636",
            "23.30737"};


    private void startUploadGps() {
        if (isOK) {
            return;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();

        timerTask = new TimerTask() {
            int times = 0;
            int size = lngs.length;

            @Override
            public void run() {
                times++;

                String NLongitude = lngs[times % size];
                String NLatitude = lats[times % size];
//
                uploadGps(NLongitude, NLatitude);
//                113.622282,23.306056	  113.623147,23.305834
//                times++;
//                if (times % 2 == 0) {
//                    uploadGps("113.622282", "23.306056");
//                } else {
//                    uploadGps("113.623147", "23.305834");
//                }
            }
        };

        timer.schedule(timerTask, 500, 10 * 1000);
    }

    private void uploadGps(final String longitude, final String latitude) {
        GPSRequest gpsRequest = new GPSRequest();
        gpsRequest.setDeviceID(DeviceParams.getInstance().getDeviceId());
        if (currentTaskInfo == null) {
            gpsRequest.setTaskId("");
            gpsRequest.setUserID("");
        } else {
            gpsRequest.setTaskId(currentTaskInfo.getTaskID());
            gpsRequest.setUserID(currentTaskInfo.getUserId());
        }
        gpsRequest.setLongitude(longitude);
        gpsRequest.setLatitude(latitude);

        SocketManager.intance().launch(GROUP_TAG, gpsRequest, null);
    }

    private static final String str = "0123456789";

    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}


