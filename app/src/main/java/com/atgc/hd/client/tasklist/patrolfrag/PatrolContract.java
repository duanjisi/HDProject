package com.atgc.hd.client.tasklist.patrolfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.comm.net.response.TaskListResponse;

/**
 * <p>描述：当前巡更任务契约
 * <p>作者：liangguokui 2018/1/23
 */
public interface PatrolContract {

    interface IModel {
    }

    interface IView {
        void refreshTaskList(TaskListResponse.TaskInfo taskInfo);
    }

    interface IPresenterView {

        void setTaskHandContract(TaskHandContract presenter);

        void onDestory();
    }

    interface IPresenterModel {
    }
}
