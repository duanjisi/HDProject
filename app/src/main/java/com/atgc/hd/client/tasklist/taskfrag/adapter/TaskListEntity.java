package com.atgc.hd.client.tasklist.taskfrag.adapter;

import com.atgc.hd.comm.net.response.TaskListResponse.PointInfo;
import com.atgc.hd.comm.net.response.TaskListResponse.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/1
 */
public class TaskListEntity {
    public static final int ITEM_GROUP = 0;
    public static final int ITEM_CHILD = 1;

    private TaskInfo taskInfo;
    private PointInfo pointInfo;

    private int entityType;

    // 用于标识巡更任务为第几条，ITEM_GROUP == entityType才有意义
    private String groupPosition;
    // 用于标识group是否展示，ITEM_GROUP == entityType才有意义
    private boolean isGroupExpand;

    // 用于标识该点是否为该巡更任务的第一个点，ITEM_CHILD == entityType才有意义
    private boolean isFirstPointInfo;
    // 用于标识该点是否为该巡更任务的最后一个点，ITEM_CHILD == entityType才有意义
    private boolean isLastPointInfo;

    public TaskListEntity(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
        entityType = ITEM_GROUP;
    }

    public TaskListEntity(PointInfo pointInfo) {
        this.pointInfo = pointInfo;
        entityType = ITEM_CHILD;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setGroupExpand(boolean groupExpand) {
        if (ITEM_GROUP == entityType) {
            isGroupExpand = groupExpand;
        }
    }

    /**
     * ITEM_GROUP == entityType才会返回isGroupExpand值
     *
     * @return
     */
    public boolean isGroupExpand() {
        if (ITEM_GROUP == entityType) {
            return isGroupExpand;
        } else {
            return false;
        }
    }

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

    public PointInfo getPointInfo() {
        return pointInfo;
    }

    public void setPointInfo(PointInfo pointInfo) {
        this.pointInfo = pointInfo;
    }

    public boolean isFirstPointInfo() {
        return isFirstPointInfo;
    }

    public void setFirstPointInfo(boolean firstPointInfo) {
        isFirstPointInfo = firstPointInfo;
    }

    public boolean isLastPointInfo() {
        return isLastPointInfo;
    }

    public void setLastPointInfo(boolean lastPointInfo) {
        isLastPointInfo = lastPointInfo;
    }

    public String getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        if (groupPosition < 10) {
            this.groupPosition = "0" + groupPosition;
        } else {
            this.groupPosition = "" + groupPosition;
        }
    }

    public List<TaskListEntity> getPointInfoEntities() {
        if (ITEM_GROUP == entityType) {
            List<TaskListEntity> entities = new ArrayList<>();
            for (int i = 0, count = taskInfo.getPointArray().size(); i < count; i++) {
                PointInfo info = taskInfo.getPointArray().get(i);
                TaskListEntity entity = new TaskListEntity(info);

                entity.setFirstPointInfo(i == 0);
                entity.setLastPointInfo(i == (count - 1));

                entities.add(entity);
            }
            return entities;
        } else {
            return null;
        }
    }
}
