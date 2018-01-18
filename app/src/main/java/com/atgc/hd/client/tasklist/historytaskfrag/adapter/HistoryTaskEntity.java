package com.atgc.hd.client.tasklist.historytaskfrag.adapter;

import com.atgc.hd.client.tasklist.tasklistfrag.adapter.TaskEntity;

import java.util.List;

/**
 * <p>描述： 历史巡更任务实体
 * <p>作者： liangguokui 2018/1/18
 */
public class HistoryTaskEntity {
    // 任务标题
    private String historyTaskTitle;
    // 任务状态
    private String taskStatus;
    // 任务时间段
    private String taskPeriod;

    private List<TaskEntity> historyTaskEntitys;

    public String getHistoryTaskTitle() {
        return historyTaskTitle;
    }

    public void setHistoryTaskTitle(String historyTaskTitle) {
        this.historyTaskTitle = historyTaskTitle;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(String taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    public List<TaskEntity> getHistoryTaskEntitys() {
        return historyTaskEntitys;
    }

    public void setHistoryTaskEntitys(List<TaskEntity> historyTaskEntitys) {
        this.historyTaskEntitys = historyTaskEntitys;
    }
}
