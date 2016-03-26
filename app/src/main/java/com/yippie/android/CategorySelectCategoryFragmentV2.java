package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.CategoryRef;
import com.yippie.android.classes.CriteriaV2;
import com.yippie.android.classes.Location;
import com.yippie.android.classes.Operation;
import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.ParentFragmentInterface;
import com.yippie.android.library.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zi Yang
 *
 */
public class CategorySelectCategoryFragmentV2 extends Fragment implements View.OnClickListener, HttpRequestInterface
{
    protected Activity activity;
    protected CategoryListAdapter adapter;
    protected List<CategoryRef.CategoryGroup> categoryGroupList;
    protected List<CategoryRef.Category> categoryList;
    protected List<CategoryRef.SubCategory> subCategoryList;
    protected ListView categoryListView;
    protected LinearLayout btnBack;
    protected ProgressDialog pDialog;
    protected TextView title;
    protected TextView categoryTitle;
    protected ParentFragmentInterface interfaceObj;

    public Integer CATEGORY_GROUP = 1;
    public Integer CATEGORY = 2;
    public Integer SUB_CATEGORY = 3;
    public Integer CURRENT_TYPE = 0;

    private static final String BACK_TAG = "back";
    private static final String GET_CATEGORY_LIST = "get_category_list";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.map_area_select_category_menu, container, false);

        // Import Assets
        this.categoryListView = (ListView) view.findViewById(R.id.categoryListView);
        this.title = (TextView) view.findViewById(R.id.title);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        this.categoryTitle = (TextView) view.findViewById(R.id.categorySelectText);

        // Set shadow for the textview
        this.categoryTitle.setShadowLayer(1, 0, 0, Color.WHITE);

        // Set the tag and click event for back button
        this.btnBack.setTag(BACK_TAG);
        this.btnBack.setOnClickListener(this);

        // Update fragment title
        this.updateTitle();

        // Launch update content asynctask
        RefreshCategoryList task = new RefreshCategoryList();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    @Override
    public void onAttach(Activity act) {
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
            }
        }

        // Set new Title
        title.setText(newTitle);
    }

    @Override
    public void onClick(View v) {
        // Get the view tag from view
        String tag = (String) v.getTag();

        // We will handle the button click action based on tag
        switch(tag){
            case BACK_TAG: {
                // Clear the criteria before switch screen
                this.clearCriteriaSelection();
                interfaceObj.switchScreen(true,0);
                break;
            }
        }//switch(tag)
    }

    @Override
    public void HttpRequestPreExecuteDelegate() {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Showing progress dialog before perform the HTTP REQUEST
                pDialog = new ProgressDialog(activity);
                pDialog.setMessage("Updating category list.. Please wait..");
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
                    // Extract the return_vars
                    JSONObject returnVars = responseJson.getJSONObject("return_vars");

                    // Get status
                    Boolean status = returnVars.getBoolean("status");
                    if(status)
                    {
                        // Enter here if the request is valid
                        JSONArray json_categoryGroupList = returnVars.getJSONArray("category_group_list");
                        JSONArray json_categoryList = returnVars.getJSONArray("category_list");
                        JSONArray json_subCategoryList = returnVars.getJSONArray("sub_category_list");

                        // These list will store the list of category
                        CategoryLists newListObj = new CategoryLists();
                        newListObj.categoryGroupList = new ArrayList<>();
                        newListObj.categoryList = new ArrayList<>();
                        newListObj.subCategoryList = new ArrayList<>();

                        // Loop through the JSONArray, and create corresponding object and store into List
                        for(int a=0;a<json_categoryGroupList.length();a++)
                        {
                            // Get current category group object
                            JSONObject currentCategoryGroup = json_categoryGroupList.getJSONObject(a);
                            // Extract the category group info
                            Integer categoryGroupId = currentCategoryGroup.getInt("category_group_id");
                            String categoryGroupName = currentCategoryGroup.getString("category_group_desc");

                            // Construct the Category Group Object
                            CategoryRef.CategoryGroup newObj = new CategoryRef.CategoryGroup(categoryGroupId,categoryGroupName);
                            newListObj.categoryGroupList.add(newObj);
                        }//for(int a=0;a<json_categoryGroupList.length();a++)

                        for(int a=0;a<json_categoryList.length();a++)
                        {
                            // Get current category object
                            JSONObject currentCategory = json_categoryList.getJSONObject(a);
                            // Extract the category info
                            Integer categoryGroupId = currentCategory.getInt("category_group_id");
                            Integer categoryId = currentCategory.getInt("category_id");
                            String categoryName = currentCategory.getString("category_desc");

                            // Construct the Category Group Object
                            CategoryRef.Category newObj = new CategoryRef.Category(categoryId,categoryGroupId,categoryName);
                            newListObj.categoryList.add(newObj);
                        }//for(int a=0;a<json_categoryList.length();a++)

                        for(int a=0;a<json_subCategoryList.length();a++)
                        {
                            // Get current category object
                            JSONObject currentSubCategory = json_subCategoryList.getJSONObject(a);
                            // Extract the category info
                            Integer categoryGroupId = currentSubCategory.getInt("category_group_id");
                            Integer categoryId = currentSubCategory.getInt("category_id");
                            Integer subCategoryId = currentSubCategory.getInt("sub_category_id");
                            String subCategoryName = currentSubCategory.getString("sub_category_desc");

                            // Construct the Category Group Object
                            CategoryRef.SubCategory newObj = new CategoryRef.SubCategory(subCategoryId,categoryId,categoryGroupId,subCategoryName);
                            newListObj.subCategoryList.add(newObj);
                        }//for(int a=0;a<json_categoryGroupList.length();a++)

                        // Store into database
                        CategoryRef.CategoryGroup.bulkInsertORRupdateCategoryGroup(newListObj.categoryGroupList);
                        CategoryRef.Category.bulkInsertORRupdateCategory(newListObj.categoryList);
                        CategoryRef.SubCategory.bulkInsertORRupdateSubCategory(newListObj.subCategoryList);

                        // Update the TTL timestamp
                        Operation.registerOperationTimestamp(Operation.CATEGORY_LIST);

                        // Update the list
                        this.updateContent(newListObj);
                    }

                    break;

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

        // Dismiss the Progress Dialog
        this.pDialog.dismiss();
    }


    /**
     * @author  Written by Zi Yang
     * This is a custom listview adapter
     */
    private class CategoryListAdapter extends BaseAdapter
    {
        private Activity activity;
        private List<CategoryRef.CategoryGroup> categoryGroupList = new ArrayList<>();
        private List<CategoryRef.Category> categoryList = new ArrayList<>();
        private List<CategoryRef.SubCategory> subCategoryList = new ArrayList<>();
        private LayoutInflater inflater;
        public Integer type;

        /**
         * Constructor For Category Group List
         */
        public CategoryListAdapter(Activity activity, List<CategoryRef.CategoryGroup> categoryGroupList, List<CategoryRef.Category> categoryList, List<CategoryRef.SubCategory> subCategoryList, Integer type) {
            this.activity = activity;
            this.type = type;
            this.inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(categoryGroupList != null) {
                this.categoryGroupList = categoryGroupList;
            }
            if(categoryList != null) {
                this.categoryList = categoryList;
            }
            if(subCategoryList != null) {
                this.subCategoryList = subCategoryList;
            }
        }


        @Override
        // Basic function of BaseAdapater. Used to return the size of Array, Map of data
        public int getCount() {
            // Set default size to 0
            int size = 0;
            // We will get the size of the list based on the type
            if (type.equals(CATEGORY_GROUP)) {
                size = categoryGroupList.size();
            } else if (type.equals(CATEGORY)) {
                size = categoryList.size();
            } else if (type.equals(SUB_CATEGORY)) {
                size = subCategoryList.size();
            }

            return size;
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

        // Custom BaseAdapter function. This will return the categoiry group object of that position
        public CategoryRef.CategoryGroup getCategoryGroup(int position) {
            return categoryGroupList.get(position);
        }

        // Custom BaseAdapter function. This will return the category object of that position
        public CategoryRef.Category getCategory(int position) {
            return categoryList.get(position);
        }

        // Custom BaseAdapter function. This will return the sub category object of that position
        public CategoryRef.SubCategory getSubCategory(int position) {
            return subCategoryList.get(position);
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
                convertView = inflater.inflate(R.layout.map_state_category_item, parent, false);
                holder = new ViewHolder();
                // Import Asset
                holder.cateogoryName = (TextView) convertView.findViewById(R.id.categoryName);

                convertView.setTag(holder);
            }
            else
            {
                // View already exists
                // Retrieve existing view using Tag.
                holder = (ViewHolder) convertView.getTag();
            }

            // We will update the category name based on the type set on this adapter
            if(type.equals(CATEGORY_GROUP))
            {
                // Get current category group object,, we will need to get the category name
                CategoryRef.CategoryGroup categoryGroup = getCategoryGroup(position);
                String categoryGroupName = categoryGroup.categoryGroupName;
                holder.cateogoryName.setText(categoryGroupName);
            }
            else if (type.equals(CATEGORY))
            {
                // Get current category group object,, we will need to get the category name
                CategoryRef.Category category = getCategory(position);
                String categoryName = category.categoryName;
                holder.cateogoryName.setText(categoryName);
            }
            else if (type.equals(SUB_CATEGORY))
            {
                // Get current category group object,, we will need to get the category name
                CategoryRef.SubCategory subCategory = getSubCategory(position);
                String subCategoryname = subCategory.subCategoryName;
                holder.cateogoryName.setText(subCategoryname);
            }

            // Set shadow for the state name
            holder.cateogoryName.setShadowLayer(1,0,0, Color.BLACK);

            return convertView;
        }

        // Create a ViewHolder class
        // Note: This class is used to store the list view cell
        public class ViewHolder {
            TextView cateogoryName;
        }

        /**
         * Function to clear the list
         */
        public void clearList() {
            this.categoryGroupList.clear();
            this.categoryList.clear();
            this.subCategoryList.clear();
            this.notifyDataSetChanged();
        }


    }

    /**
     * Function to chandle the reset the selected criteria when user click on the back button
     */
    private void clearCriteriaSelection() {

        // We will clear the selection based on the type
        if(CURRENT_TYPE.equals(SUB_CATEGORY))
        {
            // Reset Category and sub category, then we will back to category list
            CriteriaV2.getInstance().resetCategory();
            CriteriaV2.getInstance().resetSubCategory();
        }
        else if(CURRENT_TYPE.equals(CATEGORY))
        {
            // We will reset all category, in order to back to Category Group List
            CriteriaV2.getInstance().resetCategoryGroup();
            CriteriaV2.getInstance().resetCategory();
            CriteriaV2.getInstance().resetSubCategory();
        }
        else if(CURRENT_TYPE.equals(CATEGORY_GROUP))
        {
            // Reset all category and state
            CriteriaV2.getInstance().resetState();
            CriteriaV2.getInstance().resetCategoryGroup();
            CriteriaV2.getInstance().resetCategory();
            CriteriaV2.getInstance().resetSubCategory();
        }
    }

    private class CategoryLists {
        public List<CategoryRef.CategoryGroup> categoryGroupList;
        public List<CategoryRef.Category> categoryList;
        public List<CategoryRef.SubCategory> subCategoryList;
    }

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Get the data from server
     */
    public class RefreshCategoryList extends AsyncTask<Void, Void, CategoryLists>
    {
        String baseUrl;
        Integer operation;

        /**
         * Constructor
         */
        public RefreshCategoryList()
        {
            this.baseUrl = Utility.getBaseUrl();
            this.operation = Operation.CATEGORY_LIST;
        }

        @Override
        protected CategoryLists doInBackground(Void...voids)
        {
            CategoryLists categoryLists = new CategoryLists();

            // Check if update category is required
            Boolean isUpdateRequired = Operation.isUpdateRequire(operation);
            if(isUpdateRequired)
            {
                // Launch update category list asynctask
                // Construct parameter to be sent over HTTP Request
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));
                params.add(new BasicNameValuePair("caller", GET_CATEGORY_LIST));

                // Trigger AsyncTask
                HttpRequestController httpClient = new HttpRequestController(CategorySelectCategoryFragmentV2.this);
                String httpURL = baseUrl +"laravel/public/ref/getcategorylist";

                // Execute Get Httprequest
                httpClient.performAsynRequest(httpURL, params);
            }
            else
            {
                // Get the category list from database
                // Get Criteria Object from Singleton
                CriteriaV2 currentCriteria = CriteriaV2.getInstance();

                // Get all selected category
                Integer categoryGroupId = currentCriteria.getCategoryGroup();
                Integer categoryId = currentCriteria.getCategory();

                // Get the list of category group
                categoryLists.categoryGroupList = CategoryRef.CategoryGroup.getCategoryGroupList();
                categoryLists.categoryList = CategoryRef.Category.getCategoryList(categoryGroupId);
                categoryLists.subCategoryList = CategoryRef.SubCategory.getSubCategoryList(categoryId);
            }

            return categoryLists;
        }

        @Override
        protected void onPostExecute(CategoryLists categoryLists)
        {
            // Update the category list content
            updateContent(categoryLists);
        }
    }

    /**
     * Fucntion to construct the category list to display
     */
    private void updateContent(CategoryLists categoryLists) {
        // We will handle the category list in here
        CriteriaV2 currentCriteria = CriteriaV2.getInstance();

        // Get all selected category
        Integer categoryGroupId = currentCriteria.getCategoryGroup();
        Integer categoryId = currentCriteria.getCategory();

        // We will construct the content with this priority order
        // Sub Category > Category > CategoryGroup
        if(categoryId != 0)
        {
            // Enter here if the category id is set, then we will need to show the list of the sub category available in this category
            if(categoryLists.subCategoryList != null && !categoryLists.subCategoryList.isEmpty()) {
                // Get category name
                String categoryName = CategoryRef.Category.getCategoryInfo(categoryGroupId,categoryId).categoryName;
                this.categoryTitle.setText(categoryName);
                this.subCategoryList = categoryLists.subCategoryList;

                // Set type
                this.CURRENT_TYPE = SUB_CATEGORY;

                // Set new adapter and update the category list
                this.adapter = new CategoryListAdapter(activity, null, null, subCategoryList, SUB_CATEGORY);
                this.categoryListView.setAdapter(adapter);
                this.categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        // Get current position's state
                        CategoryRef.SubCategory subCategory = subCategoryList.get(position);
                        // Get the category Id, and set into the CriteriaV2 object
                        Integer subCategoryId = subCategory.subCategoryId;
                        CriteriaV2.getInstance().setSubCategory(subCategoryId);

                        interfaceObj.switchScreen(false,CategoryListFragmentV2.GOTO_SHOPLIST);
                    }
                });

                return;
            }
            else
            {
                // There are no sub category list available, then we will redirect user to Shop List Fragment
                this.interfaceObj.switchScreen(false,CategoryListFragmentV2.GOTO_SHOPLIST);
            }
        }

        if(categoryGroupId != 0)
        {
            // Enter here if the category id is set, then we will need to show the list of the sub category available in this category
            if(categoryLists.categoryList != null && !categoryLists.categoryList.isEmpty()) {
                // Get category name
                String categoryGroupName = CategoryRef.CategoryGroup.getCategoryGroupInfo(categoryGroupId).categoryGroupName;
                this.categoryTitle.setText(categoryGroupName);
                this.categoryList = categoryLists.categoryList;

                // Set type
                this.CURRENT_TYPE = CATEGORY;

                // Set new adapter and update the category list
                this.adapter = new CategoryListAdapter(activity, null, categoryList, null, CATEGORY);
                this.categoryListView.setAdapter(adapter);
                this.categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        // Get current position's state
                        CategoryRef.Category category = categoryList.get(position);
                        // Get the category Id, and set into the CriteriaV2 object
                        Integer categoryId = category.categoryId;
                        CriteriaV2.getInstance().setCategory(categoryId);

                        interfaceObj.switchScreen(false,0);
                    }
                });

                return;
            }
            else
            {
                // There are no category list available, then we will redirect user to Shop List Fragment
                this.interfaceObj.switchScreen(false,CategoryListFragmentV2.GOTO_SHOPLIST);
            }
        }

        // If we will managed to reach here, means the either the list is empty
        // Set the category title
        String title = "Please select a Category";
        this.categoryTitle.setText(title);
        this.categoryGroupList = categoryLists.categoryGroupList;

        // Set type
        this.CURRENT_TYPE = CATEGORY_GROUP;

        // Set new adapter and update the category list
        adapter = new CategoryListAdapter(activity, categoryGroupList, null, null, CATEGORY_GROUP);
        categoryListView.setAdapter(adapter);
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Get current position's state
                CategoryRef.CategoryGroup categoryGroup = categoryGroupList.get(position);
                // Get the category Id, and set into the CriteriaV2 object
                Integer categoryGroupId = categoryGroup.categoryGroupId;
                CriteriaV2.getInstance().setCategoryGroup(categoryGroupId);

                // Call the parent fragment to switch the screen
                interfaceObj.switchScreen(false,0);
            }
        });
    }
}
