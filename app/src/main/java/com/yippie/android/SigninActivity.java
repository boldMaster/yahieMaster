package com.yippie.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yippie.android.classes.User;
import com.yippie.android.classes.FacebookAuth;

import java.util.ArrayList;
import java.util.List;

public class SigninActivity extends Activity
{
    EditText emailUsername;
    EditText password;
    RelativeLayout btnSignin;
    RelativeLayout btnFBSignin;
    RelativeLayout btnGoogleSignin;
    TextView btnForgetPass;
    private FacebookAuth objFacebookAuth;

    protected void onCreate(Bundle savedInstanceState) {
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

                Toast.makeText(getApplicationContext(), "Forget password Clicked!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * Private function that perform the account registration
     */
    private void loginUser() {
        // Extract the value from edittext
        String emailusername = emailUsername.getText().toString();
        String pass = password.getText().toString();

        // Validate the input
        Validation validateTask = validate(emailusername, pass);

        // Check if the request is valid
        Boolean isValid = validateTask.getStatus();

        if(isValid)
        {
            // Login user
            User.loginUser(emailusername,emailusername,pass);

            // Launch Sign In Activity
            // Note: Remember to finish(). User shall not be able to return to this page after they login
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
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

        if(validateObj.getStatus())
        {
            // Enter here if there are no problem found in email and password
            // We will try to get user information from database
            User user = User.getUser(emailUsername,emailUsername,pass);

            if(user==null)
            {
                validateObj.setErrMsg("Invalid email, username or password combination.");
                validateObj.setStatus(false);
            }
        }

        return validateObj;
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
}
