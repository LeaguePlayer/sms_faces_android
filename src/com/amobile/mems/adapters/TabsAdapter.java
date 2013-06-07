package com.amobile.mems.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.amobile.mems.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 25.04.13
 * Time: 20:01
 * To change this template use File | Settings | File Templates.
 */
public class TabsAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener {
    private Context mContext;
    private ViewPager mPager;
    private ArrayList<TabInfo> mTabs;
    private ActionBar mActionBar;

    public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());

        mContext = activity;
        mActionBar = activity.getSupportActionBar();
        mTabs = new ArrayList<TabInfo>();
        mPager = pager;
        mPager.setAdapter(this);
        mPager.setOnPageChangeListener(this);
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        Fragment fragment = (Fragment) Fragment.instantiate(mContext, info.mClass.getName(), info.mArgs);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mPager.setCurrentItem(mActionBar.getSelectedNavigationIndex());
        tab.select();
        //mActionBar.getSelectedTab().getCustomView().setBackground( mContext.getResources().getDrawable(R.drawable.tab_active));
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
       // tab.getCustomView().setBackground( mContext.getResources().getDrawable(R.drawable.tab));
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public void addTab(ActionBar.Tab tab, Bundle args, Class<?> clss) {
        TabInfo info = new TabInfo(args, clss);
        tab.setTag(info);
        tab.setTabListener(this);
        mActionBar.addTab(tab);
        mTabs.add(info);
        notifyDataSetChanged();
    }

    static final class TabInfo {
        private final Bundle mArgs;
        private final Class<?> mClass;

        TabInfo(Bundle mArgs, Class<?> mClass) {
            this.mArgs = mArgs;
            this.mClass = mClass;
        }
    }
}