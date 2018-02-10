/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.emergency;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.atgc.hd.R;
import com.atgc.hd.adapter.EventAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.db.dao.EventDao;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.EventEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：应急事件列表页
 * <p>作者：duanjisi 2018年 02月 01日
 */

public class EmergencyListActivity extends BaseActivity {
    @BindView(R.id.listview)
    ListView listview;
    private EventAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initListView();
    }

    private void initListView() {
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        barHelper.setActionRightTextColor(getResources().getColor(R.color.white));
        barHelper.setActionRightText("新建");
        barHelper.setActionRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(EmergencyEventActivity.class);
            }
        });
        adapter = new EventAdapter(context, new ItemClickListener());
        listview.setAdapter(adapter);

        ArrayList<EventEntity> entities = (ArrayList<EventEntity>) EventDao.getInstance().query();
        if (entities != null && entities.size() != 0) {
            adapter.initData(entities);
        }
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.emergence_event);
    }

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (event != null) {
            String action = event.getAction();
            if (action.equals(Constants.Action.EXIT_ACTIVITY)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class ItemClickListener implements EventAdapter.onItemClickListener {
        @Override
        public void onItemClick(EventEntity event) {
            if (event != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("obj", event);
                openActivity(EmergencyDetailsActivity.class, bundle);
            }
        }
    }
}
