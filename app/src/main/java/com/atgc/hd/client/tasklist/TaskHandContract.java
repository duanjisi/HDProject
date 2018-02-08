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
        void onReceiveTask(TaskListResponse.TaskInfo taskInfo);

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
    }
}
