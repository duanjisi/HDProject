package com.atgc.hd.client.tasklist.patrolfrag;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.clock.InnerClock;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.local.Coordinate;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.local.LocationService.ILocationListener;
import com.atgc.hd.comm.net.request.ReportPointStatusRequest;
import com.atgc.hd.comm.net.request.ReportTaskStatusRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.TaskListResponse.PointInfo;
import com.atgc.hd.comm.net.response.base.BaseResponse;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.CoordinateUtil;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.entity.EventMessage;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：巡更任务presenter层
 * <p>作者：liangguokui 2018/1/23
 */
public class PatrolPresenter implements PatrolContract.IPresenterView, PatrolContract.IPresenterModel, TaskHandContract.OnCurrentTaskListener {
    /**
     * 打点范围，单位米
     */
    private static final double POINT_RANGE = 20;
    private static final String GROUP_TAG = StringUtils.getRandomString(20);

    private PatrolContract.IView iView;
    private PatrolContract.IModel iModel;
    private TaskInfoProxy taskInfoProxy;

    private Timer countdownTimer;
    /**
     * 用于巡更任务超时结束任务并上报任务状态
     */
    private TimerTask taskTimeOutTimerTask;
    /**
     * 用于打点超时上报巡查点状态
     */
    private TimerTask pointTimeOutTimerTask;

    private ILocationListener locationListener;

    public PatrolPresenter(PatrolContract.IView iView) {
        this.iView = iView;
        iModel = new PatrolModel(this);
        countdownTimer = new Timer();

        SocketManager.intance().registertOnActionListener(
                GROUP_TAG,
                DeviceCmd.PAT_TASK_STATUS,
                BaseResponse.class,
                onActionAdapter);

        SocketManager.intance().registertOnActionListener(
                GROUP_TAG,
                DeviceCmd.PAT_POINT_RESULT,
                BaseResponse.class,
                onActionAdapter);

        // 在TaskHandModel注册监听
        sendEventMessage("on_current_task_listener", this);
    }

    @Override
    public void onDestory() {
        SocketManager.intance().unRegistertOnActionListener(DeviceCmd.PAT_POINT_RESULT);
        SocketManager.intance().unRegistertOnActionListener(DeviceCmd.PAT_TASK_STATUS);

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

        taskInfo.initInspectStatus();
        taskInfoProxy = new TaskInfoProxy(taskInfo);

        // 若当前巡查点为第一个点且该任务为未执行状态，则应进行一次任务状态上报
        if (taskInfo.shouldReportTaskStatus()) {
            taskInfo.setTaskStatus("2");
            reportTaskStatus("2", "0", "", null);
        }

        // 初始化数据，将TaskInfo/PointInfo转为adapter使用的TaskListEntity
        List<TaskListEntity> entities = taskInfoProxy.adapterEntities();
        iView.refreshTaskList(entities);

        // 开启任务强制结束倒计时，超时需上报任务状态
        startTaskTimeOutCountDown();
        // 开启巡查点超时倒计时，超时需要上报打点超时状态
        startPointTimeOutCountDown();

        // TODO 开始进行GPS定位监听
//        locationListener = getLocationListener(currentPointInfo);
//        LocationService.intance().registerLocationListener(locationListener);
    }

    @Override
    public void onReceiveExceptionTask(SparseArray<TaskListResponse.TaskInfo> exceptionTasks) {
//        for (int i = 0; i < exceptionTasks.size(); i++) {
//            TaskListResponse.TaskInfo exceptionTask = exceptionTasks.get(i);
//            reportTaskStatus(
//                    exceptionTask.getUserId(),
//                    exceptionTask.getTaskID(),
//                    "4",
//                    "1",
//                    "其他",
//                    null);
//        }
    }

    @Override
    public void onReceiveNfcCardNum(String cardNum) {
        if (taskInfoProxy == null) {
            return;
        }
        if ("636B3EA4".equals(cardNum)) {
            checkPoint();
            return;
        }
        if (cardNum.equals(taskInfoProxy.getCurrentPointInfo().getCardNumber())) {
            checkPoint();
        }
    }

    @Override
    public TaskListResponse.TaskInfo stopTask() {

        LocationService.intance().unregisterLocationListener(locationListener);

        return taskInfoProxy.getTaskInfo();
    }

    private ILocationListener getLocationListener(final PointInfo currentPointInfo) {
        ILocationListener listener = new ILocationListener() {
            Object lock = new Object();

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                synchronized (lock) {
                    actionReceive(bdLocation);
                }
            }

            private void actionReceive(BDLocation bdLocation) {
                Coordinate currentLocation = CoordinateUtil.gcj02ToWbs84(bdLocation.getLongitude(), bdLocation.getLatitude());
                double distance = CoordinateUtil.getDistance(
                        currentPointInfo.getLatitude(),
                        currentPointInfo.getLongitude(),
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                );

//            Logger.e(currentPointInfo.getLatitude() + "\n" +
//                    currentPointInfo.getLongitude() + "\n" +
//                    currentLocation.getLatitude() + "\n" +
//                    currentLocation.getLongitude());

                if (distance > POINT_RANGE) {
                    return;
                }

                checkPoint();
            }
        };

