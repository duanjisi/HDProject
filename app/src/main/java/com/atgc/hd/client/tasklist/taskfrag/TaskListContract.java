package com.atgc.hd.client.tasklist.taskfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.comm.net.response.TaskListResponse;

import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */
public interface TaskListContract {

    interface IModel {

    }

    interface IView {
        void registerOnAllTaskListener(TaskHandContract.OnAllTaskLlistener listener);

        void refreshTaskList(List<TaskListResponse.TaskInfo> taskArray);
    }

    interface IPresenterView {
        boolean isCurrentTask(String currentTaskId);

        void onDestory();
    }

    interface IPresenterModel {

    }
}
