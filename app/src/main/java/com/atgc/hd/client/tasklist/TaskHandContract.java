package com.atgc.hd.client.tasklist;

import com.atgc.hd.client.tasklist.patrolfrag.PatrolContract;
import com.atgc.hd.comm.net.response.TaskListResponse;

import java.io.Serializable;
import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */
public interface TaskHandContract extends PatrolContract.OnTaskActionListener {
    void initData();

    void registerOnCurrentTaskListener(OnCurrentTaskListener listener);

    void registerOnAllTaskListener(OnAllTaskLlistener listener);

    interface OnCurrentTaskListener {
        void onReceiveCurrentTask(TaskListResponse.TaskInfo taskInfo);

        TaskListResponse.TaskInfo stopTask();
    }

    interface OnAllTaskLlistener {
        void onReceiveAllTask(List<TaskListResponse.TaskInfo> taskInfos);

        void onCurrentTask(String taskId);
    }
}
