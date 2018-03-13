package com.atgc.hd.client.tasklist.patrolfrag;

import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.clock.InnerClock;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.TaskListResponse.PointInfo;
import com.atgc.hd.comm.utils.DateUtil;

import java.util.ArrayList;
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
     * 标志当前巡更点在列表的索引位置
     */
    private int currentPointIndex = -1;

    public TaskInfoProxy(TaskListResponse.TaskInfo taskInfo) {
        this.taskInfo = taskInfo;

        init();
    }

    private void init() {
        Collections.sort(getPointInfos());

        // getPointArray()应该是要以orderNo进行过排序的
        for (int i = 0; i < getPointInfos().size(); i++) {
            TaskListResponse.PointInfo pointInfo = taskInfo.getPointArray().get(i);

            pointInfo.initCoordinate();

            // 找到第1个未打点/超时未巡查的点
            if ("0".equals(pointInfo.getResultType()) || "2".equals(pointInfo.getResultType())) {
                if (currentPointIndex == -1) {
                    currentPointIndex = i;
                }
            }
        }

        initPlanTime();
    }

    private void initPlanTime() {
        Date planTime;
        PointInfo currentPointInfo = taskInfo.getPointArray().get(currentPointIndex);
        // 当任务列表的第1个点为未打点状态时，需要设置该点的最晚打点时间
        if (currentPointIndex == 0) {
            Date taskStart = taskInfo.taskStartTime();
            planTime = DateUtil.dateAddMinutes(taskStart, currentPointInfo.getInterval());
        }
        // 当任务列表的第N个点为未打点状态时，需要根据第N-1个点的已打点时间设置第N个点的最晚打点时间
        else {
            PointInfo lastCheckedPointInfo = taskInfo.getPointArray().get(currentPointIndex - 1);
            Date taskStart = DateUtil.dateParse(lastCheckedPointInfo.getPointTime());
            planTime = DateUtil.dateAddMinutes(taskStart, currentPointInfo.getInterval());
        }

        // 若最晚打点时间早于巡查任务的最晚打点时间，则设置当前最晚打点时间
        if (planTime.before(taskInfo.taskEndTime())) {
            currentPointInfo.setPlanTime(DateUtil.dateFormat(planTime, DateUtil.DATE_TIME_PATTERN));
        }
        // 否则设置任务的最晚打点时间
        else {
            currentPointInfo.setPlanTime(taskInfo.getEndTime());
        }
    }

    /**
     * 获取现在正在巡查的点
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
     * 超时未打点
     */
    public void checkPointTimeOut() {
        PointInfo currentPointInfo = getPointInfos().get(currentPointIndex);
        currentPointInfo.setResultType("2");
    }

    /**
     * 打点
     */
    public void checkPoint() {
        PointInfo currentPointInfo = getPointInfos().get(currentPointIndex);

        String lastPointCheckedTime = DateUtil.dateFormat(InnerClock.instance().nowDate(), DateUtil.DATE_TIME_PATTERN);

        currentPointInfo.setPointTime(lastPointCheckedTime);

        String pointStatus = pointStatus();
        currentPointInfo.setResultType(pointStatus);
    }

    private String pointStatus() {
        Date pointTimeLine = getCurrentPointPlanTime();
        Date currentTime = InnerClock.instance().nowDate();

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
        if (currentPointIndex < (taskInfo.getPointArray().size() - 1)) {
            return true;
        } else {
            return false;
        }
    }

    public void moveToNextPoint() {
        currentPointIndex++;

        initPlanTime();
    }

    /**
     * 获取当前点的最迟打点时间
     *
     * @return
     */
    public Date getCurrentPointPlanTime() {
        return DateUtil.dateParse(getCurrentPointInfo().getPlanTime());
    }

    /**
     * 获取任务状态
     *
     * @return 3：时间范围内结束任务 4：超出时间，强制结束
     */
    public String getTaskStatus() {
        Date nowTime = InnerClock.instance().nowDate();
        Date taskEndTime = taskInfo.taskEndTime();
        String taskStatus = nowTime.before(taskEndTime) ? "3" : "4";

        return taskStatus;
    }

    /**
     * 获取巡查结果状态
     *
     * @return 0：正常  1：异常（巡查点有超时）
     */
    public String getCarryStatus() {
        String carryStatus = "0";

        for (PointInfo pointInfo : taskInfo.getPointArray()) {
            // 巡查点结果为1视为正常，其他情况视为异常
            if ("1".equals(pointInfo.getResultType())) {
            } else {
                carryStatus = "1";
                break;
            }
        }
        return carryStatus;
    }

    public TaskListResponse.TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public List<PointInfo> getPointInfos() {
        return taskInfo.getPointArray();
    }

    public List<TaskListEntity> adapterEntities() {
        List<TaskListEntity> entities = new ArrayList<>();
        entities.add(new TaskListEntity(taskInfo));
        for (int i = 0, count = taskInfo.getPointArray().size(); i < count; i++) {
            PointInfo info = taskInfo.getPointArray().get(i);
            TaskListEntity entity = new TaskListEntity(info);

            entity.setFirstPointInfo(i == 0);
            entity.setLastPointInfo(i == (count - 1));

            entities.add(entity);
        }

        return entities;
    }
}
