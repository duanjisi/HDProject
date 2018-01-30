package com.atgc.hd.client.tasklist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.adapter.ContentFragAdapter;
import com.atgc.hd.client.tasklist.patrolfrag.PatrolFrag;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.request.ReportPointStatusRequest;
import com.atgc.hd.comm.widget.PagerSlidingTabStrip;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>描述： 任务列表界面
 * <p>作者： liangguokui 2018/1/17
 */
public class TaskListActivity extends BaseActivity {
    private int currentFragmentPosition;

    private ContentFragAdapter fragAdapter;
    private PagerSlidingTabStrip pagerTitle;
    private ViewPager contentViewPager;

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

        FragmentManager manager = getSupportFragmentManager();
        manager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            int numberFragmentViewCreated = 0;

            @Override
            public void onFragmentViewCreated(FragmentManager fm, android.support.v4.app.Fragment f, View v, Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);

                if (f instanceof PatrolFrag) {
                    PatrolFrag patrolFrag = (PatrolFrag) f;
                    patrolFrag.registerTaskFinishListener(taskHandContract);
                }

                numberFragmentViewCreated++;
                if (numberFragmentViewCreated >= fragAdapter.getCount()) {
                    taskHandContract.initData();
                }
            }

        }, false);

    }

    private void initContentFragments() {
        pagerTitle = findViewById(R.id.topic_viewpager_title);
        contentViewPager = findViewById(R.id.content_viewpager);

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

    public void showCurrentTaskPage() {
        contentViewPager.setCurrentItem(0);
    }

    /**
     * 当前任务页注册监听
     *
     * @param listener
     */
    public void registerOnCurrentTaskListener(TaskHandContract.OnCurrentTaskListener listener) {
        taskHandContract.registerOnCurrentTaskListener(listener);
    }

    /**
     * 任务列表页面注册监听
     *
     * @param listener
     */
    public void registerOnAllTaskListener(TaskHandContract.OnAllTaskLlistener listener) {
        taskHandContract.registerOnAllTaskListener(listener);
    }
}
