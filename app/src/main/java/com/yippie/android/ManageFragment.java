package com.yippie.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yippie.android.classes.User;
import com.yippie.android.library.ParentFragmentInterface;

/**
 * Created by Tan on 5/27/2015.
 */
public class ManageFragment extends Fragment implements View.OnClickListener
{
    protected Activity activity;
    protected LinearLayout btnAccountInfo;
    protected LinearLayout btnManageSubscription;
    protected LinearLayout btnAdvertise;
    protected LinearLayout btnContactUs;
    protected LinearLayout btnTermsCondition;
    protected LinearLayout btnSignOut;
    protected ParentFragmentInterface interfaceObj;

    // Button tag
    private static final String ACCOUNT_INFO_TAG = "account_info";
    private static final String MANAGE_ACCOUNT_TAG = "manage_account";
    private static final String ADVERTISE_TAG = "advertise";
    private static final String CONTACT_US_TAG = "contact_us";
    private static final String TERMS_N_CONDITION_TAG = "terms_condition";
    private static final String SIGN_OUT_TAG = "sign_out";

    /**
     * Constructor
     */
    public ManageFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.user_manage, container, false);

        // Import assets
        this.btnAccountInfo = (LinearLayout) view.findViewById(R.id.btnAccInfo);
        this.btnManageSubscription = (LinearLayout) view.findViewById(R.id.btnManageSubscription);
        this.btnAdvertise = (LinearLayout) view.findViewById(R.id.btnAdvertise);
        this.btnContactUs = (LinearLayout) view.findViewById(R.id.btnContactUs);
        this.btnTermsCondition = (LinearLayout) view.findViewById(R.id.btnTemsNCondition);
        this.btnSignOut = (LinearLayout) view.findViewById(R.id.btnSignOut);

        // Set click listener
        btnAccountInfo.setTag(ACCOUNT_INFO_TAG);
        btnManageSubscription.setTag(MANAGE_ACCOUNT_TAG);
        btnAdvertise.setTag(ADVERTISE_TAG);
        btnContactUs.setTag(CONTACT_US_TAG);
        btnTermsCondition.setTag(TERMS_N_CONDITION_TAG);
        btnSignOut.setTag(SIGN_OUT_TAG);

        btnAccountInfo.setOnClickListener(this);
        btnManageSubscription.setOnClickListener(this);
        btnAdvertise.setOnClickListener(this);
        btnContactUs.setOnClickListener(this);
        btnTermsCondition.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v)
    {
        String viewTag = (String) v.getTag();
        switch(viewTag)
        {
            case ACCOUNT_INFO_TAG:

                break;

            case MANAGE_ACCOUNT_TAG:

                break;

            case ADVERTISE_TAG:

                break;

            case CONTACT_US_TAG:

                // Add fragment
                Intent feedbackIntent = new Intent(activity,FeedbackActivity.class);
                activity.startActivity(feedbackIntent);

                break;

            case TERMS_N_CONDITION_TAG:

                break;

            case SIGN_OUT_TAG:

                // Create dialog popup box
                LogoutDialog logoutDialog = LogoutDialog.newInstance(activity);
                // For dragment dialog, use needto use activity support fragment manager
                FragmentManager fragmentManager = activity.getFragmentManager();
                logoutDialog.show(fragmentManager,"LogoutAlert");

                break;
        }

    }

    /**
     * This is a class to generate a confirmation fragment dialog popup box when user about to leave the page/fragment.
     */
    public static class LogoutDialog extends DialogFragment
    {
        static Activity activity;

        public static LogoutDialog newInstance(Activity a)
        {
            activity = a;
            return new LogoutDialog();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = R.string.signout_alert_title;
            int message = R.string.signout_alert_message;
            // To set theme for Dialog Fragment, we need to use ContextThemeWrapper instead of
            // AlertDialog.Builder(context, theme), due to it requires API 11.
            ContextThemeWrapper context = new ContextThemeWrapper(activity, R.style.Theme_AppCompat_Light);
            // Create a new dialog
            AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
            // Set Title
            newDialog.setTitle(title);
            newDialog.setMessage(message);
            // Set Positive (yes) button action
            newDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int whichButton)
                {
                    // If yes is pressed, means that user is confirm to discard the existing message.
                    // Remove the fragment
                    // Logout User
                    User.logoutUser();
                    // Start first page activity
                    Intent intent = new Intent(activity.getApplicationContext(), SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
            // Set Negative (No) button action
            newDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int whichButton)
                {
                    // If no is pressed, dismiss the dialog box
                    dialog.dismiss();
                }
            });

            // create new dialog box & return
            return newDialog.create();
        }
    }
}
