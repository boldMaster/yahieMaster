package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yippie.android.classes.Category;
import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.List;

public class CategorySelectSecondCategoryFragment extends Fragment
{
    Activity activity;
    Category mainCategory;
    HorizontalScrollView titleContainer;
    Integer stateId;
    Integer areaId;
    Integer categoryId;
    LinearLayout secondCategoryListContainer;
    LinearLayout btnBack;
    List<Category.SubCategory> secondCategoryList;
    Location.State currentState;
    Location.StateArea currentArea;
    TextView title;
    TextView categoryTitle;
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
        categoryId = getArguments().getInt("CategoryId");

        // Get state info
        currentState = Location.State.getStateInfo(stateId);

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.map_area_select_second_category_menu, container, false);

        // Import Assets
        this.secondCategoryListContainer = (LinearLayout) view.findViewById(R.id.secondCategoryList);
        this.titleContainer = (HorizontalScrollView) view.findViewById(R.id.titleContainer);
        this.title = (TextView) view.findViewById(R.id.title);
        this.categoryTitle = (TextView) view.findViewById(R.id.secondCategorySelectText);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);

        // Set shadow for the textview
        this.categoryTitle.setShadowLayer(1,0,0, Color.WHITE);

        // Set click event for back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger activity backpress button
                activity.onBackPressed();
            }
        });

        // Get the category info
        this.mainCategory = Category.getCategoryById(categoryId);
        // Get the list of the state from Location class
        this.secondCategoryList = this.mainCategory.getSubCategoryList();

        // Update fragment title
        this.updateTitle();

        // Loop through the state list in Malaysia and contruct the view
        for(Category.SubCategory subCategory : this.secondCategoryList)
        {
            LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View categoryView = newInflater.inflate(R.layout.map_state_category_item, null);

            // Import single category
            final TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
            // Set the category name into textview
            categoryName.setText(subCategory.getSubCategoryName());
            categoryName.setShadowLayer(1,0,0,Color.BLACK);

            // Get subCategory id
            final Integer subCategoryId = subCategory.getSubCategoryId();

            // Set onclick event
            categoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // The selected category do not have sub category, change to map fragment
                    CategorySelectThirdCategoryFragment mapFragment = new CategorySelectThirdCategoryFragment();
                    // Store state object to args and pass to next fragment
                    Bundle args = new Bundle();
                    args.putInt("StateId",stateId);
                    args.putInt("AreaId",areaId);
                    args.putInt("CategoryId",categoryId);
                    args.putInt("SubCategoryId",subCategoryId);
                    mapFragment.setArguments(args);

                    interfaceObj.addFragment(mapFragment);

                }
            });

            // Add new category into the state container
            this.secondCategoryListContainer.addView(categoryView);
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
        title.setText(currentState.getStateName() + " > " + mainCategory.getCategoryName());
        // Set the horizontal scrollview to always show to right side
        titleContainer.post(new Runnable() {
            @Override
            public void run() {
                titleContainer.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        // Set category title
        categoryTitle.setText(mainCategory.getCategoryName());
    }
}