        return listener;
    }

    private void checkPoint() {
        // 取消打点超时的倒计时
        if (pointTimeOutTimerTask != null) {
            pointTimeOutTimerTask.cancel();
        }
        // 进行打点
        taskInfoProxy.checkPoint();
        // 上报巡查点状态
        reportPointStatus(taskInfoProxy.getCurrentPointInfo());

        // 有还有巡查点未巡查
        if (taskInfoProxy.hasNextPoint()) {
            // 将当前点移动到下一个点
            taskInfoProxy.moveToNextPoint();
            // 对下一个点进行倒计时
            startPointTimeOutCountDown();

            // 刷新界面
            // TODO 待优化，理想情况是只对指定点进行更新，现在是全部数据进行更新一次
            List<TaskListEntity> entities = taskInfoProxy.adapterEntities();
            iView.refreshTaskList(entities);
        }
        // 当前点是最后一个巡查点
        else {
            // 刷新界面
            // TODO 待优化，理想情况是只对指定点进行更新，现在是全部数据进行更新一次
            List<TaskListEntity> entities = taskInfoProxy.adapterEntities();
            iView.refreshTaskList(entities);

            String taskStatus = taskInfoProxy.getTaskStatus();
            String carryStatus = taskInfoProxy.getCarryStatus();
            // 若巡查任务正常结束且没有异常点，则直接上报任务状态
            if ("3".equals(taskStatus) && "0".equals(carryStatus)) {
                reportTaskStatus("3", "0", "", null);

                // TaskHandModel.onTaskFinish()接收
                Log.e("finish_tag", "218行结束任务");
                sendEventMessage("on_task_finish");
            }
            // 否则弹出提示框填写异常原因，再上报任务状态
            else {
                iView.showFillReasonDialog(taskStatus, carryStatus);
            }
        }
    }

    private void checkPointTimeOut() {
        Logger.e("打点超时，开始上报巡查点超时未打点");
        // 上报巡查点超时未巡查
        taskInfoProxy.checkPointTimeOut();
        iView.refreshTaskList(taskInfoProxy.adapterEntities());
        reportPointStatus(taskInfoProxy.getCurrentPointInfo());
    }

    /**
     * 用于任务超时强制结束
     */
    private void startTaskTimeOutCountDown() {
        Date nowTime = InnerClock.instance().nowDate();
        final Date taskPlanTime = DateUtil.dateParse(taskInfoProxy.getTaskInfo().getEndTime());
        long delay = taskPlanTime.getTime() - nowTime.getTime();
        if (delay < 0) {
            Logger.e("立即结束任务：" + delay);
            reportTaskStatus("4", "1", "其他", null);
            return;
        }

        taskTimeOutTimerTask = new TimerTask() {
            @Override
            public void run() {
                Logger.e("倒计时结束，任务超时上报任务状态");
                reportTaskStatus("4", "1", "其他", null);
            }
        };
        Logger.e((delay / 1000) + "秒后结束任务");
        countdownTimer.schedule(taskTimeOutTimerTask, delay);
    }

    /**
     * 用于巡查点超时上报
     */
    private void startPointTimeOutCountDown() {
        Date nowTime = InnerClock.instance().nowDate();

        final Date pointPlanTime = taskInfoProxy.getCurrentPointPlanTime();

        long delay = pointPlanTime.getTime() - nowTime.getTime();

        // 当前巡查点已超时，则标记超时巡查点并刷新界面
        if (delay < 0) {
            taskInfoProxy.checkPointTimeOut();
            reportPointStatus(taskInfoProxy.getCurrentPointInfo());
            iView.refreshTaskList(taskInfoProxy.adapterEntities());
        }
        // 未超时，开启timeoutTimerTask
        else {
            pointTimeOutTimerTask = new TimerTask() {
                @Override
                public void run() {
                    checkPointTimeOut();
                }
            };

            countdownTimer.schedule(pointTimeOutTimerTask, delay);
        }
    }

    /**
     * 上报巡查点状态
     */
    private void reportPointStatus(PointInfo pointInfo) {
        ReportPointStatusRequest request = new ReportPointStatusRequest();

        request.setDeviceID(DeviceParams.getInstance().getDeviceId());
        request.setTaskID(taskInfoProxy.getTaskInfo().getTaskID());
        request.setTaskPointID(pointInfo.getTaskPointId());
        request.setPointTime(pointInfo.getPointTime());
        request.setPlanTime(pointInfo.getPlanTime());
        request.setHistoryPointStatus(pointInfo.getResultType());

        SocketManager.intance().launch(GROUP_TAG, request, null);
    }

    /**
     * 上报巡查任务状态
     *
     * @param taskStatus  1-未执行 2-正在执行 3-时间范围内结束任务 4-强制结束任务
     * @param carryStatus
     * @param reason
     */
    @Override
    public void reportTaskStatus(final String taskStatus,
                                 final String carryStatus,
                                 final String reason,
                                 final PatrolContract.OnReportTaskListener listener) {
        Logger.w("上报任务（initTaskStatus:" + taskStatus + " carryStatus:" + carryStatus + "）");
        reportTaskStatus(
                taskInfoProxy.getTaskInfo().getUserId(),
                taskInfoProxy.getTaskInfo().getTaskID(),
                taskStatus,
                carryStatus,
                reason,
                listener
        );
    }

    public String reportTaskStatus(final String userId,
                                   final String taskId,
                                   final String taskStatus,
                                   final String carryStatus,
                                   final String reason,
                                   final PatrolContract.OnReportTaskListener listener) {
        ReportTaskStatusRequest request = new ReportTaskStatusRequest();

        request.setDeviceID(DeviceParams.getInstance().getDeviceId());
        request.setUserId(userId);
        request.setTaskID(taskId);
        request.setTaskStatus(taskStatus);
        request.setCarryStatus(carryStatus);
        request.setAbnormalReason(reason);

        Bundle bundle = new Bundle();
        bundle.putString("taskStatus", taskStatus);
        bundle.putString("carryStatus", carryStatus);
        bundle.putSerializable("listener", listener);

        return SocketManager.intance().launch(GROUP_TAG, request, bundle);
    }

    private OnActionAdapter onActionAdapter = new OnActionAdapter() {
        @Override
        public void onSendSucess(String cmd, String serialNum, Bundle bundle) {
            if (DeviceCmd.PAT_TASK_STATUS.equals(cmd)) {
                dealReportTaskStatusSuccess(serialNum, bundle);
            } else if (DeviceCmd.PAT_POINT_RESULT.equals(cmd)) {
                Logger.e("上报巡查点状态成功");
            }
        }

        @Override
        public void onSendFail(String cmd, String serialNum, String errorCode, String errorMsg, Bundle bundle) {
            if (DeviceCmd.PAT_TASK_STATUS.equals(cmd)) {
                dealReportTaskStatusFail(serialNum, errorCode, errorMsg, bundle);
            } else if (DeviceCmd.PAT_POINT_RESULT.equals(cmd)) {
                Logger.e("上报巡查点状态失败");
            }
        }

        private void dealReportTaskStatusSuccess(String serialNum, Bundle bundle) {
            String taskStatus = bundle.getString("taskStatus");
            String carryStatus = bundle.getString("taskStatus");
            PatrolContract.OnReportTaskListener listener
                    = (PatrolContract.OnReportTaskListener) bundle.getSerializable("listener");

            Logger.e("上报任务状态成功（initTaskStatus:" + taskStatus + " carryStatus:" + carryStatus + "）");
            if (listener != null) {
                listener.onReportSuccess();
            }

            // taskInfoProxy == null，说明是上报异常任务状态的，不需继续执行下面的逻辑
            if (taskInfoProxy == null) {
                return;
            }

            // 3:时间范围内结束任务
            if ("3".equals(taskStatus) && "1".equals(carryStatus)) {
                // 通知TaskHandModel结束当前任务，TaskHandModel.onTaskFinish()接收
                Log.e("finish_tag", "373行结束任务");
                sendEventMessage("on_task_finish");
            }

            // 4：强制结束任务
            if ("4".equals(taskStatus)) {
                // 通知TaskHandModel结束当前任务，TaskHandModel.onTaskFinish()接收
                Log.e("finish_tag", "379行结束任务");
                sendEventMessage("on_task_finish");
            }
        }

        private void dealReportTaskStatusFail(String serialNum, String errorCode, String errorMsg, Bundle bundle) {
            String taskStatus = bundle.getString("taskStatus");
            String carryStatus = bundle.getString("carryStatus");
            PatrolContract.OnReportTaskListener listener
                    = (PatrolContract.OnReportTaskListener) bundle.getSerializable("listener");

            Logger.e("上报任务状态失败（initTaskStatus:" + taskStatus + " carryStatus:" + carryStatus + "）");

            if (listener != null) {
                listener.onReportFail(errorMsg);
            }
        }
    };


    private void sendEventMessage(String eventTag) {
        sendEventMessage(eventTag, null);
    }

    private void sendEventMessage(String eventTag, Object object) {
        EventMessage msg = new EventMessage(eventTag, object);
        EventBus.getDefault().post(msg);
    }
}
