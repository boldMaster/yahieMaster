package com.yippie.android.library;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class TabsAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener,ViewPager.OnPageChangeListener
{
    private final Context context;
    private final ActionBar actionBar;
    private final ViewPager viewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private final List<Fragment> fragments = new ArrayList<>();
    private int curViewPagerIndex = 0;
    private Fragment curActiveFragment;

    /**
     * The tab info class
     */
    public static class TabInfo
    {
        private Class<?> Class;
        private Drawable tabIcon;
        private Bundle args;

        /**
         * Creates a new instance.
         * @param Class - The fragment class
         * @param args  - The args
         */
        public TabInfo(Class<?> Class, Drawable tabIcon, Bundle args)
        {
            this.Class = Class;
            this.tabIcon = tabIcon;
            this.args = args;
        }

        @Override
        public boolean equals(final Object o)
        {
            return this.Class.getClass().getName().equals(o.getClass().getName());
        }

        @Override
        public int hashCode()
        {
            return this.Class.getClass().getName() != null ? Class.getClass().getName().hashCode() : 0;
        }

        @Override
        public String toString()
        {
            return "TabInfo{" + "fragment=" + Class + "}";
        }
    }

    /**
     * Constructor
     * @param activity - The ActionBarActivity that hosting the ActionBar and ViewPager
     * @param pager    - The View Pager
     */
    public TabsAdapter(Activity activity, ActionBar actionBar, ViewPager pager)
    {
        super(activity.getFragmentManager());
        context = activity;
        this.actionBar = actionBar;
        this.actionBar.setDisplayHomeAsUpEnabled(false);
        this.actionBar.setDisplayShowHomeEnabled(false);
        this.actionBar.setDisplayShowTitleEnabled(false);
        this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager = pager;
        viewPager.setAdapter(this);
        viewPager.setOnPageChangeListener(this);
    }

    /**
     * Function to add tab into action bar
     */
    public void addTab(TabInfo newTabInfo)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setTag(newTabInfo);
        tab.setTabListener(this);
        tab.setIcon(newTabInfo.tabIcon);
        mTabs.add(newTabInfo);
        actionBar.addTab(tab);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(context, info.Class.getName(), info.args);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    /**
     * Override the default setPrimaryItem function.
     * Due to we unable to get current state of fragment from viewpager that extends FragmentStatePagerAdapter,
     * we have to perform some trick in here: Save a copy of the current fragment as a copy.
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object){
        if(curActiveFragment!=object)
        {
            curActiveFragment = (Fragment) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Object tag = tab.getTag();
        for (int i=0; i<mTabs.size(); i++)
        {
            if (mTabs.get(i) == tag)
            {
                viewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
        curViewPagerIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    /**
     * This function will return the fragment based on current viewpager/tab
     */
    public Fragment getCurrentActiveFragment(){
          return curActiveFragment;
    }
}
