package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yippie.android.classes.Category;
import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.List;

public class CategoryShopListFragment extends Fragment
{
    Activity activity;
    Category mainCategory;
    Category.SubCategory subCategory;
    Category.CategoryItem thirdCategory;
    HorizontalScrollView titleContainer;
    Integer stateId;
    Integer areaId;
    Integer categoryId;
    Integer subCategoryId;
    Integer thirdCategoryId;
    LinearLayout shopListContainer;
    LinearLayout btnBack;

    Location.State currentState;
    Location.StateArea currentArea;
    TextView title;
    TextView categoryTitle;
    ParentFragmentInterface interfaceObj;
    int requestCategoryType;

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
        subCategoryId = getArguments().getInt("SubCategoryId");
        thirdCategoryId = getArguments().getInt("ThirdCategoryId");
        requestCategoryType = getArguments().getInt("CategoryType");

        // Get state info
        currentState = Location.State.getStateInfo(stateId);

        // Get main category info
        this.mainCategory = Category.getCategoryById(categoryId);

        // Get sub category info
        if(subCategoryId!=null) {
            this.subCategory = Category.getCategoryById(categoryId).getSubCategoryById(subCategoryId);
        }

        // Get sub category info
        if(subCategoryId!=null) {
 //           this.thirdCategory = Category.getCategoryById(categoryId).getSubCategoryById(subCategoryId);
        }

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.category_shop_list, container, false);

        // Import Assets
        this.shopListContainer = (LinearLayout) view.findViewById(R.id.shopList);
        this.titleContainer = (HorizontalScrollView) view.findViewById(R.id.titleContainer);
        this.title = (TextView) view.findViewById(R.id.title);
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);

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

        // Update the list
        this.updateShopList();

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

        title.setText(currentState.getStateName() + " > " + mainCategory.getCategoryName()  );
        // Set the horizontal scrollview to always show to right side
        titleContainer.post(new Runnable() {
            @Override
            public void run() {
                titleContainer.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    /**
     * Function to update the shop list
     */
    private void updateShopList()
    {
        // Loop through the state list in Malaysia and contruct the view
        for(int i=0; i<10; i++)
        {
            LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View shopView = newInflater.inflate(R.layout.shop_list_item, null);

            // Import assets
            ImageView shopImage = (ImageView) shopView.findViewById(R.id.shop_image);
            TextView shopName = (TextView) shopView.findViewById(R.id.shop_name);
            TextView shopAddress = (TextView) shopView.findViewById(R.id.shop_address);
            LinearLayout btnCall = (LinearLayout) shopView.findViewById(R.id.btn_shop_call);
            LinearLayout btnGo = (LinearLayout) shopView.findViewById(R.id.btn_shop_direction);
            LinearLayout btnInfo = (LinearLayout) shopView.findViewById(R.id.btn_shop_info);

            // Set click event
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:09-8765432"));
                    startActivity(intent);
                }
            });

            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Note: DO NOT FINISH THE ACTIVITY
                    Intent intent = new Intent(activity.getApplicationContext(), ShopInfoActivity.class);
                    startActivity(intent);
                }
            });


            // Add shop into the shop list container
            this.shopListContainer.addView(shopView);
        } // for(int i=0; i<10; i++)
    }

}
