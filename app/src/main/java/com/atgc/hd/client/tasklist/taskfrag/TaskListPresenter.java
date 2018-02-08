package com.atgc.hd.client.tasklist.taskfrag;

import android.util.SparseArray;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.entity.EventMessage;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：任务列表presenter
 * <p>作者：liangguokui 2018/1/25
 */
public class TaskListPresenter implements TaskListContract.IPresenterView, TaskListContract.IPresenterModel, TaskHandContract.OnAllTaskLlistener {

    private TaskListContract.IView iView;

    private String currentTaskId;

    public TaskListPresenter(TaskListContract.IView iView) {
        this.iView = iView;

        // 在TaskHandModel注册监听
        TaskHandContract.OnAllTaskLlistener listener = this;
        EventMessage msg = new EventMessage("on_all_task_listener", listener);
        EventBus.getDefault().post(msg);
    }

    @Override
    public boolean isCurrentTask(String currentTaskId) {
        if (StringUtils.isEmpty(this.currentTaskId)) {
            return false;
        } else {
            return this.currentTaskId.equals(currentTaskId);
        }
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
    public void onReceiveAllTask(SparseArray<TaskListResponse.TaskInfo> taskInfos) {

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
