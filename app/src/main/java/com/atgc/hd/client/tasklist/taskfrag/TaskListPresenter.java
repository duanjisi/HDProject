package com.atgc.hd.client.tasklist.taskfrag;

import com.atgc.hd.client.tasklist.TaskHandContract;

/**
 * <p>描述：任务列表presenter
 * <p>作者：liangguokui 2018/1/25
 */
public class TaskListPresenter implements TaskListContract.IPresenterView, TaskListContract.IPresenterModel{

    private TaskListContract.IView iView;

    public TaskListPresenter(TaskListContract.IView iView) {
        this.iView = iView;
    }

    @Override
    public void setTaskHandContract(TaskHandContract presenter) {
         iView.refreshTaskList(presenter.getAllTask());
    }

    @Override
    public void onDestory() {

    }
}
