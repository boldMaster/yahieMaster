package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yippie.android.classes.Category;
import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.List;

/**
 * Created by Tan on 4/26/2015.
 */
public class MapSelectCategoryFragment extends Fragment
{
    Activity activity;
    LinearLayout categoryListContainer;
    LinearLayout btnBack;
    Location.State currentState;
    Location.StateArea currentArea;
    List<Category> categoryList;
    TextView title;
    Integer stateId;
    Integer areaId;
    ParentFragmentInterface interfaceObj;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Get arguments from previous fragment
        stateId = getArguments().getInt("StateId");
        areaId = getArguments().getInt("AreaId");
        // Get state info
        currentState = Location.State.getStateInfo(stateId);

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.map_area_select_category_menu, container, false);

        // Import Assets
        this.categoryListContainer = (LinearLayout) view.findViewById(R.id.categoryList);
        this.title = (TextView) view.findViewById(R.id.title);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        TextView Label= (TextView) view.findViewById(R.id.categorySelectText);

        // Set shadow for the textview
        Label.setShadowLayer(1,0,0, Color.WHITE);

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
        for(Category category : this.categoryList)
        {
            LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View categoryView = newInflater.inflate(R.layout.map_state_category_item, null);

            // Import single category
            final TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
            // Set the category name into textview
            categoryName.setText(category.getCategoryName());
            categoryName.setShadowLayer(1,0,0,Color.BLACK);

            // Get category id
            final Category _category = category;

            // Set onclick event
            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check if the area category have subCategory list
                    Boolean isContainSubCategory = (!_category.getSubCategoryList().isEmpty());

                    if(isContainSubCategory)
                    {
                        // Add new fragment
                        MapSelectSecondCategoryFragment mapSelectCategory = new MapSelectSecondCategoryFragment();
                        // Store state object to args and pass to next fragment
                        Bundle args = new Bundle();
                        args.putInt("StateId",stateId);
                        args.putInt("AreaId",areaId);
                        args.putInt("CategoryId",_category.getCategoryId());
                        mapSelectCategory.setArguments(args);

                        interfaceObj.addFragment(mapSelectCategory);

                    }
                    else
                    {
                        // The selected category do not have sub category, change to map fragment
                        MapShowFragment mapFragment = new MapShowFragment();
                        // Store state object to args and pass to next fragment
                        Bundle args = new Bundle();
                        args.putInt("StateId",stateId);
                        args.putInt("AreaId",areaId);
                        args.putInt("CategoryId",_category.getCategoryId());
                        mapFragment.setArguments(args);

                        interfaceObj.addFragment(mapFragment);
                    }

                }
            });

            // Add new category into the state container
            this.categoryListContainer.addView(categoryView);
        } // for(Category category : this.categoryList)

        return view;
    }

    @Override
    public void onAttach(Activity act)
    {
        super.onAttach(act);
        activity = act;
        interfaceObj = (ParentFragmentInterface) getParentFragment();
    }

    /**
     * Function to update the title of this fragment
     */
    private void updateTitle()
    {
        // Set state name
        title.setText(currentState.getStateName() + " > " + "Jelutong");
    }
}
