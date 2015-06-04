package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yippie.android.classes.Category;
import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.ArrayList;
import java.util.List;

public class MapShowFragment extends Fragment
{
    Activity activity;
    Category mainCategory;
    Category.SubCategory subCategory;
    HorizontalScrollView titleContainer;
    ImageView curMap;
    ImageView locationGo;
    ImageView locationPhone;
    ImageView locationInfo;
    ImageView menuLink;
    ImageView premium1;
    ImageView premium2;
    ImageView premium3;
    ImageView premium4;
    Integer categoryId = 0;
    Integer subCategoryId = 0;
    Integer categoryItemId = 0;
    LinearLayout btnBack;
    LinearLayout categoryListView;
    List<Category> categoryList = new ArrayList<>();
    List<Category.SubCategory> subCategoryList = new ArrayList<>();
    List<Category.CategoryItem> categoryItemList = new ArrayList<>();
    Location.State currentState;
    Location.StateArea currentArea;
    RelativeLayout shopinfo;
    RelativeLayout menuView;
    RelativeLayout btnChangeArea;
    RelativeLayout btnBackCategory;
    TextView title;
    ParentFragmentInterface interfaceObj;

    Float distance= (float) 1000;

    // Constrcutor
    public MapShowFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get arguments from previous fragment
        Integer stateId = getArguments().getInt("StateId");
        Integer areaId = getArguments().getInt("AreaId");
        Integer categoryId = getArguments().getInt("CategoryId");
        Integer subCategoryId = getArguments().getInt("SubCategoryId");

        // Get the category info
        this.mainCategory = Category.getCategoryById(categoryId);

        Bundle args = getArguments();
        updateCategoryParams(args);

