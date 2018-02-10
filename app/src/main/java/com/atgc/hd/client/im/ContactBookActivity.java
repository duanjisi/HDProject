/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.im;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.atgc.hd.R;
import com.atgc.hd.adapter.MemberAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.entity.Member;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>描述：通讯录
 * <p>作者：duanjisi 2018年 01月 25日
 */
public class ContactBookActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView listview;
    private MemberAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.bind(this);
        intiListView();
    }

    private void intiListView() {
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        adapter = new MemberAdapter(context);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new ItemClickListener());
        listview.setOnItemLongClickListener(new ItemLongClickListener());
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.contact);
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
