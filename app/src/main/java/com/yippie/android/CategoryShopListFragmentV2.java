package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.CategoryRef;
import com.yippie.android.classes.CriteriaV2;
import com.yippie.android.classes.Location;
import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.ImageHandler;
import com.yippie.android.library.ParentFragmentInterface;
import com.yippie.android.classes.ShopInfo.ShopInfoLite;
import com.yippie.android.library.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zi Yang on 1/9/2016.
 *
 */
public class CategoryShopListFragmentV2 extends Fragment implements HttpRequestInterface, View.OnClickListener, AbsListView.OnScrollListener
{
    protected Activity activity;
    protected HorizontalScrollView titleContainer;
    protected LinearLayout btnBack;
    protected ListView shopList;
    protected List<ShopInfoLite> data;
    protected ParentFragmentInterface interfaceObj;
    protected ProgressDialog pDialog;
    protected ShopListAdapter adapter;
    protected TextView title;

    protected Boolean isFetchRequired = true;
    protected Integer previousTotal = 0;
    protected Integer nextPage = 1;
    protected Integer numberOfItem = 12;
    protected Integer visibleThereHold = 3;

    private static final String BACK_TAG = "back";
    private static final String GET_SHOP_LIST_CALLER = "get_shop_list";

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
        View view = inflater.inflate(R.layout.category_shop_list_fragment_view, container, false);

