package com.atgc.hd.client.tasklist.taskfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.net.response.TaskListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：任务列表presenter
 * <p>作者：liangguokui 2018/1/25
 */
public class TaskListPresenter implements TaskListContract.IPresenterView, TaskListContract.IPresenterModel, TaskHandContract.OnAllTaskLlistener {

    private TaskListContract.IView iView;

    private String currentTaskId;

    public TaskListPresenter(TaskListContract.IView iView) {
        this.iView = iView;

        iView.registerOnAllTaskListener(this);
    }

    @Override
    public boolean isCurrentTask(String currentTaskId) {
        return this.currentTaskId.equals(currentTaskId);
    }

    @Override
    public void onDestory() {

    }

    /**
     * TaskHandModel处理完数据后，将会回调该方法
     *
     * @param taskInfos
     */
    @Override
    public void onReceiveAllTask(List<TaskListResponse.TaskInfo> taskInfos) {

        List<TaskListEntity> entities = new ArrayList<>();

        for (int i = 0; i < taskInfos.size(); i++) {
            TaskListResponse.TaskInfo taskInfo = taskInfos.get(i);
            TaskListEntity entity = new TaskListEntity(taskInfo);
            entity.setGroupPosition(i + 1);
            entities.add(entity);
        }

        iView.refreshTaskList(entities);
    }

    @Override
    public void onCurrentTask(String taskId) {
        currentTaskId = taskId;
    }
}
