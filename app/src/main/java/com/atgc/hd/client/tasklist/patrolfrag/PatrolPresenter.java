package com.atgc.hd.client.tasklist.patrolfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.comm.local.Coordinate;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.local.LocationService.*;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.utils.CoordinateUtil;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;

/**
 * <p>描述：巡更任务presenter层
 * <p>作者：liangguokui 2018/1/23
 */
public class PatrolPresenter implements PatrolContract.IPresenterView, PatrolContract.IPresenterModel {
    /**
     * 打点范围，单位米
     */
    private static final double POINT_RANGE = 20;

    private PatrolContract.IView iView;
    private PatrolContract.IModel iModel;
    private TaskListResponse.PointInfo currentPointInfo = null;
    private TaskHandContract taskHandContract;

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

            if (distance < POINT_RANGE) {

            }
        }
    };

    public PatrolPresenter(PatrolContract.IView iView) {
        this.iView = iView;
        iModel = new PatrolModel(this);
    }

    @Override
    public void setTaskHandContract(TaskHandContract taskHandContract) {
        this.taskHandContract = taskHandContract;
        TaskListResponse.TaskInfo taskInfo = this.taskHandContract.getCurrentTask();
        Logger.e("currentTask: "+taskInfo);
        iView.refreshTaskList(taskInfo);
    }

    @Override
    public void onDestory() {
        LocationService.intance().unregisterLocationListener(locationListener);
    }

}
