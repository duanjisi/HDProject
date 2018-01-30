package com.atgc.hd.client.tasklist.patrolfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.comm.clock.InnerClock;
import com.atgc.hd.comm.local.Coordinate;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.local.LocationService.ILocationListener;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.request.ReportPointStatusRequest;
import com.atgc.hd.comm.net.request.ReportTaskStatusRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.TaskListResponse.PointInfo;
import com.atgc.hd.comm.utils.CoordinateUtil;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>描述：巡更任务presenter层
 * <p>作者：liangguokui 2018/1/23
 */
public class PatrolPresenter implements PatrolContract.IPresenterView, PatrolContract.IPresenterModel, TaskHandContract.OnCurrentTaskListener {
    /**
     * 打点范围，单位米
     */
    private static final double POINT_RANGE = 20;

    private PatrolContract.IView iView;
    private PatrolContract.IModel iModel;
    private TaskInfoProxy taskInfoProxy;

    private TaskListResponse.PointInfo currentPointInfo = null;

    private PatrolContract.OnTaskActionListener onTaskActionListener;

    private Timer countdownTimer;
    private TimerTask countdownTimerTask;

    private ILocationListener locationListener = new ILocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Coordinate currentLocation = CoordinateUtil.gcj02ToWbs84(bdLocation.getLongitude(), bdLocation.getLatitude());
            double distance = CoordinateUtil.getDistance(
                    currentPointInfo.getLatitude(),
                    currentPointInfo.getLongitude(),
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );

            Logger.e(currentPointInfo.getLatitude() + "\n" +
                    currentPointInfo.getLongitude() + "\n" +
                    currentLocation.getLatitude() + "\n" +
                    currentLocation.getLongitude());
            iView.showTips(currentPointInfo.getPointName() + "\n" + bdLocation.getLongitude() + "\n" + bdLocation.getLatitude() + "\n" + (float) distance + "米");

            if (distance < POINT_RANGE) {

                countdownTimerTask.cancel();

                taskInfoProxy.checkPoint();
                reportPointStatus(taskInfoProxy.getLastPointInfo());

                if (taskInfoProxy.hasNextPoint()) {
                    taskInfoProxy.moveToNextPoint();
                } else {
                    // 最后一个点，上报巡查任务状态
                    iView.showFillReasonDialog("2", "");
                }
            }
        }
    };

    public PatrolPresenter(PatrolContract.IView iView) {
        this.iView = iView;
        iModel = new PatrolModel(this);
        countdownTimer = new Timer();

        iView.registerOnCurrentTaskListener(this);
    }

    @Override
    public void onDestory() {
        LocationService.intance().unregisterLocationListener(locationListener);
    }

    /**
     * TaskHandModel数据准备完毕后，将会回调该方法
     *
     * @param taskInfo
     */
    @Override
    public void onReceiveCurrentTask(TaskListResponse.TaskInfo taskInfo) {
        // 暂无任务
        if (taskInfo == null) {
            iView.refreshTaskList(null);
            return;
        }

        // TODO 开启点超时倒计时，超时需要上报一次状态

        taskInfoProxy = new TaskInfoProxy(taskInfo);

        // 找到当前应该进行打点的任务
        currentPointInfo = taskInfoProxy.getCurrentPointInfo();
        // 若当前任务点为第一个点，则应进行一次任务状态上报
        if (currentPointInfo.getOrderNo() == 0) {
            reportTaskStatus("1", "0", "");
        }

        iView.refreshTaskList(taskInfoProxy.getPointInfos());

        startPointTimeOutCountDown(taskInfoProxy.getCurrentPointInfo());

        // 开始进行GPS定位监听
        LocationService.intance().registerLocationListener(locationListener);
    }

    @Override
    public TaskListResponse.TaskInfo stopTask() {

        LocationService.intance().unregisterLocationListener(locationListener);

        return taskInfoProxy.getTaskInfo();
    }

    /**
     * 用于巡更点超时上报
     * @param pointInfo
     */
    private void startPointTimeOutCountDown(final PointInfo pointInfo) {
        Date nowTime = InnerClock.instance().getInnerClockDate();

        final Date pointTimeLine = taskInfoProxy.getPointTimeLine();

        long delay = pointTimeLine.getTime() - nowTime.getTime();

        if (delay < 0) {
            return;
        }

        countdownTimerTask = new TimerTask() {
            @Override
            public void run() {
                // 上报巡查点超时未巡查
                pointInfo.setCheckedTime(pointTimeLine.toString());
                pointInfo.setResultType("2");
                reportPointStatus(pointInfo);
            }
        };

        countdownTimer.schedule(countdownTimerTask, delay);
    }

    /**
     * 上报巡查点结果
     */
    private void reportPointStatus(PointInfo pointInfo) {
//        PointInfo checkedPointInfo = taskInfoProxy.getLastPointInfo();

        ReportPointStatusRequest request = new ReportPointStatusRequest();
        request.setDeviceID("10012017020000000000");

        request.setTaskID(taskInfoProxy.getTaskInfo().getTaskID());
        request.setTaskPointID(pointInfo.getTaskPointId());
        request.setPointTime(pointInfo.getCheckedTime());
        request.setHistoryPointStatus(pointInfo.getResultType());
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                Logger.e("上报巡查点成功");
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    // TaskHandModel会注册此监听
    @Override
    public void registerTaskFinishListener(PatrolContract.OnTaskActionListener onTaskActionListener) {
        this.onTaskActionListener = onTaskActionListener;
    }

    /**
     * @param taskStatus
     * @param carryStatus
     * @param reason
     */
    @Override
    public void reportTaskStatus(String taskStatus, String carryStatus, String reason) {
        ReportTaskStatusRequest request = new ReportTaskStatusRequest();
        request.setDeviceID("10012017020000000000");

        request.setTaskID(taskInfoProxy.getTaskInfo().getTaskID());

        request.setTaskStatus(taskStatus);

        request.setCarryStatus(carryStatus);

        request.setAbnormalReason(reason);

        request.send(new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
