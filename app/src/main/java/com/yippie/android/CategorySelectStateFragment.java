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

import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

import java.util.List;

public class CategorySelectStateFragment extends Fragment
{
    Activity activity;
    LinearLayout stateListContainer;
    List<Location.State> stateList;
    ParentFragmentInterface interfaceObj;

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
        View view = inflater.inflate(R.layout.map_select_state, container, false);

        // Import Assets
        this.stateListContainer = (LinearLayout) view.findViewById(R.id.stateList);
        TextView stateLabel= (TextView) view.findViewById(R.id.stateSelectText);

        // Set shadow for the textview
        stateLabel.setShadowLayer(1,0,0, Color.BLACK);

        // Get the list of the state from Location class
        this.stateList = Location.getMalaysiaStateList();

        // Loop through the state list in Malaysia and contruct the view
        for(Location.State state : this.stateList)
        {
            LayoutInflater newInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View stateView = newInflater.inflate(R.layout.location_state_item, null);

            // Import single state
            final TextView stateName = (TextView) stateView.findViewById(R.id.stateName);
            // Set the state name into textview
            stateName.setText(state.getStateName());
            stateName.setShadowLayer(1,0,0,Color.BLACK);

            // Get state id
            final Integer stateId = state.getStateId();

            // Set onclick event
            stateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Add new MapDivision fragment
                    CategorySelectCategoryFragment mapSelectArea = new CategorySelectCategoryFragment();
                    // Store state object to args and pass to next fragment
                    Bundle args = new Bundle();
                    args.putInt("StateId",stateId);
                    mapSelectArea.setArguments(args);

                    interfaceObj.addFragment(mapSelectArea);
                }
            });

            // Add new state into the state container
            this.stateListContainer.addView(stateView);
        }// for(Location.State state : this.stateList)

        return view;
    }

    @Override
    public void onAttach(Activity act)
    {
        super.onAttach(act);
        activity = act;
        interfaceObj = (ParentFragmentInterface) getParentFragment();
    }
}
