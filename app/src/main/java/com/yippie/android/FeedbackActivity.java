package com.yippie.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.yippie.android.library.FeedbackSubjectSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends Activity
{
    EditText feedbackMessage;
    RelativeLayout btnFeedbackSubmit;
    Spinner feedbackSubjectList;
    private static final int PLEASE_CHOOSE = 0;
    private static final int SUGGESTION = 1;
    private static final int ISSUE_PROBLEMS = 2;
    private static final int RIGHTS_LEGAL = 3;
    private static final int AFFILIATES = 4;
    private static final int FEEDBACK = 5;
    private static final int OTHERS = 6;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout
        setContentView(R.layout.feedback);

        // Import assets
        feedbackMessage = (EditText) findViewById(R.id.feedback_msg);
        feedbackSubjectList = (Spinner) findViewById(R.id.feedback_subject);
        btnFeedbackSubmit = (RelativeLayout) findViewById(R.id.btn_feedback_submit);

        // Get the list of the feedback subject
        List<FeedbackSubjectObj> subjectlist = getFeedbackSubjectList();
        // Initialise the spinner adapter
        FeedbackSubjectSpinnerAdapter adapter = new FeedbackSubjectSpinnerAdapter(this,subjectlist);
        // Set adapter to the subject spinner
        feedbackSubjectList.setAdapter(adapter);
    }

    /**
     * Class that stores feedback subject information
     */
    public static class FeedbackSubjectObj
    {
        private int feedbackId;
        private String feedbackText;

        /**
         * Constructor
         */
        public FeedbackSubjectObj(int feedbackId, String feedbackText)
        {
            this.feedbackId = feedbackId;
            this.feedbackText = feedbackText;
        }

        // Getter
        public int getFeedbackId(){
            return feedbackId;
        }
        public String getFeedbackText(){
            return feedbackText;
        }
    }

    /**
     * Function to construct and return the list of subject available for Feedback form
     */
    private List<FeedbackSubjectObj> getFeedbackSubjectList()
    {
        List<FeedbackSubjectObj> list = new ArrayList<>();

        // Add Please Choose Option
        list.add(new FeedbackSubjectObj(PLEASE_CHOOSE, "- Please Choose -"));
        // Add Suggestion
        list.add(new FeedbackSubjectObj(SUGGESTION, "Suggestion"));
        // Add Issue & Problems
        list.add(new FeedbackSubjectObj(ISSUE_PROBLEMS, "Issue & Problems"));
        // Add Legal & Rights
        list.add(new FeedbackSubjectObj(RIGHTS_LEGAL, "Legal & Rights"));
        // Add Affiliates
        list.add(new FeedbackSubjectObj(AFFILIATES, "Affiliates"));
        // Add Feedback
        list.add(new FeedbackSubjectObj(FEEDBACK, "Feedback"));
        // Add Others
        list.add(new FeedbackSubjectObj(OTHERS, "Others"));

        return list;
    }
}
