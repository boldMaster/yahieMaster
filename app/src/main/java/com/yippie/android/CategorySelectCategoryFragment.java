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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.Category;
import com.yippie.android.classes.Location;
import com.yippie.android.library.Criteria;
import com.yippie.android.library.ParentFragmentInterface;

import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tan on 4/26/2015.
 */
public class CategorySelectCategoryFragment extends Fragment implements HttpRequestInterface {
    Activity activity;
    LinearLayout categoryListContainer;
    LinearLayout btnBack;
    Location.State currentState;
    List<Category> categoryList;
    TextView title;
    Integer stateId;
    ParentFragmentInterface interfaceObj;

    protected static String CAL_FILTER_PLACE = "cal_filter_place";
    protected static String API_FILTER_PLACE = "android/v1/place/filter";
    protected ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get arguments from previous fragment
        stateId = getArguments().getInt("StateId");
        // Get state info
        currentState = Location.State.getStateInfo(stateId);

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.map_area_select_category_menu, container, false);

        // Import Assets
        this.categoryListContainer = (LinearLayout) view.findViewById(R.id.categoryList);
        this.title = (TextView) view.findViewById(R.id.title);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        TextView Label = (TextView) view.findViewById(R.id.categorySelectText);
        // Get Set Criteria from Criteria Class

        Criteria objCriteria = Criteria.initialize();
        //Testing to display Criteria
        Integer testState = objCriteria.getCriteria(Criteria.LOCATION);
        this.title.setText("" + testState);

        // Set shadow for the textview
        Label.setShadowLayer(1, 0, 0, Color.WHITE);

        // Set click event for back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger activity backpress button
                activity.onBackPressed();
            }
        });

        // Update fragment title
        this.updateTitle();

        // Get the list of the state from Location class
        this.categoryList = Category.getCategoryList();

        // Loop through the state list in Malaysia and contruct the view
        for (Category category : this.categoryList) {
            LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View categoryView = newInflater.inflate(R.layout.map_state_category_item, null);

            // Import single category
            final TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
            // Set the category name into textview
            categoryName.setText(category.getCategoryName());
            categoryName.setShadowLayer(1, 0, 0, Color.BLACK);

            // Get category id
            final Category _category = category;

            // Set onclick event
            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check if the area category have subCategory list
                    Boolean isContainSubCategory = (!_category.getSubCategoryList().isEmpty());
                    Criteria objCriteria = Criteria.initialize();
                    if (isContainSubCategory) {
                        // Add new fragment
                        CategorySelectSecondCategoryFragment mapSelectCategory = new CategorySelectSecondCategoryFragment();
                        // Store state object to args and pass to next fragment
                        Bundle args = new Bundle();
                        args.putInt("StateId", stateId);
                        args.putInt("CategoryId", _category.getCategoryId());
                        // Set selected state to criteria
                        objCriteria.setCriteria(Criteria.CATEGORY_GROUP, _category.getCategoryId());
                        mapSelectCategory.setArguments(args);

                        interfaceObj.addFragment(mapSelectCategory);

                    } else {
                        // Get all criteria
                        JSONObject jsonCriteria = objCriteria.getAllCriteriaInJson();
                        FilterPlace task = new FilterPlace(jsonCriteria);
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                }
            });

            // Add new category into the state container
            this.categoryListContainer.addView(categoryView);
        } // for(Category category : this.categoryList)

        return view;
    }

    @Override
    public void onAttach(Activity act) {
        super.onAttach(act);
        activity = act;
        interfaceObj = (ParentFragmentInterface) getParentFragment();
    }

    /**
     * Function to update the title of this fragment
     */
    private void updateTitle() {
        // Set state name
        title.setText(currentState.getStateName());
    }

    @Override
    public void HttpRequestPreExecuteDelegate() {

    }

    @Override
    public void HttpRequestDoInBackgroundDelegate(HttpReturnObject response) {
    }

    @Override
    public void HttpRequestDoInBackgroundErrorDelegate(HttpReturnObject response) {
    }

    @Override
    public void HttpRequestProgressUpdateDelegate(HttpReturnObject response) {
    }

    @Override
    public void HttpRequestOnPostExecuteUpdateDelegate(HttpReturnObject response) {
        try {
            // Extract the result from the
            JSONObject responseJson = response.getJSON();
            // Get Status code from returned JSON
            Integer statusCode = responseJson.getInt("status_code");
            // Extract the return_vars
            switch (statusCode) {
                case 200: {
                    // Enter here if status code is 200 (Success)
                    // Extract resutl from the return vars
                    Boolean is_valid = responseJson.getBoolean("status");

                    if (is_valid) {
                        // Enter here if the request is valid and status is true
                        // Get caller name
                        String caller = responseJson.getString("caller");

                        // Sign in request
                        Boolean status = responseJson.getBoolean("success");
                        if (status) {
                            // Sandbox testing usage

                        } else {
                            // Display error in this section
                            String error = responseJson.getString("error");
                            // Show error message by usign Toast\
                            Toast.makeText(this.activity, R.string.error_email_not_exist, Toast.LENGTH_LONG).show();
                        }
                        break;
                    } else {
                        // Display error in this section
                        String error = responseJson.getString("error");

                        // Show error message by usign Toast
                        Toast.makeText(this.activity, R.string.error_email_validation_failed, Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 422: {
                    // Enter here if the status code is 403 (unauthorized)
                    // Display error in this section
                    String error = responseJson.getString("error");

                    Toast.makeText(this.activity, R.string.error_email_validation_failed, Toast.LENGTH_LONG).show();
                    break;
                }
                case 403:
                default: {
                    // Enter here if the status code is 403 (unauthorized)
                    // Display error in this section

                    // Enter here if the status code is 403 (unauthorized)
                    Toast.makeText(this.activity, R.string.error_access_denied, Toast.LENGTH_LONG).show();
                    break;
                }
            }//switch(statusCode)

        } catch (JSONException e) {
            e.printStackTrace();
        }//try

        // Dismiss the Progress Dialog if not null
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Validate user crdential at server
     */
    public class FilterPlace extends AsyncTask<Void, Void, Void> {
        //    String baseUrl = "localhost";
        String baseUrl = "192.168.0.101";
        //    String baseUrl = "bee.honeyspear.com";
        JSONObject criteria;

        /**
         * Constructor
         *
         * @param inputCriteria - The Criteria set by the user
         */
        public FilterPlace(JSONObject inputCriteria) {
            this.criteria = inputCriteria;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));    // API Key
            params.add(new BasicNameValuePair("caller", CAL_FILTER_PLACE));                // Caller
            params.add(new BasicNameValuePair("criteria", this.criteria.toString()));       // Criteria

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(CategorySelectCategoryFragment.this);
            String httpURL = "http://" + baseUrl + "/flink/public/" + API_FILTER_PLACE;
            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }
}
