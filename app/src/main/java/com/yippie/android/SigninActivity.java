package com.yippie.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.User;
import com.yippie.android.library.HttpRequestController;
import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;
import com.yippie.android.library.Utility;
import com.yippie.android.classes.FacebookAuth;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SigninActivity extends Activity implements HttpRequestInterface
{
    protected EditText emailUsername;
    protected EditText password;
    protected RelativeLayout btnSignin;
    protected RelativeLayout btnFBSignin;
    protected RelativeLayout btnGoogleSignin;
    protected TextView btnForgetPass;
    protected ProgressDialog pDialog;
    private FacebookAuth objFacebookAuth;
    private static final String SIGN_IN_CALLER = "sign_in_caller";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout
        setContentView(R.layout.signin_activity);

        // Import assets
        emailUsername = (EditText) findViewById(R.id.signinEmailUsername);
        password      = (EditText) findViewById(R.id.signinPassword);
        btnSignin     = (RelativeLayout) findViewById(R.id.btnSignIn);
        btnFBSignin     = (RelativeLayout) findViewById(R.id.btnFBSignIn);
        btnGoogleSignin     = (RelativeLayout) findViewById(R.id.btnGoogleSignIn);
        btnForgetPass    = (TextView) findViewById(R.id.btnForgetPass);

        // Sign In Button Click Event
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();

            }
        });

        // Facebook Sign in button Click Event
        final Activity a = this;
        this.objFacebookAuth = FacebookAuth.getInstance(getApplicationContext());
        btnFBSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objFacebookAuth.facebookLogin(a);
            }
        });

        // Google Plus Sign in button Click Event
        btnGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Sign in Google Clicked!", Toast.LENGTH_SHORT).show();

            }
        });

        // Forget Password button Click Event
        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the new Intent to forgot password page
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Private function that perform the account registration
     */
    private void loginUser()
    {
        // Dismiss soft keyboard
        InputMethodManager input = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        // Extract the value from edittext
        String emailusername = emailUsername.getText().toString();
        String pass = password.getText().toString();

        // Validate the input
        Validation validateTask = validate(emailusername, pass);

        // Check if the request is valid
        Boolean isValid = validateTask.getStatus();

        if(isValid)
        {
            // Launch SignIn user Async Task
            SignInUser task = new SignInUser(emailusername,pass);
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
    private Validation validate(String emailUsername, String pass)
    {
        // Construct new validation object
        Validation validateObj = new Validation();
        // Set the pattern for reg matching
        String htmlPattern = ".*<[^>]+>.*";

        // Check if the email is valid
        if(emailUsername.length()<=0)
        {
            validateObj.setErrMsg("The email address or username cannot be empty.");
            validateObj.setStatus(false);
        }
        // Check if the email address is valid
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailUsername).matches())
        {
            validateObj.setErrMsg("The email address or username entered is invalid.");
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
    public void HttpRequestPreExecuteDelegate()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                // Showing progress dialog before perform the HTTP REQUEST
                pDialog = new ProgressDialog(SigninActivity.this);
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
                            // Sign in request
                            case SIGN_IN_CALLER:
                            {
                                Boolean status = responseJson.getBoolean("status");

                                if(status)
                                {
                                    // Enter here if the user credential are valid
                                    String firstName = responseJson.getString("first_name");
                                    String lastName = responseJson.getString("last_name");

                                    // Sandbox testing usage
                                    Toast.makeText(this, "Welcome back! " + firstName + " " + lastName, Toast.LENGTH_LONG).show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean blnSuccess = this.objFacebookAuth.activityResult(requestCode,resultCode,data);
        if(blnSuccess){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /**
     * This is an AsyncTask to handle the following process:
     * 1. Validate user crdential at server
     */
    public class SignInUser extends AsyncTask<Void, Void, Void>
    {
        //String baseUrl = "localhost";
        String baseUrl = "192.168.1.11";
        String email;
        String pass;

        /**
         * Constructor
         * @param email - The email of the user
         * @param pass  - The password of the account (MD5)
         */
        public SignInUser(String email, String pass)
        {
            this.email = email;
            this.pass = Utility.MD5(pass);
        }

        @Override
        protected Void doInBackground(Void...voids)
        {
            // Construct parameter to be sent over HTTP Request
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("api_key", Utility.getPublicAccessKey()));	// API Key
            params.add(new BasicNameValuePair("caller", SIGN_IN_CALLER));	                // Caller
            params.add(new BasicNameValuePair("email", email));	                            // User ID
            params.add(new BasicNameValuePair("pass", pass));                               // User ID of the conversation user

            // Trigger AsyncTask
            HttpRequestController httpClient = new HttpRequestController(SigninActivity.this);
            String httpURL = "http://" + baseUrl +"/laravel/public/android/v1/user/signin";

            // Execute Get Conversation User List AsyncTask
            httpClient.performAsynRequest(httpURL, params);

            return null;
        }
    }
}
