package com.yippie.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.Event;
import com.yippie.android.classes.User;
import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventFragment extends Fragment implements HttpRequestInterface
{
    protected Activity activity;
    protected final static String CAL_EVENT_GET = "cal_event_get";
    public static String API_GET_EVENT = "android/v1/event/getEvent";
    protected final static String CAL_EVENT_GETNEXT = "cal_event_getNext";
    public static String API_NEXT_EVENT = "android/v1/event/getNextEvent";
    protected final static String PARTICIPANT_EVENT = "participant_event";
    public static String API_PARTICIPANT_EVENT = "android/v1/event/participant";

    // Set a tag for this fragment. This var is static, can be referene cia MainMapFragment.FRAGMENT_TAG
    public static final String FRAGMENT_TAG = "EVENT";
    public static final String EVENT_FRAGMENT = "EventFragment";
    public static final String PARTICIPANT_SUCCES = "Success";
    public static final String PARTICIPANT_DUPLICATE = "Duplicate";
    public static final String PARTICIPANT_ERROR = "Error Occurs";
    public static final String PARIICIPANT_INVALID = "Invalid Event";

    public LinearLayout btnParticipant;
    private TextView title;
    protected User user;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // In fragment, we cannot set content view in main, we can only set interface view in onCreateView
        // Set layout
        View view = inflater.inflate(R.layout.event, container, false);
        Fragment f = ((MainActivity) getActivity()).getCurrentFragment();
        String fragmentString = f.toString();
        // Get current active event from db
        final Event activeEvent = Event.getActiveEvent();
        //If found, get the time and start the timer
        if (activeEvent != null) {
            long endMiliSecond = Utility.getMiliSecondFromDate(activeEvent.getEndDate());
            this.startTimer(endMiliSecond, view);
            this.title = (TextView) view.findViewById(R.id.currentEventTitle);
            this.title.setText(activeEvent.getEventTitle());
        } else {
            // Else Call http request to server to get active Event
            GetEvent task = new GetEvent();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        // Append the participant button action
        if(view != null) {
            btnParticipant = (LinearLayout) view.findViewById(R.id.btnParticipate);
            // Forget Password button Click Event
            btnParticipant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer event_id = activeEvent.getEventId();
                    user = new User();
                    user = user.getLoginUser();
                    if(user != null) {
                        Integer user_id = user.getUid();
                        ParticipantEvent participantEvent = new ParticipantEvent(user_id, event_id);
                        participantEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            });
        }else{
            System.out.println("No view info");
        }
        return view;
    }

    public void getNextEvent() {
        NextEvent newEventTask = new NextEvent();
        newEventTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void HttpRequestPreExecuteDelegate() {

    }

    @Override
    public void HttpRequestDoInBackgroundDelegate(HttpReturnObject response) {
    }

    @Override
    public void HttpRequestDoInBackgroundErrorDelegate(HttpReturnObject response) {
    }

    @Override
    public void HttpRequestProgressUpdateDelegate(HttpReturnObject response) {
    }

    @Override
    public void HttpRequestOnPostExecuteUpdateDelegate(HttpReturnObject response) {
        try {
            // Extract the result from the
            JSONObject responseJson = response.getJSON();
            // Get Status code from returned JSON
            Integer statusCode = responseJson.getInt("status_code");
            // Extract the return_vars
            switch (statusCode) {
                case 200: {
                    String strResult = responseJson.getString("result");
                    // Get caller name
                    String caller = responseJson.getString("caller");
                    // If the found active event
                    if (strResult != null && !strResult.isEmpty()) {
                        System.out.println("HTTP RESULT: "+strResult);
                        // Enter here if the request is valid and status is true
                        // Sandbox testing usage
                        switch (caller) {
                            case CAL_EVENT_GET: {
                                // Show error message by using Toast
                                this.title = (TextView) getView().findViewById(R.id.currentEventTitle);
                                JSONObject $jsonResult = new JSONObject(strResult);
                                Event currentEvent = processEventRequest($jsonResult);
                                currentEvent.insertEvent();
                                currentEvent.getEventTestString();
                                long endMiliSecond = Utility.getMiliSecondFromDate(currentEvent.getEndDate());
                                this.startTimer(endMiliSecond, getView());
                                this.title.setText(currentEvent.getEventTitle());
                            }
                            case CAL_EVENT_GETNEXT: {
                                // Show error message by using Toast
                                JSONObject $jsonResult = new JSONObject(strResult);
                                Event currentEvent = processEventRequest($jsonResult);
                                currentEvent.getEventTestString();
                                currentEvent.insertEvent();
                                this.title = (TextView) getView().findViewById(R.id.nextEventTitle);
                                this.title.setText(currentEvent.getEventTitle());
                            }
                            case PARTICIPANT_EVENT: {
                                switch(strResult){
                                    case PARTICIPANT_SUCCES:{
                                        // Store detail into participant

                                    }
                                    case PARTICIPANT_DUPLICATE:{
                                        // Display Toast for duplicate

                                        // Direct to participant event list
                                    }
                                    case PARTICIPANT_ERROR:{
                                        // Display Error Message

                                    }
                                    case PARIICIPANT_INVALID:{
                                        // Display Error Message

                                    }
                                }

                            }
                            default:
                        }
                        // Else No active event
                    } else {
                        if(caller.equalsIgnoreCase(CAL_EVENT_GET)) {
                            this.getNextEvent();
                        } else if (caller.equalsIgnoreCase(CAL_EVENT_GETNEXT)) {
                            this.title.setText("No coming new event, stay tuned");
                        }
                    }
                    break;
                }
                case 403:
                default: {
                    // Enter here if the status code is 403 (unauthorized)
                    // Display error in this section

                    // Enter here if the status code is 403 (unauthorized)
                    Toast.makeText(this.activity, R.string.error_access_denied, Toast.LENGTH_LONG).show();
                    break;
                }
            }//switch(statusCode)

        } catch (JSONException e) {
            e.printStackTrace();
        }//try
    }

    public void setTimer(long intTime, View view){
        //Set Timer
        final CounterClass timer = new CounterClass(intTime,1000, view);
        timer.start();
    }

    // Get Remainning time and start timer
    private void startTimer(long endMiliSecond, View view){
        Date date = new Date();
        long curMiliSecond = date.getTime();

        long diffMiliSecond = 0;
        if(endMiliSecond > curMiliSecond){
            diffMiliSecond = endMiliSecond - curMiliSecond;
        }
        this.setTimer(diffMiliSecond, view);
    }

    public Event processEventRequest(JSONObject $jsonResult){
        Integer intEventId = null;
        Event event = new Event();
        try {
            intEventId = Integer.parseInt($jsonResult.getString("event_id"));
            Integer intPlaceId = Integer.parseInt($jsonResult.getString("place_id"));
            Integer intAdvertiserId = Integer.parseInt($jsonResult.getString("advertiser_id"));
            String strEventTitle = $jsonResult.getString("event_title");
            String strEventDesc = $jsonResult.getString("event_desc");
            Integer intAmountPerVoucher = Integer.parseInt($jsonResult.getString("amount_per_voucher"));
            Integer intTotalAmount = Integer.parseInt($jsonResult.getString("total_amount"));
            Integer intTotalWinner = Integer.parseInt($jsonResult.getString("total_winner"));
            Integer intRedeemDuration = Integer.parseInt($jsonResult.getString("redeem_duration"));
            String strStartDate = $jsonResult.getString("start_date");
            String strEndDate = $jsonResult.getString("end_date");
            String strUpdatedAt = $jsonResult.getString("updated_at");
            event = new Event(intEventId, intPlaceId, intAdvertiserId, strEventTitle, strEventDesc, intAmountPerVoucher, intTotalAmount, intTotalWinner, strStartDate, strEndDate, intRedeemDuration, strUpdatedAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Validate user credential at server
     */
    public class GetEvent extends AsyncTask<Void, Void, Void> {
         //   String baseUrl = "localhost";
        String baseUrl = "192.168.0.103";
        //    String baseUrl = "bee.honeyspear.com";
        JSONObject criteria;

        @Override
        protected Void doInBackground(Void... voids) {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));    // API Key
            params.add(new BasicNameValuePair("caller", CAL_EVENT_GET));                // Caller

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(EventFragment.this);
            String httpURL = "http://" + baseUrl + "/flink/public/" + API_GET_EVENT;
            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Validate user credential at server
     */
    public class NextEvent extends AsyncTask<Void, Void, Void> {
        // String baseUrl = "localhost";
        String baseUrl = "192.168.0.103";
        //    String baseUrl = "bee.honeyspear.com";
        JSONObject criteria;

        @Override
        protected Void doInBackground(Void... voids) {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));    // API Key
            params.add(new BasicNameValuePair("caller", CAL_EVENT_GETNEXT));                // Caller

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(EventFragment.this);
            String httpURL = "http://" + baseUrl + "/flink/public/" + API_NEXT_EVENT;
            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Paricipant to the active Event
     */
    public class ParticipantEvent extends AsyncTask<Void, Void, Void> {
        // String baseUrl = "localhost";
        String baseUrl = "192.168.0.103";
        //    String baseUrl = "bee.honeyspear.com";
        JSONObject criteria;
        Integer userId;
        Integer eventId;
        ParticipantEvent(Integer uid, Integer eid){
            userId = uid;
            eventId = eid;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));    // API Key
            params.add(new BasicNameValuePair("caller", PARTICIPANT_EVENT));                // Caller
            params.add(new BasicNameValuePair("user_id", this.userId.toString()));
            params.add(new BasicNameValuePair("event_id", this.eventId.toString()));

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(EventFragment.this);
            String httpURL = "http://" + baseUrl + "/flink/public/" + API_PARTICIPANT_EVENT;
            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);
            return null;
        }
    }

    public class CounterClass extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        TextView tvHour;
        TextView tvMinute;
        TextView tvSecond;
        public CounterClass(long millisInFuture, long countDownInterval, View view) {
            super(millisInFuture, countDownInterval);
              tvHour = (TextView) view.findViewById(R.id.textViewHour);
              tvMinute = (TextView) view.findViewById(R.id.textViewMinute);
              tvSecond = (TextView) view.findViewById(R.id.textViewSecond);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long milis = millisUntilFinished;
            String hour = String.format("%02d", TimeUnit.MILLISECONDS.toHours(milis));
            String minute = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(milis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milis)));
            String second = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes(milis)));
            System.out.println(hour+":"+minute+":"+second);

            if(!this.tvHour.getText().toString().equals(hour)) {
                System.out.println("Change Hour"+this.tvHour.getText().toString()+":"+hour);
                this.tvHour.setText(hour);
            }
            if(!this.tvMinute.getText().toString().equals(minute)){
                System.out.println("Change minute"+this.tvMinute.getText()+":"+minute);
                this.tvMinute.setText(minute);
            }
            if(!this.tvSecond.getText().toString().equals(second) ) {
                System.out.println("Change Second"+this.tvSecond.getText()+":"+second);
                this.tvSecond.setText(second);
            }
        }

        @Override
        public void onFinish() {

        }
    }
}