        // Import Assets
        this.shopList = (ListView) view.findViewById(R.id.categoryShopList);
        this.titleContainer = (HorizontalScrollView) view.findViewById(R.id.titleContainer);
        this.title = (TextView) view.findViewById(R.id.title);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);

        // Set the tag and click event for back button
        this.btnBack.setTag(BACK_TAG);
        this.btnBack.setOnClickListener(this);

        // Update fragment title
        this.updateTitle();

        // Intialize the sorted map data
        this.data = new ArrayList<>();

        // Initialize the shop listview adapter
        this.adapter = new ShopListAdapter(activity,data);
        this.shopList.setAdapter(adapter);

        // Launch get shop list Async Task
        GetCategoryShopList task = new GetCategoryShopList();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    @Override
    public void onAttach(Activity act){
        super.onAttach(act);
        this.activity = act;
        this.interfaceObj = (ParentFragmentInterface) getParentFragment();
    }

    /**
     * Function to update the title of this fragment
     */
    private void updateTitle() {

        // Get Criteria Object from Singleton
        CriteriaV2 currentCriteria = CriteriaV2.getInstance();

        // We will set the title based on the criteria
        // Find out the selected state
        int stateId = currentCriteria.getSelectedState();
        // Get the state name
        String newTitle = Location.State.getStateInfo(stateId).getStateName();

        // Next check if the category group is set
        Integer categoryGroupId = currentCriteria.getCategoryGroup();
        if(categoryGroupId != 0)
        {
            CategoryRef.CategoryGroup categoryGroup = CategoryRef.CategoryGroup.getCategoryGroupInfo(categoryGroupId);
            newTitle = newTitle + " > " +  categoryGroup.categoryGroupName;

            // Check if the category is selected
            Integer categoryId = currentCriteria.getCategory();
            if(categoryId != 0)
            {
                CategoryRef.Category category = CategoryRef.Category.getCategoryInfo(categoryGroupId, categoryId);
                newTitle = newTitle + " > " +  category.categoryName;

                // Check if sub category is selected
                Integer subCategoryId = currentCriteria.getSubCategory();
                if(subCategoryId != 0)
                {
                    CategoryRef.SubCategory subCategory = CategoryRef.SubCategory.getSubCategoryInfo(subCategoryId);
                    newTitle = newTitle + " > " + subCategory.subCategoryName;
                }
            }
        }

        // Set new Title
        title.setText(newTitle);

        // Set the horizontal scrollview to always show to right side
        this.titleContainer.post(new Runnable() {
            @Override
            public void run() {
                titleContainer.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    @Override
    public void HttpRequestPreExecuteDelegate() {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing progress dialog before perform the HTTP REQUEST
                pDialog = new ProgressDialog(activity);
                pDialog.setMessage("Getting shop list.. Please wait..");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        });
    }

    @Override
    public void HttpRequestDoInBackgroundDelegate(HttpReturnObject response) {}

    @Override
    public void HttpRequestDoInBackgroundErrorDelegate(HttpReturnObject response) {}

    @Override
    public void HttpRequestProgressUpdateDelegate(HttpReturnObject response) {}

    @Override
    public void HttpRequestOnPostExecuteUpdateDelegate(HttpReturnObject response) {
        try
        {
            // Extract the result from the
            JSONObject responseJson = response.getJSON();
            // Get Status code from returned JSON
            Integer statusCode = responseJson.getInt("status_code");

            switch(statusCode)
            {
                case 200:
                {
                    // Enter here if status code is 200 (Success)
                    // Extract the return_vars
                    JSONObject returnVars = responseJson.getJSONObject("return_vars");
                    // Extract resutl from the return vars
                    Boolean is_valid = returnVars.getBoolean("status");

                    if(is_valid)
                    {
                        // Enter here if the request is valid and status is true
                        // Get caller name
                        String caller = responseJson.getString("caller");

                        switch(caller) {
                            // Get shop list request
                            case GET_SHOP_LIST_CALLER:
                            {
                                // Get the list
                                JSONArray filteredShopList = returnVars.getJSONArray("place_list");

                                // Loop through the shop list
                                for(int index=0; index < filteredShopList.length(); index++) {
                                    // Get current shop info
                                    JSONObject currentShopInfo = filteredShopList.getJSONObject(index);

                                    // Extract single shop information
                                    Integer shopId = currentShopInfo.getInt("place_id");
                                    String shopImageUrl = currentShopInfo.getString("picture_path");
                                    String shopTitle = currentShopInfo.getString("place_title");
                                    String shopAddress = currentShopInfo.getString("place_address");
                                    String shopContact = currentShopInfo.getString("contact");
                                    String shopMapLongitude = currentShopInfo.getString("map_longitude");
                                    String shopMapLatitude = currentShopInfo.getString("map_latitude");

                                    shopImageUrl = Utility.getBaseUrl() + shopImageUrl;

                                    // Construct new shop info object
                                    ShopInfoLite newShopInfoObj = new ShopInfoLite(shopId,shopImageUrl,shopTitle,shopAddress,shopContact,shopMapLongitude,shopMapLatitude);
                                        // Store the new obj into list
                                    data.add(newShopInfoObj);
                                }

                                // After that, we need to update the list
                                // Note: the update function will notify the changes, so there is no need to repeat again
                                this.adapter.update(data);

                                // If data is empty, means there are no shop available under this category
                                // Hide the listview and show the no shop found message
                                if(data.isEmpty()) {
                                    // Hide the listview and show the empty message
                                }




                                break;
                            }
                        }
                    }
                    else
                    {
                        // Display error in this section
                        JSONArray errorList = returnVars.getJSONArray("errors");
                        List<String> errMsgList = new ArrayList<>();

                        for(Integer i = 0; i < errorList.length(); i++) {
                            // Get the err msg and store into array list
                            errMsgList.add(errorList.getString(i));
                        }

                        // Join the error message with \n
                        String errMsg = TextUtils.join("\n", errMsgList);

                        // Show error message by usign Toast
                        Toast.makeText(activity, errMsg,Toast.LENGTH_LONG).show();
                    }


                    break;
                }

                case 403:
                default:
                    // Enter here if the status code is 403 (unauthorized)
                    Toast.makeText(activity, "You do not have permission to perform this action.", Toast.LENGTH_LONG).show();
                    break;
            }//switch(statusCode)

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }//try

        // Finally, dismiss the progress dialog
        pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        // Get the view tag from view
        String tag = (String) v.getTag();

        // We will handle the button click action based on tag
        switch(tag){
            case BACK_TAG: {
                this.clearCriteriaSelection();
                this.interfaceObj.switchScreen(true,0);
                break;
            }
        }//switch(tag)
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isFetchRequired)
        {
            if (totalItemCount > previousTotal)
            {
                isFetchRequired = false;
                previousTotal = totalItemCount;
            }
        }

        if (!isFetchRequired && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThereHold))
        {
            // Calculate start and end offset
            // Formula: Start = Total Item Count. (Due to offset is start from 0 instead of 1)
            //			End = Total Item Count + number of picked item - 1 (Same as above.)
            Integer start = totalItemCount;
            Integer end = totalItemCount + numberOfItem - 1;
            GetCategoryShopList task = new GetCategoryShopList();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            isFetchRequired = true;
        }
    }

    /**
     * Custom Adapter for Category Shop List ListView
     */
    private class ShopListAdapter extends BaseAdapter
    {
        private Activity activity;
        private List<ShopInfoLite> data;
        private LayoutInflater inflater;
        private ImageHandler imageHandler;
        private Drawable defaultImage;

        /**
         * Constructor
         */
        public ShopListAdapter(Activity activity, List<ShopInfoLite> data)
        {
            this.activity = activity;
            this.data = data;
            this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.imageHandler = new ImageHandler(this.activity, ImageHandler.ScaleType.PLACE_IMAGE);
            this.defaultImage = new ColorDrawable(Color.GRAY);
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
        public ShopInfoLite getShopInfo(int position)
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
                convertView = inflater.inflate(R.layout.shop_list_item_v2, parent, false);
                holder = new ViewHolder();
                // Import Asset
                holder.shopImage = (ImageView) convertView.findViewById(R.id.shop_image);
                holder.shopName = (TextView) convertView.findViewById(R.id.shop_name);
                holder.shopAddress = (TextView) convertView.findViewById(R.id.shop_address);
                holder.btnCall = (LinearLayout) convertView.findViewById(R.id.btn_shop_call);
                holder.btnDirection = (LinearLayout) convertView.findViewById(R.id.btn_shop_direction);
                holder.btnDetail = (LinearLayout) convertView.findViewById(R.id.btn_shop_info);

                convertView.setTag(holder);
            }
            else
            {
                // View already exists
                // Retrieve existing view using Tag.
                holder = (ViewHolder) convertView.getTag();
            }

            // Get Current Position's Shop Info Object
            ShopInfoLite currentShopInfo = getShopInfo(position);
            // Extract the invoice information and import to asset
            String imgLink = currentShopInfo.shopImageUrl!= null ? currentShopInfo.shopImageUrl : "";
            this.imageHandler.displayImage(imgLink, holder.shopImage, this.defaultImage);

            // Set shop title
            String shopTitle = currentShopInfo.shopTitle!= null ? currentShopInfo.shopTitle : "";
            holder.shopName.setText(shopTitle);

            // Set shop address
            String shopAddress = currentShopInfo.shopAddressFull!= null ? currentShopInfo.shopAddressFull : "";
            holder.shopAddress.setText(shopAddress);

            // Set store button action
            final Integer shopId = currentShopInfo.shopId;
            final String shopContact = currentShopInfo.shopContact!= null ? currentShopInfo.shopContact : "";
            final String shopMapLogitude = currentShopInfo.shopMapLongitude!= null ? currentShopInfo.shopMapLongitude : "";
            final String shopMapLatitude = currentShopInfo.shopMapLatitude!= null ? currentShopInfo.shopMapLatitude : "";

            // Set call button action
            holder.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Set contact format
                    String contact = "tel:" + shopContact;
                    // Start Dial Activity
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(contact));
                    startActivity(intent);

                    // Note: DO NOT FINISH THE ACTIVITY
                }
            });

            // Set direction button action
            holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO Add Coordinate related function
                    Toast.makeText(activity, "Longitude: " + shopMapLogitude + "\nLatitude: " + shopMapLatitude, Toast.LENGTH_SHORT).show();
                }
            });

            // Set shop info button action
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start shop info activity
                    Intent intent = new Intent(activity.getApplicationContext(), ShopInfoActivity.class);
                    intent.putExtra("shopId",shopId);
                    startActivity(intent);

                    // Note: DO NOT FINISH THE ACTIVITY
                }
            });

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
            LinearLayout btnDirection;
            LinearLayout btnDetail;
        }

        /**
         * Function to set the list
         */
        public void update(List<ShopInfoLite> d) {
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

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Get the shop list available for selected category
     */
    public class GetCategoryShopList extends AsyncTask<Void, Void, Void>
    {
        String baseURL;
        String criteria;

        /**
         * Constructor
         */
        public GetCategoryShopList()
        {
            this.baseURL = Utility.getBaseUrl();
            // Get JSON encoded criteria
            this.criteria = CriteriaV2.getEncoded();
        }

        @Override
        protected Void doInBackground(Void...voids)
        {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));
            params.add(new BasicNameValuePair("caller", GET_SHOP_LIST_CALLER));
            params.add(new BasicNameValuePair("criteria", this.criteria));
            params.add(new BasicNameValuePair("page", nextPage.toString()));
            params.add(new BasicNameValuePair("num", numberOfItem.toString()));

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(CategoryShopListFragmentV2.this);
            String httpURL = baseURL +"laravel/public/android/v1/place/filter";

            // Execute Get Category AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }

    /**
     * Function to chandle the reset the selected criteria when user click on the back button
     */
    private void clearCriteriaSelection() {
        // Get current criteria
        CriteriaV2 currentCriteria = CriteriaV2.getInstance();

        // We will reset the category in this order, sub category -> category -> category group
        if(currentCriteria.getSubCategory()!=0){
            // Reset sub category
            CriteriaV2.getInstance().resetSubCategory();
        }
        else if(currentCriteria.getCategory()!=0){
            CriteriaV2.getInstance().resetCategory();
        }
        else {
            CriteriaV2.getInstance().resetCategoryGroup();
        }
    }
}
