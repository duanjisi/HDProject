package com.atgc.hd.client.tasklist.patrolfrag;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/23
 */
public class PatrolModel implements PatrolContract.IModel {

    private PatrolContract.IPresenterModel iPresenter;

    public PatrolModel(PatrolContract.IPresenterModel iPresenter) {
        this.iPresenter = iPresenter;
//        TcpSocketClient.getInstance().registerOnReceiveListener(this);
    }


}
