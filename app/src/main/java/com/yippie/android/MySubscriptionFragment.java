package com.yippie.android;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yippie.android.classes.ShopInfo;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Tan on 5/20/2015.
 */
public class MySubscriptionFragment extends Fragment implements View.OnClickListener
{
    protected Activity activity;
    protected ListView recommendedToYouList;
    protected ParentFragmentInterface interfaceObj;
    protected RecomendedListAdapter adapter;
    protected SortedMap<Integer,ShopInfo> recommendedList = new TreeMap<>();

    /**
     * Constructor
     */
    public MySubscriptionFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.recommended_to_you, container, false);

        // Import assets
        this.recommendedToYouList = (ListView) view.findViewById(R.id.recommendedToYouList);

        // Setup adapter
        this.adapter = new RecomendedListAdapter(activity,recommendedList);
        // Set adapter to the List View
        this.recommendedToYouList.setAdapter(adapter);

        for(int i=0; i<5; i++)
        {
            this.recommendedList.put(i, new ShopInfo());
        }
        this.adapter.setMap(recommendedList);
        this.adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }

    /**
     * Custom Adapter for ListView
     */
    private class RecomendedListAdapter extends BaseAdapter
    {
        private Activity activity;
        private SortedMap<Integer,ShopInfo> data;
        private LayoutInflater inflater;

        /**
         * Constructor
         */
        public RecomendedListAdapter(Activity activity, SortedMap<Integer,ShopInfo> data)
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

        // Custom BaseAdapter function. This will return the whole ConversationSubject object
        public ShopInfo getConversation(int position)
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
                convertView = inflater.inflate(R.layout.shop_list_item, parent, false);
                holder = new ViewHolder();
                // Import Asset
                holder.shopImage = (ImageView) convertView.findViewById(R.id.shop_image);
                holder.shopName = (TextView) convertView.findViewById(R.id.shop_name);
                holder.shopAddress = (TextView) convertView.findViewById(R.id.shop_address);
                holder.btnCall = (LinearLayout) convertView.findViewById(R.id.btn_shop_call);
                holder.btnGetDirection = (LinearLayout) convertView.findViewById(R.id.btn_shop_direction);
                holder.btnViewDetail = (LinearLayout) convertView.findViewById(R.id.btn_shop_info);

                convertView.setTag(holder);
            }
            else
            {
                // View already exists
                // Retrieve existing view using Tag.
                holder = (ViewHolder) convertView.getTag();
            }

            // Get Current Position's ConversationSubject Object
            ShopInfo curConversation = getConversation(position);

            // Update Shop info here
            holder.shopName.setText("My Subscription " + String.valueOf(position));

            return convertView;
        }

        // Create a ViewHolder class
        // Note: This class is used to store the list view cell
        public class ViewHolder
        {
            ImageView shopImage;
            TextView shopName;
            TextView shopAddress;
            LinearLayout btnCall;
            LinearLayout btnGetDirection;
            LinearLayout btnViewDetail;
        }

        // My Purchase invoices List/Map Setter
        public void setMap(SortedMap <Integer, ShopInfo> d)
        {
            data = d;
        }
    }

}
