package com.atgc.hd.client.tasklist.tasklistfrag;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;

/**
 * <p>描述： 当天巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskListFrag extends BaseFragment {

    public static TaskListFrag createIntance() {
        return new TaskListFrag();
    }

    @Override
    public int layoutResourceId() {
        return R.layout.frag_tasklist;
    }
}
