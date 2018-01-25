package com.atgc.hd.client.tasklist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.adapter.ContentFragAdapter;
import com.atgc.hd.client.tasklist.patrolfrag.PatrolFrag;
import com.atgc.hd.client.tasklist.taskfrag.TaskListFrag;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述： 任务列表界面
 * <p>作者： liangguokui 2018/1/17
 */
public class TaskListActivity extends BaseActivity {
    private int currentFragmentPosition;

    private ContentFragAdapter fragAdapter;
    private PagerSlidingTabStrip pagerTitle;

    private TaskHandContract taskHandContract;

    @Override
    public String toolBarTitle() {
        return "巡更";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);

        taskHandContract = new TaskHandModel();
        initContentFragments();

        taskHandContract.initData(new TaskHandContract.OnInitDataListener() {
            @Override
            public void onInitData(boolean initSuccess) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragAdapter.addAll(getFragments());
                        pagerTitle.notifyDataSetChanged();
                    }
                });
            }
        });

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragAdapter.addAll(getFragments());
                pagerTitle.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btn_test2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSRequest gpsRequest = new GPSRequest();
                gpsRequest.setLongitude("23.65555");
                gpsRequest.setLatitude("113.546841");

                gpsRequest.send(new BaseDataRequest.RequestCallback() {
                    @Override
                    public void onSuccess(Object pojo) {

                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
            }
        });
    }


    private void initContentFragments() {
        pagerTitle = findViewById(R.id.topic_viewpager_title);
        ViewPager contentViewPager = findViewById(R.id.content_viewpager);

        contentViewPager.setOffscreenPageLimit(3);

        fragAdapter = new ContentFragAdapter(getSupportFragmentManager());
        contentViewPager.setAdapter(fragAdapter);
        pagerTitle.setViewPager(contentViewPager);

        contentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentFragmentPosition = position;
                BaseFragment fragment = fragAdapter.getItem(currentFragmentPosition);
                fragment.onFragmentSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private List<BaseFragment> getFragments() {
        List<BaseFragment> fragments = new ArrayList<>();

        PatrolFrag patrolFrag = PatrolFrag.createIntance();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("taskHandContract", taskHandContract);
        patrolFrag.setArguments(bundle1);

        TaskListFrag taskListFrag = TaskListFrag.createIntance();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("taskHandContract", taskHandContract);
        taskListFrag.setArguments(bundle2);

        fragments.add(patrolFrag);
        fragments.add(taskListFrag);

        return fragments;
    }

}
