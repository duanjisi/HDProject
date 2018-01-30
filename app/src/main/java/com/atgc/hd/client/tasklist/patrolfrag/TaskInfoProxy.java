package com.atgc.hd.client.tasklist.patrolfrag;

import com.atgc.hd.comm.clock.InnerClock;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.TaskListResponse.PointInfo;
import com.atgc.hd.comm.utils.DateUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>描述：巡更任务代理
 * <p>作者：liangguokui 2018/1/29
 */
public class TaskInfoProxy {

    private TaskListResponse.TaskInfo taskInfo;
    /**
     * 标志当前巡更任务在列表的索引位置
     */
    private int currentPointIndex = -1;

    /**
     * 当前任务点最迟的打点时间
     */
    private Date pointTimeLine;

    /**
     * 整个任务的巡查点结果，若某个点有异常，则整个巡查任务为异常（0正常，1异常）
     */
    private String carryStatus;

    public TaskInfoProxy(TaskListResponse.TaskInfo taskInfo) {
        this.taskInfo = taskInfo;

        init();
    }

    private void init() {
        int interval = 0;

        Collections.sort(getPointInfos());

        // getPointArray()应该是要以orderNo进行过排序的
        for (int i = 0; i < getPointInfos().size(); i++) {
            TaskListResponse.PointInfo pointInfo = taskInfo.getPointArray().get(i);

            if ("0".equals(pointInfo.getResultType()) || "2".equals(pointInfo.getResultType())) {
                currentPointIndex = i;
                break;
            } else {
                interval += pointInfo.getInterval();
            }
        }

        Date taskStart = DateUtil.dateParse(taskInfo.getStartTime());
        pointTimeLine = DateUtil.dateAddMinutes(taskStart, interval);
    }

    /**
     * 获取现在正在巡查的第一个点
     *
     * @return
     */
    public TaskListResponse.PointInfo getCurrentPointInfo() {
        if (0 <= currentPointIndex && currentPointIndex < taskInfo.getPointArray().size()) {
            return taskInfo.getPointArray().get(currentPointIndex);
        } else {
            return null;
        }
    }

    /**
     *
     */
    public void checkPoint() {
        PointInfo currentPointInfo = getPointInfos().get(currentPointIndex);

        String lastPointCheckedTime = InnerClock.instance().getInnerClockDate().toString();
        currentPointInfo.setCheckedTime(lastPointCheckedTime);

        String pointStatus = pointStatus();
        currentPointInfo.setResultType(pointStatus);

    }

    private String pointStatus() {
        Date pointTimeLine = getPointTimeLine();
        Date currentTime = InnerClock.instance().getInnerClockDate();

        if (currentTime.before(pointTimeLine)) {
            return "1";
        } else {
            return "3";
        }
    }

    /**
     * 是否还有下一个点
     *
     * @return
     */
    public boolean hasNextPoint() {
        if (currentPointIndex < taskInfo.getPointArray().size()) {
            return true;
        } else {
            return false;
        }
    }

    public void moveToNextPoint() {
        currentPointIndex++;
        PointInfo pointInfo = getCurrentPointInfo();
        pointTimeLine = DateUtil.dateAddMinutes(pointTimeLine, pointInfo.getInterval());
    }

    /**
     * 获取当前点的最迟打点时间
     *
     * @return
     */
    public Date getPointTimeLine() {
        return pointTimeLine;
    }

    public String getCarryStatus() {
        carryStatus = "0";

        for (PointInfo pointInfo : taskInfo.getPointArray()) {
            // 巡查点结果为1视为正常，其他情况视为异常
            if ("1".equals(pointInfo.getResultType())) {
            } else {
                carryStatus = "1";
            }
        }
        return carryStatus;
    }

    public TaskListResponse.TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public PointInfo getLastPointInfo() {
        return taskInfo.getPointArray().get(currentPointIndex - 1);
    }

    public List<PointInfo> getPointInfos() {
        return taskInfo.getPointArray();
    }
}
