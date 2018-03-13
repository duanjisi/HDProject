/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.platform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.platform.adapter.DispatchMemberAdapter;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.db.dao.PlatformInfoDao;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.EventMessage;
import com.atgc.hd.entity.PatInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：派遣人员消息
 * <p>作者：liangguokui 2018/3/12
 */
public class DispatchMemberInfoActivity extends BaseActivity {

    @BindView(R.id.dispatch_recyclerview)
    RecyclerView dispatchRecyclerView;

    @BindView(R.id.btn_test)
    Button btnTest;

    private DispatchMemberAdapter dispatchMemberAdapter;
    private PlatformInfoDao infoDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_member);
        ButterKnife.bind(this);

        barHelper.setTitleColor(getResources().getColor(R.color.white));
        initView();

        EventBus.getDefault().register(this);
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.dispatch_msg);
    }

    private void initView() {
//        int visible = Constants.isDemo ? View.VISIBLE : View.GONE;
//        btnTest.setVisibility(visible);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        dispatchRecyclerView.setLayoutManager(manager);
        dispatchMemberAdapter = new DispatchMemberAdapter(context, true);
        dispatchRecyclerView.setAdapter(dispatchMemberAdapter);

        infoDao = PlatformInfoDao.getInstance();

        List<PatInfo> infos =  infoDao.queryDispatchInfo();
        dispatchMemberAdapter.setNewData(infos);
    }

    @OnClick(R.id.btn_test)
    public void onClickAddData() {
        EventBus.getDefault().post(new EventMessage("add_dispatch_info"));
    }

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (Constants.Action.DISPATCH_INFO.equals(event.getAction())) {
            PatInfo patInfo = (PatInfo) event.getData();
            dispatchMemberAdapter.addWithAnimation(0, patInfo);
            dispatchRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
