package com.atgc.hd.client.tasklist;

import android.os.Parcelable;

import com.atgc.hd.comm.net.response.TaskListResponse;

import java.io.Serializable;
import java.util.List;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/25
 */
public interface TaskHandContract extends Serializable {
    void initData(OnInitDataListener listener);

    TaskListResponse.TaskInfo getCurrentTask();

    List<TaskListResponse.TaskInfo> getAllTask();

    interface OnInitDataListener {
        void onInitData(boolean initSuccess);
    }
}
