package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.Criteria;
import com.yippie.android.classes.CriteriaV2;
import com.yippie.android.classes.Location;
import com.yippie.android.classes.ShopInfo;
import com.yippie.android.library.ImageHandler;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectStateFragmentV2 extends Fragment
{
    protected Activity activity;
    protected ListView stateListView;
    protected List<Location.State> stateList;
    protected ParentFragmentInterface interfaceObj;
    protected StateListAdapter adapter;

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
        View view = inflater.inflate(R.layout.category_state_view, container, false);

        // Import Assets
        this.stateListView = (ListView) view.findViewById(R.id.state_list);

        // Get the list of the state from Location class
        this.stateList = Location.getMalaysiaStateList();

        // Set adapter
        this.adapter = new StateListAdapter(this.activity,stateList);

        // Set the adapter to the listview
        this.stateListView.setAdapter(this.adapter);
        // Set on click actions
        this.stateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Get current position's state
                Location.State currentState = stateList.get(position);
                Integer stateId = currentState.getStateId();
                // Set criteria v2 singleton class
                CriteriaV2.getInstance().setState(stateId);

                // After set, we will call to parent
                interfaceObj.switchScreen(false,0);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity act)
    {
        super.onAttach(act);
        this.activity = act;
        this.interfaceObj = (ParentFragmentInterface) getParentFragment();
    }

    /**
     * @author  Written by Zi Yang
     * @desc This is a custom listview adapter
     */
    private class StateListAdapter extends BaseAdapter
    {
        private Activity activity;
        private List<Location.State> data;
        private LayoutInflater inflater;

        /**
         * Constructor For State
         */
        public StateListAdapter(Activity activity, List<Location.State> data)
        {
            this.activity = activity;
            this.data = data;
            this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        // Basic function of BaseAdapater. Used to return the size of Array, Map of data
        public int getCount() {
            return data.size();
        }

        @Override
        // Basic function of BaseAdapater. Used to get the current position of the item.
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // Custom BaseAdapter function. This will return the whole Conversation object
        public Location.State getStateInfo(int position)
        {
            return data.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // In here, we will construct the custom view for the BaseAdapter.
            // We will be using a class to store the view of current cell.
            // Note: BaseAdapter involve in recycling the cell/view once the cell/view is out of screen.
            //		 Which mean you will notice the view display in wrong position of the list.
            //		 To counter this, we MUST use a tag to identify the cell.
            ViewHolder holder;

            // First check if the view is empty
            if(convertView==null)
            {
                // Enter here if the view is empty
                // Customize the current cell view with custom design
                convertView = inflater.inflate(R.layout.location_state_item, parent, false);
                holder = new ViewHolder();
                // Import Asset
                holder.stateName = (TextView) convertView.findViewById(R.id.stateName);

                convertView.setTag(holder);
            }
            else
            {
                // View already exists
                // Retrieve existing view using Tag.
                holder = (ViewHolder) convertView.getTag();
            }

            // Get Current Position's Shop Info Object
            Location.State currentState = this.getStateInfo(position);

            // Get current state name
            String stateName = currentState.getStateName();
            // Set the state name to textview asset
            holder.stateName.setText(stateName);
            // Set shadow for the state name
            holder.stateName.setShadowLayer(1,0,0, Color.BLACK);

            return convertView;
        }

        // Create a ViewHolder class
        // Note: This class is used to store the list view cell
        public class ViewHolder {
            TextView stateName;
        }

        /**
         * Function to set the list
         */
        public void update(List<Location.State> d) {
            this.data = d;
            this.notifyDataSetChanged();
        }

        /**
         * Function to clear the list
         */
        public void clearList() {
            this.data.clear();
            this.notifyDataSetChanged();
        }
    }
}
