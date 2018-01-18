package com.atgc.hd.client.tasklist.historytaskfrag;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;

/**
 * <p>描述： 历史巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class HistoryTaskListFrag extends BaseFragment {

    public static HistoryTaskListFrag createIntance() {
        return new HistoryTaskListFrag();
    }

    @Override
    public int layoutResourceId() {
        return R.layout.frag_tasklist;
    }
}