        // Get state info
        currentState = Location.State.getStateInfo(stateId);

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.map_area, container, false);

        // Import Assets
        this.menuLink = (ImageView) view.findViewById(R.id.selectMenu);
        this.titleContainer = (HorizontalScrollView) view.findViewById(R.id.titleContainer);
        this.title = (TextView) view.findViewById(R.id.title);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        this.categoryListView = (LinearLayout) view.findViewById(R.id.popupCategoryList);
        this.menuView = (RelativeLayout) view.findViewById(R.id.popupMenu);
        this.btnChangeArea = (RelativeLayout) view.findViewById(R.id.btnChangeArea);
        this.btnBackCategory = (RelativeLayout) view.findViewById(R.id.btnBackCategory);

        // Set click event for back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger activity backpress button
                activity.onBackPressed();
            }
        });

        // Set click event for the menu button
        menuLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update the menu item
                updateMenuItem();

                // Show the menu
                menuView.setVisibility(View.VISIBLE);

            }
        });

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the menu
                menuView.setVisibility(View.GONE);

            }
        });

        btnChangeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                interfaceObj.clearFragmentStack();

            }
        });

        btnBackCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                interfaceObj.backToFragment(MapSelectCategoryFragment.class.getName());

            }
        });

        // Update fragment title
        this.updateTitle();

        // Update click event
        this.setClickEvent(view);

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
        // Set Title
        String fragmentTitle = currentState.getStateName() + " > " + "Jelutong" + " > " + mainCategory.getCategoryName();

        if(this.subCategory!=null){
            fragmentTitle = fragmentTitle + " > " +subCategory.getSubCategoryName();
        }

        this.title.setText(fragmentTitle);
        // Set the horizontal scrollview to always show to right side
        titleContainer.post(new Runnable() {
            @Override
            public void run() {
                titleContainer.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    /**
     * Function to set the click handler event for the premium user
     */
    private void setClickEvent(View currentView){
        // Import assets
        this.curMap = (ImageView) currentView.findViewById(R.id.map_1);
        this.premium1 = (ImageView) currentView.findViewById(R.id.store_1);
        this.premium2 = (ImageView) currentView.findViewById(R.id.store_2);
        this.premium3 = (ImageView) currentView.findViewById(R.id.store_3);
        this.premium4 = (ImageView) currentView.findViewById(R.id.store_4);
        this.shopinfo = (RelativeLayout) currentView.findViewById(R.id.shop_info);
        this.locationGo = (ImageView) currentView.findViewById(R.id.location_go);
        this.locationPhone = (ImageView) currentView.findViewById(R.id.location_phone);
        this.locationInfo = (ImageView) currentView.findViewById(R.id.location_info);

        // If the view is hidden
        if(shopinfo.getVisibility() != View.VISIBLE)
        {
            shopinfo.animate().translationY(distance);
        }

        // Set onClick event
        this.premium1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopinfo.setVisibility(View.VISIBLE);
                shopinfo.animate().translationY(0);
            }
        });
        this.premium2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopinfo.setVisibility(View.VISIBLE);
                shopinfo.animate().translationY(0);
            }
        });
        this.premium3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopinfo.setVisibility(View.VISIBLE);
                shopinfo.animate().translationY(0);
            }
        });
        this.premium4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopinfo.setVisibility(View.VISIBLE);
                shopinfo.animate().translationY(0);
            }
        });

        this.locationPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:09-8765432"));
                startActivity(intent);
            }
        });

        this.locationGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.locationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Note: DO NOT FINISH THE ACTIVITY
                Intent intent = new Intent(activity.getApplicationContext(), ShopInfoActivity.class);
                startActivity(intent);
            }
        });

        this.curMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When user click on map, hide the shop info
                shopinfo.animate().translationY(distance);
            }
        });
    }

    /**
     * Function to get the category list info
     */
    private void updateCategoryParams(Bundle args)
    {
        // Get category id
        this.categoryId = args.getInt("CategoryId");
        // Get category information
        this.mainCategory = Category.getCategoryById(this.categoryId);

        // Check if the category contain sub category
        if(!this.mainCategory.getSubCategoryList().isEmpty())
        {
            // Get the sub category list
            this.subCategoryList = this.mainCategory.getSubCategoryList();

            // Get sub category id if exists
            if(args.containsKey("SubCategoryId"))
            {
                // Get sub category id
                this.subCategoryId = args.getInt("SubCategoryId");

                // Get sub category information
                for(Category.SubCategory subCategory : this.subCategoryList)
                {
                    if(this.subCategoryId.equals(subCategory.getSubCategoryId()))
                    {
                        this.subCategory = subCategory;
                        this.categoryItemList = subCategory.getCategoryItemList();
                    }
                }//for(Category.SubCategory subCategory : this.subCategoryList)
            }
        }
        else
        {
            // Enter here if the category
            this.categoryList = Category.getCategoryList();
        }
    }

    /**
     * Function to update the list of category in menu
     */
    private void updateMenuItem()
    {
        // Remove all view
        this.categoryListView.removeAllViews();

        // Update the menu based on the followign condition
        // Category Item > Sub Category > Category
        if(!this.categoryItemList.isEmpty())
        {
            // Insert the item into list
            for(Category.CategoryItem item : this.categoryItemList)
            {
                LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View categoryView = newInflater.inflate(R.layout.map_state_category_item, null);

                // Import single category
                final TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
                // Set the category name into textview
                categoryName.setText(item.getCategoryItemName());
                categoryName.setShadowLayer(1,0,0, Color.BLACK);

                // Set onclick event
                categoryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // Add new category into the container
                this.categoryListView.addView(categoryView);
            }
        }
        else if(!this.subCategoryList.isEmpty())
        {
            // Insert the item into list
            for(Category.SubCategory item : this.subCategoryList)
            {
                LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View categoryView = newInflater.inflate(R.layout.map_state_category_item, null);

                // Import single category
                final TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
                // Set the category name into textview
                categoryName.setText(item.getSubCategoryName());
                categoryName.setShadowLayer(1,0,0, Color.BLACK);

                // Set onclick event
                categoryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // Add new category into the container
                this.categoryListView.addView(categoryView);
            }
        }
        else
        {
            // Insert the item into list
            for(Category item : this.categoryList)
            {
                LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View categoryView = newInflater.inflate(R.layout.map_state_category_item, null);

                // Import single category
                final TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
                // Set the category name into textview
                categoryName.setText(item.getCategoryName());
                categoryName.setShadowLayer(1,0,0, Color.BLACK);

                // Set onclick event
                categoryView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // Add new category into the container
                this.categoryListView.addView(categoryView);
            }
        }
    }
}
