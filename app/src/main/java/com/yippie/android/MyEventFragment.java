package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yippie.android.classes.Event;
import com.yippie.android.classes.ParticipantEvent;
import com.yippie.android.classes.ShopInfo;
import com.yippie.android.classes.User;
import com.yippie.android.library.Utility;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Ming on 3/23/2016.
 */
public class MyEventFragment  extends Fragment{
    protected Activity activity;
    protected ListView EventList;
    protected EventListAdapter adapter;
    protected SortedMap<Integer,Event> eventList = new TreeMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.my_event, container, false);
// Import assets
        this.EventList = (ListView) view.findViewById(R.id.eventList);

        // Setup adapter

        this.adapter = new EventListAdapter(activity, eventList);
        // Set adapter to the List View
        this.EventList.setAdapter(adapter);

        // CODE HERE !!!!!!!!!!!!!!! DO GET the EVENT LIST FROM DB, then create new index with event tie with it.
        // Get all Participiant Event
        ParticipantEvent pe = new ParticipantEvent();
        ArrayList<Event> peList = new ArrayList<Event>();
        peList = pe.getAllParticipantEvent();
        for(int i = 0; i < peList.size() ;i++)
        {
            this.eventList.put(i, peList.get(i));
        }
        this.adapter.setMap(eventList);
        this.adapter.notifyDataSetChanged();

        return view;
    }

    /**
     * Custom Adapter for ListView
     */
    private class EventListAdapter extends BaseAdapter
    {
        private Activity activity;
        private SortedMap<Integer,Event> data;
        private LayoutInflater inflater;

        /**
         * Constructor
         */
        public EventListAdapter(Activity activity, SortedMap<Integer,Event> data)
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
        public Event getEventInfo(int position)
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
                convertView = inflater.inflate(R.layout.event_list_item, parent, false);
                holder = new ViewHolder();
                // Import Asset
                holder.eventImage = (ImageView) convertView.findViewById(R.id.event_image);
                holder.eventName = (TextView) convertView.findViewById(R.id.event_name);
                holder.eventDesc = (TextView) convertView.findViewById(R.id.event_desc);
                holder.eventExpireDate = (TextView) convertView.findViewById(R.id.event_expire_date);
                holder.btnGetDirection = (LinearLayout) convertView.findViewById(R.id.btn_event_direction);
                holder.btnRedeem = (LinearLayout) convertView.findViewById(R.id.btn_redeem);

                convertView.setTag(holder);
            }
            else
            {
                // View already exists
                // Retrieve existing view using Tag.
                holder = (ViewHolder) convertView.getTag();
            }

            // Get Current Position's ConversationSubject Object
            Event event = getEventInfo(position);


            // Update Shop info here
            holder.eventName.setText(String.valueOf(position));

            // Set click event
            holder.btnGetDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String phoneNumber = "0124358790";
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    activity.startActivity(intent);
                }
            });

            holder.btnRedeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Note: DO NOT FINISH THE ACTIVITY
                    Intent intent = new Intent(activity.getApplicationContext(), ShopInfoActivity.class);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        // Create a ViewHolder class
        // Note: This class is used to store the list view cell
        public class ViewHolder
        {
            ImageView eventImage;
            TextView eventName;
            TextView eventDesc;
            TextView eventExpireDate;
            LinearLayout btnGetDirection;
            LinearLayout btnRedeem;
        }

        // My Purchase invoices List/Map Setter
        public void setMap(SortedMap <Integer, Event> d)
        {
            data = d;
        }
    }
}
