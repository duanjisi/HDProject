package com.atgc.hd.client.tasklist;

import android.nfc.Tag;
import android.util.SparseArray;

import com.atgc.hd.comm.net.response.TaskListResponse;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */
public interface TaskHandContract {
    void initData();

    void handleNfcTag(Tag nfcTag);

    void onDestroy();

    interface OnCurrentTaskListener {

        /**
         * 任务状态为未开始且任务完成时间大于当前时间，则会调用该方法
         *
         * @param taskInfo
         */
        void onReceiveCurrentTask(TaskListResponse.TaskInfo taskInfo);

        /**
         * 任务状态为未开始、进行中，但任务完成时间小于当前时间，需要上报任务结束状态
         *
         * @param exceptionTasks
         */
        void onReceiveExceptionTask(SparseArray<TaskListResponse.TaskInfo> exceptionTasks);

        void onReceiveNfcCardNum(String cardNum);

        TaskListResponse.TaskInfo stopTask();
    }

    interface OnAllTaskLlistener {

        void onReceiveAllTask(SparseArray<TaskListResponse.TaskInfo> taskInfos);

        void onCurrentTask(String taskId);
    }

    interface IView {
        void showProgressDialog(String msg);

        void dimssProgressDialog();

        void toastMessage(String message);
    }
}
