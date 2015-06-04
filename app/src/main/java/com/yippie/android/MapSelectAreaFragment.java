package com.yippie.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yippie.android.classes.Location;
import com.yippie.android.library.ParentFragmentInterface;

public class MapSelectAreaFragment extends Fragment{

    Activity activity;
    LinearLayout btnBack;
    TextView title;
    Location.State state;

    // Temp area var
    ImageView tempArea;
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
        Integer stateId = getArguments().getInt("StateId");
        // Get state info
        state = Location.State.getStateInfo(stateId);

        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.map_state, container, false);

        // Import assets
        this.btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        this.title = (TextView) view.findViewById(R.id.title);
        this.tempArea = (ImageView) view.findViewById(R.id.map_area_one);

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

        // Set map area one click event
        tempArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new MapDivision fragment
                MapSelectCategoryFragment mapSelectCategory = new MapSelectCategoryFragment();
                // Store state object to args and pass to next fragment
                Bundle args = new Bundle();
                args.putInt("StateId",state.getStateId());
                args.putInt("AreaId",1);
                mapSelectCategory.setArguments(args);

                interfaceObj.addFragment(mapSelectCategory);
            }
        });

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
        title.setText("State: " + state.getStateName());
    }
}
