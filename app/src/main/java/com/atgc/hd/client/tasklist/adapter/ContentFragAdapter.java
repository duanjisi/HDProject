package com.atgc.hd.client.tasklist.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.patrolfrag.PatrolFrag;
import com.atgc.hd.client.tasklist.taskfrag.TaskListFrag;
import com.atgc.hd.comm.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述： 巡更界面的frag适配器
 * <p>作者： liangguokui 2018/1/18
 */
public class ContentFragAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.TabProvider {

    private List<BaseFragment> fragments;
    private List<String> titles;
//    private int[] icons;

    public ContentFragAdapter(FragmentManager fm) {
        super(fm);

        fragments = new ArrayList<>();
        titles = new ArrayList<>();

        fragments.add(PatrolFrag.createIntance());
        fragments.add(TaskListFrag.createIntance());

        titles.add("当前任务");
        titles.add("任务列表");

//        int[] temp = {R.drawable.ic_svg_home,
//
//                R.drawable.ic_svg_keyproject,
//
//                R.drawable.ic_svg_workschedule};
//        icons = temp;
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getPageIconResId(int position) {
//        return icons[position];
        return 0;
    }

    @Override
    public String getCurrentPageTitle(int position) {
        return titles.get(position);
    }

    public void addAll( List<BaseFragment> fragments) {
        this.fragments.addAll(fragments);
        notifyDataSetChanged();
    }
}
