package com.yippie.android.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

//Facebook library Class
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

/**
 * Created by Bryan on 9/6/2015.
 */
public class FacebookAuth {

    /*Facebook Permission List
        * Add on in future if more permission needed
        */
    private static FacebookAuth objFacebookAuth;
    private static String fbProfilePermission = "public_profile";
    private static String fbEmailPermission = "email";
    private static String fbLocationPermission = "user_location";

    /* Specify the field permission we needed from Facebook SDK
     *Add on if more field required
     */
    private static String[] fbPermissionField = {"id",
                                                "name",
                                                "first_name",
                                                "last_name",
                                                "email",
                                                "location",
                                                "devices"
    };

    /*Facebook Field List
     *Add on if more field required
     */
    private static String fbIdField = "id";
    private static String fbNameField = "name";
    private static String fbFirstNameField = "first_name";
    private static String fbLastNameField = "last_name";
    private static String fbGenderField = "gender";
    private static String fbEmailField = "email";


    private CallbackManager callbackManager;
    private LoginManager objLoginManager;
    private AccessToken objAccessToken;
    protected String accessTokenError = "";


    private FacebookAuth(Context c){
        this.callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(c);
        objLoginManager= LoginManager.getInstance();
        objAccessToken = AccessToken.getCurrentAccessToken();
    }

    public static FacebookAuth getInstance(Context context){
        if (objFacebookAuth == null)
        {
            // Create the instance
            objFacebookAuth = new FacebookAuth(context);
        }
        return objFacebookAuth;
    }
    public AccessToken getAccessToken(){return this.objAccessToken;}
    public LoginManager getLoginManager(){
        return this.objLoginManager;
    }
    public CallbackManager getCallBackManager(){
        return this.callbackManager;
    }

    /* Check Valid of the Facebook Access Token
     */
    public boolean isLoggedIn() {
        if(AccessToken.getCurrentAccessToken() != null) {
            Log.i("ACCESS TOKEN", "" + AccessToken.getCurrentAccessToken().getExpires());
            return true;
        }
        return false;
    }

    protected void loginWithFacebook(LoginManager objLoginManager){
        objLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                objAccessToken = loginResult.getAccessToken();
                Log.i("FACEBOOKLOGIN", "LoginManager FacebookCallback onSuccess");
                if (loginResult.getAccessToken() != null) {
                    Log.i("FACEBOOKLOGIN", "Access Token:: " + loginResult.getAccessToken());
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    try {
                                        response.getError();
                                        //                String strId = object.getString(fbIdField);
                                        //                String strGender = object.getString(fbGenderField);
                                        String strName = object.getString(fbNameField);
                                        String strEmail= object.getString(fbEmailField);

                                        boolean blnSuccess = User.registerUser(strEmail,strName,"0",objAccessToken);
                                        if(blnSuccess)
                                            Log.i("Save to User", "Cretea an user");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", implode(fbPermissionField,","));
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }

            @Override
            public void onCancel() {
                Log.i("FACEBOOKLOGIN", "LoginManager FacebookCallback onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.i("FACEBOOKLOGIN", "LoginManager FacebookCallback onError");
            }
        });

    }
    protected boolean facebookLogout(){
        objLoginManager.logOut();
        if(AccessToken.getCurrentAccessToken()== null) {
            this.objAccessToken = null;
            User.logoutUser();
            return true;
        }
        else return false;
    }

    //Test print the access Token
    protected String tokenString(){
        if(this.objAccessToken != null )
            return objAccessToken.getToken();
        return "false";
    }

    protected boolean validateAccessToken(){
        if(this.objAccessToken != null) {
            Log.i("ACCESS_TOKEN", this.objAccessToken.toString());
            if (!this.objAccessToken.isExpired()) {
                GraphRequest request = GraphRequest.newMeRequest(
                        this.objAccessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                accessTokenError = "ERROR" + response.getError().toString();
                            }
                        });
                if (accessTokenError == "")
                    return true;
            }
            return false;
        }else
            return false;
    }

    public boolean activityResult(final int requestCode, final int resultCode, final Intent data) {
        this.getCallBackManager().onActivityResult(requestCode, resultCode, data);
        if(this.isLoggedIn()) {
            return true;
        }
        else
            return false;
    }

    protected String implode(String[] originArray,String regex){
        String result = "";
        for(int index= 0 ; index < originArray.length;index++){
            result+= originArray[index];
            if(index != originArray.length - 1){
                result += regex;
            }
        }
        return result;
    }

    public void facebookLogin(Activity a) {
        LoginManager objLoginManager = this.getLoginManager();
        objLoginManager.logInWithReadPermissions(a, Arrays.asList("public_profile, email, user_location"));
        loginWithFacebook(objLoginManager);
    }

    /* Get Correct Hash Key
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.social.yahie.sociallogin",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
       */

}

