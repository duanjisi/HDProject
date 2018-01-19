/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.adapter.MemberAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.entity.Member;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>描述：会话消息列表界面
 * <p>作者：duanjisi 2018年 01月 17日
 */

public class ConversationActivity extends BaseActivity {

    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_book)
    TextView tvBook;
    @BindView(R.id.listview)
    ListView listview;

    private MemberAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        ButterKnife.bind(this);
        intiListView();
        testDatas();
    }

    private void intiListView() {
        adapter = new MemberAdapter(context);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new ItemClickListener());
        listview.setOnItemLongClickListener(new ItemLongClickListener());
    }

    private void testDatas() {
        Member m = new Member();
        m.setDevice("通讯设备-001");
        m.setIdentity("当值警卫");
        m.setUserName("张三");
        m.setTime("10:00");

        Member m1 = new Member();
        m1.setDevice("通讯设备-002");
        m1.setIdentity("休假警卫");
        m1.setUserName("张三");
        m1.setTime("11:00");

        Member m2 = new Member();
        m2.setDevice("通讯设备-003");
        m2.setIdentity("当值警卫");
        m2.setUserName("王五");
        m2.setTime("12:00");


        Member m3 = new Member();
        m3.setDevice("通讯设备-004");
        m3.setIdentity("休假警卫");
        m3.setUserName("李四");
        m3.setTime("10:40");

        ArrayList<Member> members = new ArrayList<>();
        members.add(m);
        members.add(m1);
        members.add(m2);
        members.add(m3);
        adapter.initData(members);
    }

    @OnClick({R.id.tv_back, R.id.tv_book})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_book:
                break;
        }
    }


    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Member member = (Member) adapterView.getItemAtPosition(i);
            if (member != null) {
                openActivity(ChatActivity.class);
            }
        }
    }

    private class ItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            Member member = (Member) adapterView.getItemAtPosition(i);
            if (member != null) {

            }
            return false;
        }
    }
}
