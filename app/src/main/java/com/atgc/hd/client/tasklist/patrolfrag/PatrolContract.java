package com.atgc.hd.client.tasklist.patrolfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.net.response.TaskListResponse;

import java.io.Serializable;
import java.util.List;

/**
 * <p>描述：当前巡更任务契约
 * <p>作者：liangguokui 2018/1/23
 */
public interface PatrolContract {

    interface IModel {
    }

    interface IView {

        void showTips(String tips);

        void showFillReasonDialog(String taskStatus, String carryStatus);

        void refreshTaskList(List<TaskListEntity> entities);
    }

    interface IPresenterView {
        void reportTaskStatus(String taskStatus, String carryStatus, String reason, OnReportTaskListener listener);

        void onDestory();
    }

    interface IPresenterModel {
    }

    interface OnReportTaskListener extends Serializable{
        void onReportSuccess();

        void onReportFail(String msg);
    }
}
