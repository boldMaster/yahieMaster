package com.yippie.android;

import android.app.Activity;
import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgetPasswordActivity extends Activity implements HttpRequestInterface
{
    protected ProgressDialog pDialog;
    protected static String CAL_FORGOT_PASSWORD = "cal_forget_password";
    protected static String API_FORGOT_PASSWORD = "android/v1/auth/forgot";
    protected EditText emailUsername;
    protected RelativeLayout btnResetPassword;
    protected TextView lblEmailError;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout
        setContentView(R.layout.forgot_password_activity);
        emailUsername = (EditText) findViewById(R.id.forogotEmailUsername);
        btnResetPassword = (RelativeLayout) findViewById(R.id.btnResetPassword);
        lblEmailError = (TextView) findViewById(R.id.lblEmailError);

        // Forget Password button Click Event
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = emailUsername.getText().toString();
                // Launch Reset Password Async Task
                if(Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
                {
                    ForgetPassword task = new ForgetPassword(emailInput);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else {
                    lblEmailError.setText(R.string.error_email_validation_failed);
                }
            }
        });
    }

    @Override
    public void HttpRequestPreExecuteDelegate()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                // Showing progress dialog before perform the HTTP REQUEST
                pDialog = new ProgressDialog(ForgetPasswordActivity.this);
                pDialog.setMessage("Email Sending...");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        });

    }

    @Override
    public void HttpRequestDoInBackgroundDelegate(HttpReturnObject response) {}

    @Override
    public void HttpRequestDoInBackgroundErrorDelegate(HttpReturnObject response) {}

    @Override
    public void HttpRequestProgressUpdateDelegate(HttpReturnObject response) {}

    @Override
    public void HttpRequestOnPostExecuteUpdateDelegate(HttpReturnObject response)
    {
        try
        {
            // Extract the result from the
            JSONObject responseJson = response.getJSON();
            // Get Status code from returned JSON
            Integer statusCode = responseJson.getInt("status_code");
            // Extract the return_vars
            switch(statusCode)
            {
                case 200:
                {
                    // Enter here if status code is 200 (Success)
                    // Extract resutl from the return vars
                    Boolean is_valid = responseJson.getBoolean("status");

                    if(is_valid)
                    {
                        // Enter here if the request is valid and status is true
                        // Get caller name
                        String caller = responseJson.getString("caller");

                        // Sign in request
                        Boolean status = responseJson.getBoolean("success");
                        if(status)
                        {
                            // Sandbox testing usage
                            Toast.makeText(this, R.string.forgot_password_email_sent, Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            // Display error in this section
                            String error = responseJson.getString("error");
                            lblEmailError.setText(R.string.error_email_not_exist);
                            // Show error message by usign Toast
                            Toast.makeText(this, R.string.error_email_not_exist,Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    else
                    {
                        // Display error in this section
                        String error = responseJson.getString("error");
                        lblEmailError.setText(R.string.error_email_validation_failed);
                        // Show error message by usign Toast
                        Toast.makeText(this, R.string.error_email_validation_failed,Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 422: {
                    // Enter here if the status code is 403 (unauthorized)
                    // Display error in this section
                    String error = responseJson.getString("error");
                    lblEmailError.setText(R.string.error_email_validation_failed);
                    Toast.makeText(this, R.string.error_email_validation_failed, Toast.LENGTH_LONG).show();
                    break;
                }
                case 403:
                default: {
                    // Enter here if the status code is 403 (unauthorized)
                    // Display error in this section
                    lblEmailError.setText(R.string.error_access_denied);
                    // Enter here if the status code is 403 (unauthorized)
                    Toast.makeText(this, R.string.error_access_denied , Toast.LENGTH_LONG).show();
                    break;
                }
            }//switch(statusCode)

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }//try

        // Dismiss the Progress Dialog if not null
        if(pDialog != null)
        {
            pDialog.dismiss();
        }
    }

    /**
     * Validation Class
     */
    private class Validation
    {
        Boolean isValid;
        List<String> errMsgList;

        /**
         * Constructor
         */
        public Validation()
        {
            isValid = true;
            errMsgList = new ArrayList<>();
        }

        // Setter
        public void setStatus(Boolean status){
            isValid = status;
        }
        public void setErrMsg(String errMsg){
            errMsgList.add(errMsg);
        }

        // Getter
        public Boolean getStatus(){
            return isValid;
        }
        public List<String> getErrList(){
            return errMsgList;
        }
    }

    /**
     * This is an AsyncTask to handle the following process:
     * 1. Validate user crdential at server
     */
    public class ForgetPassword extends AsyncTask<Void, Void, Void>
    {
    //    String baseUrl = "localhost";
        String baseUrl = "192.168.0.101";
    //    String baseUrl = "bee.honeyspear.com";
        String email;

        /**
         * Constructor
         * @param email - The email of the user
         */
        public ForgetPassword(String email)
        {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void...voids)
        {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));	// API Key
            params.add(new BasicNameValuePair("caller", CAL_FORGOT_PASSWORD));	            // Caller
            params.add(new BasicNameValuePair("email", email));	                            // Email

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(ForgetPasswordActivity.this);
            String httpURL = "http://" + baseUrl +"/flink/public/"+API_FORGOT_PASSWORD;
            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }
}
