package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ming on 3/23/2016.
 */
public class EventMainFragment extends Fragment implements TabHost.OnTabChangeListener{
    // Array list that store list of category
    protected Activity activity;
    protected FragmentTabHost fragmentTabHost;
    protected TabWidget tabWidget;
    protected List<Tab> tabList = new ArrayList<>();

    public static final int EVENT_TAB = 0;
    public static final int MY_EVENT_TAB = 1;

    protected static final String EVENT_TAB_TITLE = "Event";
    protected static final String MY_EVENT_TAB_TITLE = "My Event";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.user_menu, container, false);

        // Initialize Fragment TabHost
        this.initializeFragmentTabHost(view);

        return view;
    }

    @Override
    public void onAttach(Activity act)
    {
        super.onAttach(act);
        this.activity = act;
    }

    /**
     * Private Fragment TabHost Initialization
     */
    private void initializeFragmentTabHost(View view)
    {
        // Set the list of tabs to show
        this.tabList.add(EVENT_TAB, new Tab(EVENT_TAB, EVENT_TAB_TITLE, new EventFragment()));
        this.tabList.add(MY_EVENT_TAB, new Tab(MY_EVENT_TAB, MY_EVENT_TAB_TITLE, new MyEventFragment()));

        // Initialize Fragment Tabhost
        this.fragmentTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        this.fragmentTabHost.setup(activity, getChildFragmentManager(), R.id.realTabContent);
        this.fragmentTabHost.setOnTabChangedListener(this);
        // Get tab widget
        this.tabWidget = this.fragmentTabHost.getTabWidget();

        // Add tabs to Tab Host
        for(Tab curTab : this.tabList)
        {
            // Create new TabSpec
            TabHost.TabSpec newTab = this.fragmentTabHost.newTabSpec(curTab.getTitle());
            // Set tab item view
            newTab.setIndicator(getTabItemView(curTab.getTabIndex()));
            // Add tab
            this.fragmentTabHost.addTab(newTab, curTab.getFragmentClass(), null);

        }

        // Update all tab background resource
        for(int i=0; i < this.tabWidget.getTabCount(); i++)
        {
            View tabView = this.fragmentTabHost.getTabWidget().getChildAt(i);
            tabView.setBackgroundResource(R.drawable.user_menu_tab_act);
        }
    }

    /**
     * Function to construct the Tab item view for Fragment TabHost
     */
    private View getTabItemView(Integer tabIndex)
    {
        // Inflate layout
        View view = LayoutInflater.from(activity).inflate(R.layout.tab_item, null, false);

        // Import assets
        TextView tabTitle = (TextView) view.findViewById(R.id.tab_title);
        tabTitle.setText(tabList.get(tabIndex).getTitle());

        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        // Get current Tab index
        int position = this.fragmentTabHost.getCurrentTab();
        // Update the Tab item background
        this.fragmentTabHost.getTabWidget().getChildAt(position).setBackgroundResource(R.drawable.user_menu_tab_act);
    }

    /**
     * Custom Adapter for View Pager
     */
    private class ViewPagerAdapter extends FragmentPagerAdapter
    {
        protected List<Tab> tabList;

        /**
         * Constructor
         */
        public ViewPagerAdapter(FragmentManager fragmentManager, List<Tab> fList){
            super(fragmentManager);
            this.tabList = fList;
        }

        @Override
        public Fragment getItem(int position) {
            return this.tabList.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return this.tabList.size();
        }
    }

    /**
     * Custom Class that used in TabHost item
     */
    private class Tab
    {
        private Fragment tabFragment;
        private Class<?> fragmentClass;
        private String tabTitle;
        private Integer tabIndex;

        /**
         * Constructor
         */
        public Tab(Integer tabIndex, String tabTitle, Fragment tabFragment)
        {
            this.tabFragment = tabFragment;
            this.fragmentClass = tabFragment.getClass();
            this.tabTitle = tabTitle;
            this.tabIndex = tabIndex;
        }

        // Getter
        public Fragment getFragment(){
            return tabFragment;
        }
        public String getTitle(){
            return tabTitle;
        }
        public Integer getTabIndex(){
            return tabIndex;
        }
        public Class<?> getFragmentClass(){
            return fragmentClass;
        }
    }
}
