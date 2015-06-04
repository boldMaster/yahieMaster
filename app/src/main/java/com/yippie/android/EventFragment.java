package com.yippie.android;

import android.os.CountDownTimer;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EventFragment extends Fragment
{
    // Set a tag for this fragment. This var is static, can be referene cia MainMapFragment.FRAGMENT_TAG
    public static final String FRAGMENT_TAG = "EVENT";

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
        View view = inflater.inflate(R.layout.event, container, false);

        return view;
    }


}
