package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yippie.android.classes.CriteriaV2;
import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.List;
import java.util.Stack;


public class CategoryListFragmentV2 extends Fragment implements ParentFragmentInterface
{
    protected Activity activity;
    protected LinearLayout stateListContainer;
    protected List<Location.State>stateList;
    protected Stack<String> fragmentStack = new Stack<>();

    public static final Integer GOTO_SHOPLIST = 1;

    // Constructor
    public CategoryListFragmentV2(){}

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
        View view = inflater.inflate(R.layout.main_map, container, false);

        this.switchScreen(false,0);

        return view;
    }

    @Override
    public void onAttach(Activity act)
    {
        super.onAttach(act);
        activity = act;
    }

    @Override
    public void addFragment(Fragment fragment) {

        // Add fragment
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out_right)
                .replace(R.id.content_frame, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();

        // Add fragment name to stack
        fragmentStack.add(fragment.getClass().getName());
    }

    @Override
    public void backToFragment(String fragmentName) {
        int fragmentIndex = fragmentStack.indexOf(fragmentName);

        while(fragmentStack.size() > (fragmentIndex + 1)){
            getChildFragmentManager().popBackStack();
            fragmentStack.pop();
        }

    }

    @Override
    public void clearFragmentStack() {

        while(fragmentStack.size()>0){
            getChildFragmentManager().popBackStack();
            fragmentStack.pop();
        }
    }

    /**
     * @author Written by Zi Yang
     * @desc This function will handle the switch fragment, based on the global CriteriaV2 class
     */
    @Override
    public void switchScreen(Boolean isBack, Integer action) {
        // If action is set, perform action before proceed to handle the switch action
        if(action.equals(GOTO_SHOPLIST))
        {
            CategoryShopListFragmentV2 shopListFragment = new CategoryShopListFragmentV2();
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out_right, 0, 0)
                    .replace(R.id.content_frame, shopListFragment)
                    .commit();

            return;
        }

        // Get the latest CriteriaV2 singleton class, we need to determine which screen to show
        CriteriaV2 currentCriteria = CriteriaV2.getInstance();

        // First check if the state is selected
        Boolean isStateSelected = (currentCriteria.getSelectedState()!=0);

        // If the state is not selected, we will show the country select screen
        if(!isStateSelected)
        {
            CategorySelectStateFragmentV2 selectStateFragment = new CategorySelectStateFragmentV2();
            if(!isBack) {
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out_right, 0, 0)
                        .replace(R.id.content_frame, selectStateFragment)
                        .commit();
            }else{
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out, 0, 0)
                        .replace(R.id.content_frame, selectStateFragment)
                        .commit();
            }

            return;
        }

        // Next, check if the category is all selected
        Boolean isCategoryGroupSelected = (currentCriteria.getCategoryGroup()!=0);
        Boolean isCategorySelected = (currentCriteria.getCategory()!=0);
        Boolean isSubCategorySelected = (currentCriteria.getSubCategory()!=0);

        if(!isCategoryGroupSelected || !isCategorySelected || !isSubCategorySelected)
        {
            CategorySelectCategoryFragmentV2 selectCategoryFragment = new CategorySelectCategoryFragmentV2();
            if(!isBack) {
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out_right, 0, 0)
                        .replace(R.id.content_frame, selectCategoryFragment)
                        .commit();
            }else{
                getChildFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out, 0, 0)
                        .replace(R.id.content_frame, selectCategoryFragment)
                        .commit();
            }
        }
    }
}
