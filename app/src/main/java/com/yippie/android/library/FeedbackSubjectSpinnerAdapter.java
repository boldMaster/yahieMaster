package com.yippie.android.library;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.yippie.android.FeedbackActivity.FeedbackSubjectObj;
import com.yippie.android.R;

public class FeedbackSubjectSpinnerAdapter implements SpinnerAdapter
{
    private List<FeedbackSubjectObj> data;
    private LayoutInflater inflater;

    /**
     * Constructor
     * @param activity - The activity that uses the spinner and this adapter.
     * @param list - This is the list to be display as options in the spinner.
     */
    public FeedbackSubjectSpinnerAdapter(Activity activity, List<FeedbackSubjectObj> list)
    {
        data = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        SpinnerViewHolder holder;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.spinner_item, null);

            holder = new SpinnerViewHolder();

            holder.itemOne = (TextView) convertView.findViewById(R.id.spinnerText);

            convertView.setTag(holder);

        }
        else
        {
            holder = (SpinnerViewHolder) convertView.getTag();
        }

        holder.itemOne.setText(data.get(position).getFeedbackText());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    /**
     * This is a custom View Holder class to hold the view of custom spinner
     */
    public class SpinnerViewHolder
    {
        TextView itemOne;
    }

}