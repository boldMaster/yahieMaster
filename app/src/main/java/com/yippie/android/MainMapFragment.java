package com.yippie.android;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

import com.yippie.android.library.ParentFragmentInterface;


public class MainMapFragment extends Fragment implements ParentFragmentInterface
{
    Activity activity;
    Stack<String> fragmentStack = new Stack<>();

    // Constructor
    public MainMapFragment(){}

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

        // Check if the child fragment backstack count is empty or not
        // We will add the view if the backstack is empty, or else the fragment will mess up
        if(getChildFragmentManager().getBackStackEntryCount() == 0) {
            // Add new MapDivision fragment
            MapSelectStateFragment selectStateFragment = new MapSelectStateFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(0, 0, R.anim.slide_in, R.anim.slide_out_right)
                    .replace(R.id.content_frame, selectStateFragment)
                    .commit();
        }

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
}
