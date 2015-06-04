package com.yippie.android;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import com.yippie.android.library.TabsAdapter;

public class MainActivity extends Activity
{
    protected ViewPager viewPager;
    protected TabsAdapter adapter;
    protected SparseArray fragmentList;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.actionBar = getActionBar();
        // Set layout
        this.setContentView(R.layout.activity_main);

        fragmentList = new SparseArray<Fragment>();

        // Setup ActionBar and ViewPager in TabsAdapter
        this.viewPager = (ViewPager) findViewById(R.id.rootViewPager);
        this.adapter  = new TabsAdapter(this,actionBar,viewPager);
        // Initialize Action Bar Tabs
        this.initializeActionBar(savedInstanceState);
    }

    /**
     * This function returns list of action bar tabs
     */
    private List<TabsAdapter.TabInfo> getActionBarTabs(Bundle args)
    {
        List<TabsAdapter.TabInfo> actionBarTabs = new ArrayList<>();

        // Add Main Map Tab
        Drawable mainMapIcon = getResources().getDrawable(R.drawable.icon_map);
        actionBarTabs.add(new TabsAdapter.TabInfo(MainMapFragment.class,mainMapIcon,args));

        // Add Category Tab
        Drawable categoryIcon = getResources().getDrawable(R.drawable.icon_category);
        actionBarTabs.add(new TabsAdapter.TabInfo(CategoryListFragment.class,categoryIcon,args));

        // Add Event Tab
        Drawable eventIcon = getResources().getDrawable(R.drawable.icon_suprise);
        actionBarTabs.add(new TabsAdapter.TabInfo(EventFragment.class,eventIcon,args));

        // Add Option Menu Tab
        Drawable optMenuIcon = getResources().getDrawable(R.drawable.icon_user);
        actionBarTabs.add(new TabsAdapter.TabInfo(OptionMenuTopFragment.class,optMenuIcon,args));

        return actionBarTabs;
    }

    /**
     * This function initialize the action bar tabs.
     * Note: If you were about to add a new action bar tab to this activity, please add it in here
     */
    private void initializeActionBar(Bundle args)
    {
        // Get the list of Action Bar Tab Items
        List<TabsAdapter.TabInfo> actionBarTabs = getActionBarTabs(args);

        // Add the action bar tabs
        for(TabsAdapter.TabInfo tab : actionBarTabs)
        {
            this.adapter.addTab(tab);
        }

    }

    /**
     * Override the back press event
     */
    @Override
    public void onBackPressed(){

        // Get current visible active fragment
        Fragment fragment = this.adapter.getCurrentActiveFragment();

        // Retrieve the child fragment manager holding our fragment in the back stack
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();

        // And here we go, if the back stack is empty, we let the back button doing its job
        // Otherwise, we show the last entry in the back stack
        if(childFragmentManager.getBackStackEntryCount() == 0)
        {
            super.onBackPressed();
        }
        else
        {
            childFragmentManager.popBackStack();
        }
    }
}