package com.yippie.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.util.Patterns;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.yippie.android.classes.User;
import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends Activity  implements HttpRequestInterface
{
    protected EditText emailAddress;
    protected EditText username;
    protected EditText password;
    protected ProgressDialog pDialog;
    protected RelativeLayout btnJoin;
    private static final String SIGN_UP_CALLER = "sign_up_caller";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout
        setContentView(R.layout.signup_activity);

        // Import assets
        emailAddress = (EditText) findViewById(R.id.signUpEmail);
        username     = (EditText) findViewById(R.id.signUpUsername);
        password     = (EditText) findViewById(R.id.signUpPassword);
        btnJoin      = (RelativeLayout) findViewById(R.id.btnSignupJoin);

        /* Set the click event for each button */

        // Join Button Click Event
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
    }

    /**
     * Private function that perform the account registration
     */
    private void registerUser()
    {
        // Extract the value from edittext
        String email = emailAddress.getText().toString();
        String name = username.getText().toString();
        String pass = password.getText().toString();

        // Validate the input
        Validation validateTask = validate(email,name,pass);

        // Check if the request is valid
        Boolean isValid = validateTask.getStatus();

        if(isValid)
        {
            // Launch SignIn user Async Task
            SignUpUser task = new SignUpUser(email,name,pass);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            // Get the list of error message
            List<String> errMsgList = validateTask.getErrList();
            String errMsg = "";

            // Loop through the error message
            for(String curErrMsg : errMsgList)
            {
                errMsg = errMsg + curErrMsg + "\n";
            }

            Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Private function that validate the entry
     */
    private Validation validate(String email, String name, String pass)
    {
        // Construct new validation object
        Validation validateObj = new Validation();
        // Set the pattern for reg matching
        String htmlPattern = ".*<[^>]+>.*";

        // Check if the email is valid
        if(email.length()<=0)
        {
            validateObj.setErrMsg("The email address cannot be empty.");
            validateObj.setStatus(false);
        }
        // Check if the email address is valid
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            validateObj.setErrMsg("The email address entered is invalid.");
            validateObj.setStatus(false);
        }

        // Check if the name is valid
        if(name.length()<=0)
        {
            validateObj.setErrMsg("The name cannot be empty.");
            validateObj.setStatus(false);
        }
        // Check if the username contain html element
        else if(name.matches(htmlPattern))
        {
            validateObj.setErrMsg("The name cannot contain html elements.");
            validateObj.setStatus(false);
        }

        // Check if the password is valid
        if(pass.length()<=0)
        {
            validateObj.setErrMsg("The password cannot be empty.");
            validateObj.setStatus(false);
        }
        else if(pass.length()<6)
        {
            validateObj.setErrMsg("The password length must not be less than 6.");
            validateObj.setStatus(false);
        }
        // Check if the email address is valid
        else if(pass.matches(htmlPattern))
        {
            validateObj.setErrMsg("The password cannot contain html value.");
            validateObj.setStatus(false);
        }

        return validateObj;
    }

    @Override
    public void HttpRequestPreExecuteDelegate() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                // Showing progress dialog before perform the HTTP REQUEST
                pDialog = new ProgressDialog(SignupActivity.this);
                pDialog.setMessage("Signing You in.. Please wait..");
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

            switch(statusCode)
            {
                case 200:
                {
                    // Enter here if status code is 200 (Success)
                    // Extract the return_vars
                    JSONObject returnVars = responseJson.getJSONObject("return_vars");
                    // Extract resutl from the return vars
                    Boolean is_valid = returnVars.getBoolean("status");

                    if(is_valid)
                    {
                        // Enter here if the request is valid and status is true
                        // Get caller name
                        String caller = responseJson.getString("caller");

                        switch(caller)
                        {
                            // Signup request
                            case SIGN_UP_CALLER:
                            {
                                // Extract the new user information
                                JSONObject newUserInfo = responseJson.getJSONObject("user_info");
                                Integer uid = newUserInfo.getInt("uid");
                                String firstName = newUserInfo.getString("first_name");
                                String lastName = newUserInfo.getString("last_name");
                                String email = newUserInfo.getString("email");
                                Integer state_id = newUserInfo.getInt("state_id");
                                Integer country_id = newUserInfo.getInt("country_id");

                                // Sign in user
                                //User.loginUser();

                                // Redirect user to the main activity
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                // Close current activity
                                this.finish();
                            }
                        }
                    }
                    else
                    {
                        // Display error in this section
                        JSONArray errorList = returnVars.getJSONArray("errors");
                        List<String> errMsgList = new ArrayList<>();

                        for(Integer i = 0; i < errorList.length(); i++) {
                            // Get the err msg and store into array list
                            errMsgList.add(errorList.getString(i));
                        }

                        // Join the error message with \n
                        String errMsg = TextUtils.join("\n", errMsgList);

                        // Show error message by usign Toast
                        Toast.makeText(this, errMsg,Toast.LENGTH_LONG).show();
                    }


                    break;
                }

                case 403:
                default:
                    // Enter here if the status code is 403 (unauthorized)
                    Toast.makeText(this, "You do not have permission to perform this action.", Toast.LENGTH_LONG).show();
                    break;
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
        List<String>errMsgList;

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
    public class SignUpUser extends AsyncTask<Void, Void, Void>
    {
        //String baseUrl = "localhost";
        String baseUrl = "192.168.1.11";
        String email;
        String name;
        String pass;

        /**
         * Constructor
         * @param email - The email of the user
         * @param pass  - The password of the account (MD5)
         */
        public SignUpUser(String email, String name, String pass)
        {
            this.email = email;
            this.name = name;
            this.pass = Utility.MD5(pass);
        }

        @Override
        protected Void doInBackground(Void...voids)
        {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));	// API Key
            params.add(new BasicNameValuePair("caller", SIGN_UP_CALLER));	                // Caller
            params.add(new BasicNameValuePair("email", email));	                            // Email address
            params.add(new BasicNameValuePair("email", name));	                            // user name
            params.add(new BasicNameValuePair("pass", pass));                               // Password

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(SignupActivity.this);
            String httpURL = "http://" + baseUrl +"/laravel/public/android/v1/user/signup";

            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }
}
