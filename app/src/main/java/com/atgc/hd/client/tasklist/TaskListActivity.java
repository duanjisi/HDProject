package com.atgc.hd.client.tasklist;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.emergency.EmergencyListActivity;
import com.atgc.hd.client.platform.PlatformInfoActivity;
import com.atgc.hd.client.setting.SettingActivity;
import com.atgc.hd.client.tasklist.adapter.ContentFragAdapter;
import com.atgc.hd.comm.widget.PagerSlidingTabStrip;
import com.atgc.hd.entity.EventMessage;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述： 任务列表界面
 * <p>作者： liangguokui 2018/1/17
 */
public class TaskListActivity extends BaseActivity implements TaskHandContract.IView {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    private ContentFragAdapter fragAdapter;
    private PagerSlidingTabStrip pagerTitle;
    private ViewPager contentViewPager;

    private TaskHandContract taskHandContract;

    @Override
    public String toolBarTitle() {
        return "手持智能终端";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasklist);

        taskHandContract = new TaskHandModel(this);

        init();

        initContentFragments();

        addFragmentInitListener();

        EventBus.getDefault().register(this);
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
                BaseFragment fragment = fragAdapter.getItem(position);
                fragment.onFragmentSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void init() {
        barHelper.setActionLeftDrawable(R.drawable.ic_setting);
        barHelper.setActionRightDrawable(R.drawable.ic_bell);
        barHelper.setActionLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到设置页面
                openActivity(SettingActivity.class);
            }
        });

        barHelper.setActionRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到系统消息页面
                openActivity(PlatformInfoActivity.class);
            }
        });

        findViewById(R.id.iv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 调用接口刷新巡更数据
                TaskHandModel model = (TaskHandModel) taskHandContract;
                model.demoNfcCardNum("636B3EA4");
//                showProgressDialog();
//                taskHandContract.initData();
            }
        });

        findViewById(R.id.iv_emergency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 应急事件
                openActivity(EmergencyListActivity.class);
            }
        });
    }

    private void addFragmentInitListener() {
        FragmentManager manager = getSupportFragmentManager();
        manager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            int numberFragmentViewCreated = 0;

            @Override
            public void onFragmentViewCreated(FragmentManager fm, android.support.v4.app.Fragment f, View v, Bundle savedInstanceState) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                numberFragmentViewCreated++;
                if (numberFragmentViewCreated >= fragAdapter.getCount()) {
                    showProgressDialog();
                    taskHandContract.initData();
                }
            }
        }, false);
    }

    @Override
    public void dimssProgressDialog() {
        dismissProgressDialog();
    }

    @Override
    public void toastMessage(String message) {
        showToast(message);
    }

    @Override
    public void onNewIntent(Intent intent) {
        //1.获取Tag对象
        Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        taskHandContract.handleNfcTag(nfcTag);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 此处adapter需要重新获取，否则无法获取message
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // 当截获NFC消息，就会通过PendingIntent调用窗口
        Intent intent = new Intent(this, getClass());
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    /**
     * 获得焦点，按钮可以点击
     */
    @Override
    public void onResume() {
        super.onResume();
        //设置处理优于所有其他NFC的处理
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    /**
     * 暂停Activity，界面获取焦点，按钮可以点击
     */
    @Override
    public void onPause() {
        super.onPause();
        //恢复默认状态
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        taskHandContract.onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void switchTaskPage(EventMessage message) {
        if ("switch_task_page".equals(message.eventTag)) {
            Integer pageIndex = (Integer) message.object;
            contentViewPager.setCurrentItem(pageIndex.intValue());
        }
    }

    private long firstClick;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - firstClick > 2000) {
            firstClick = System.currentTimeMillis();
            showToast("再按一次退出");
        } else {
            super.onBackPressed();
            System.exit(0);
        }

    }
}
