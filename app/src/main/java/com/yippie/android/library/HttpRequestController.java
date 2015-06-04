package com.yippie.android.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class HttpRequestController
{
    private HttpRequestInterface httpInterfaceObj;

    public HttpRequestController(HttpRequestInterface httpInterfaceObjParams)
    {
        httpInterfaceObj = httpInterfaceObjParams;
    }

    @SuppressLint("NewApi")
    public Boolean performAsynRequest(String requestUrl, List<NameValuePair> doInBackgroundInputParams)
    {
        performAsyncRequestTask aTask = new performAsyncRequestTask(requestUrl, doInBackgroundInputParams);

        // For version that higher than HoneyComb, aka API Version 11 or above / Android 3.0 or above
        // The AsyncTask support multiple request to be run at the same time.
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestUrl);

        return true;
    }

    /**
     * Private class for performing Asynchronous HTTP Request
     * The 3 parameters passed in to AsyncTask are meant to be passed into doInBackground, onProgressUpdate and onPostExecute.
     */
    private class performAsyncRequestTask extends AsyncTask<String,String,HttpReturnObject>
    {
        private String URL;
        private List<NameValuePair> doInBackgroundListParams;

        // Constructor
        private performAsyncRequestTask(String url, List<NameValuePair> doInBackgroundInputParams)
        {
            URL = url;
            doInBackgroundListParams = doInBackgroundInputParams;
        }

        // Getter
        private String getURL()
        {
            return URL;
        }

        private List<NameValuePair> getDoInBackgroundListParams()
        {
            return doInBackgroundListParams;
        }

        @Override
        /**
         * Perform asynchronous HTTP request in the background
         * @returns rtnObj (HttpReturnObject) - The HttpReturnObject object created through this callback.
         *                                      For more information, please refer to HttpReturnObject class under library folder.
         */
        protected HttpReturnObject doInBackground(String... inputStr)
        {
            // Get the request URL and the object passed to AsyncTask via List
            String requestUrl = getURL();
            List<NameValuePair> getParam = getDoInBackgroundListParams();

            // Construct GET parameter
            String StringGET = URLEncodedUtils.format(getParam, "utf-8");
            // Add GET Parameter to URL
            requestUrl = requestUrl + "?" + StringGET;

            // Construct the return object
            HttpReturnObject rtnObj = new HttpReturnObject();

            // Set the default status as TRUE, we will only set the status to false if we hit any error condition
            rtnObj.setStatus(true);

            // Dump an information Log
            Log.i("HttpRequestController","In doInBackground, the requestURL="+requestUrl);

            // Instead of create new HttpClient, we should use Existing Singleton HttpClient
            DefaultHttpClient httpClient = HttpClient.getThreadSafeClient();
            HttpResponse response;

            try
            {
                response = httpClient.execute(new HttpGet(requestUrl));
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode()== HttpStatus.SC_OK)
                {
                    // Log the message to indicate the HTTP Request is success
                    Log.i("HttpRequestController","HTTP Response statusLine == HTTPStatus.SC_OK");

                    try
                    {
                        // Process the HTTP Response to get both the HTTP Response and JSON Response string
                        HttpReturnObject asyncDataJson = processHttpAsyncResultsJson(response);

                        if(asyncDataJson==null)
                        {
                            // Enter here if we did not receive any response
                            Log.i("HttpRequestController","asyncDataJson returned as null in doInBackground");
                            return rtnObj;
                        }

                        // Get the JSON Object
                        JSONObject jObj = asyncDataJson.getJSON();
                        // We only support JSON reply, and if we didn't get a JSON reply, we assume that it is an error
                        if(jObj==null)
                        {
                            // Enter here if we received a Null instead of a JSON Object
                            Log.i("HttpRequestController","jObj is Null in doInBackground");
                            rtnObj.setStatus(false);
                            return rtnObj;
                        }

                        // Get the return status code to determine if the request is successful. Status code of 200 indicates Success.
                        String statusCodeStr = jObj.getString("status_code");
                        if(statusCodeStr==null)
                        {
                            Log.i("HttpRequestController","Unable to obtain status_code");
                            rtnObj.setStatus(false);
                            return rtnObj;
                        }

                        // Convert string to integer and call the specific handler
                        Integer statusCodeInt = Integer.parseInt(statusCodeStr);

                        switch(statusCodeInt)
                        {
                            case 200:
                                // Enter here if we receive HTTP response code of 200 OK
                                Log.i("HttpRequestController","Entered switch case condition with respond code 200 in doInBackground");

                                break;
                            case 403:
                                // Enter here if we received HTTP response code of 403 Access denied
                                Log.i("HttpRequestController","Entered switch case condition with respond code 403 in doInBackground");
                                rtnObj.setStatus(false);
                                break;

                            default:
                                // Enter here if we hit a status code that is not being handled
                                Log.w("HttpRequestController","Detected unhandled status code in doInBackground");
                                rtnObj.setStatus(false);
                                break;
                        }

                        // Store the JSONObject
                        rtnObj.setJSON(jObj);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    httpInterfaceObj.HttpRequestDoInBackgroundErrorDelegate(null);
                    // Dump the Error message
                    // Close the connection
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

            }
            catch (ConnectTimeoutException e)
            {
                e.printStackTrace();
                httpInterfaceObj.HttpRequestDoInBackgroundErrorDelegate(null);
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
                httpInterfaceObj.HttpRequestDoInBackgroundErrorDelegate(null);
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            httpInterfaceObj.HttpRequestDoInBackgroundDelegate(rtnObj);

            return rtnObj;
        }

        @Override
        /**
         * This function is called before the HTTPRequest is made.
         * Typical method used in this function is to show the loading screen.
         */
        protected void onPreExecute()
        {
            httpInterfaceObj.HttpRequestPreExecuteDelegate();
        }

        @Override
        /**
         * This function is called when we need to publish progress update
         * It is called by using this function call: publishProgress
         */
        protected void onProgressUpdate(String... values)
        {

        }

        @Override
        /**
         * The following function is called after the doInBackground is completed.
         * This function runs on the main thread and should only be used for processing UI
         */
        protected void onPostExecute(HttpReturnObject postExecuteMapParams)
        {
            super.onPostExecute(postExecuteMapParams);

            //post notification such that classes can respond to this incident
            httpInterfaceObj.HttpRequestOnPostExecuteUpdateDelegate(postExecuteMapParams);

        }

        /**
         * Function that performs processing of the HttpAsynchronous result and if we know the reply is going to be in JSON format
         * @param response (HttpResponse) - A HttpResponse object that we obtained from the HttpAsynchronous request
         */
        public HttpReturnObject processHttpAsyncResultsJson(HttpResponse response) throws JSONException
        {
            // Create the return object
            HttpReturnObject rtnObj = new HttpReturnObject();
            // Set the return result status
            rtnObj.setStatus(true);

            // Call to the generic function for processing HTTP Asynchronous Results
            HttpReturnObject genericHttpResultsMap = processHttpAsyncResults(response);
            if(genericHttpResultsMap==null)
            {
                Log.w("HttpRequestController","Detected NULL while calling processHttpAsynResults in processHttpAsyncResultsJson");
                return rtnObj;
            }

            String jsonString = genericHttpResultsMap.getRawHttpData();
            if(jsonString==null)
            {
                Log.w("HttpRequestController","Detected NULL for jsonString in processHttpAsyncResultsJson");
                return rtnObj;
            }
            else
            {
                // Set the rawHttpData as part of the return
                rtnObj.setRawHttpData(jsonString);
                Log.w("ReturnJSONString", jsonString);
            }

            try
            {
                // Convert JSON String into JSON Object
                JSONObject jObject = new JSONObject(jsonString);
                if(jObject instanceof JSONObject)
                {
                    // Enter here if we have successfully converted the JSON String to JSON Object
                    rtnObj.setJSON(jObject);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                // Set the return result status as false
                rtnObj.setStatus(false);
            }
            return  rtnObj;
        }


        /**
         * Function that performs processing of the HttpAsynchronous result
         * @param response (HttpResponse) - A HttpResponse object that we obtained from the HttpAsynchronous request
         */
        public HttpReturnObject processHttpAsyncResults(HttpResponse response)
        {
            //create the return object
            HttpReturnObject rtnObj = new HttpReturnObject();
            //set the default return status
            rtnObj.setStatus(true);
            //create a string builder object to parse the HTTP Response
            StringBuilder strBuilder = new StringBuilder();
            //create an integer for holding biggest loop

            //set the maximum line to process to 1 million. After that, the function
            //will return error.
            Integer maxLoop = 1000000;

            if(response==null)
            {
                //set the return status to false to indicate that the processing had failed
                rtnObj.setStatus(false);
                rtnObj.setErrMsg("Detected HttpResponse as null in processHttpAsynResult");

                return rtnObj;
            }

            //we do an additional checking again that the return code is 200 OK before parsing the returned response
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode()!=HttpStatus.SC_OK)
            {
                //set the return status to false to indicate that the processing had failed
                rtnObj.setStatus(false);
                rtnObj.setErrMsg("Detected none SC_OK response code. Response code="+statusLine.getStatusCode());

                return rtnObj;
            }

            HttpEntity entity = response.getEntity();
            if(entity==null)
            {
                //set the return status to false and error reason
                rtnObj.setStatus(false);
                rtnObj.setErrMsg("Detected HttpEntity as NULL in processHttpAsynResult");

                return rtnObj;
            }

            InputStream content;
            try
            {
                content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line = reader.readLine();

                Integer loopCounter=0;

                while(line!=null)
                {

                    strBuilder.append(line);

                    loopCounter = loopCounter + 1;
                    if(loopCounter>=maxLoop)
                    {
                        Log.w("HttpRequestController","Exceeded maxloop in function processHttpAsynResult");
                        //set the return status to false and error reason
                        rtnObj.setStatus(false);
                        rtnObj.setErrMsg("Exceeded maxloop in function processHttpAsynResult");

                        break;
                    }

                    line = reader.readLine();

                } //while(line!=null)

                //If we exited the while loop without error, return the raw HTTP response information
                String rawData = strBuilder.toString();

                if(rawData==null)
                {
                    //Enter here if we are not able to obtain the raw HTTP data information
                    Log.i("HttpRequestController","No rawData information obtained from processHttpAsynResult function");

                    //set the return status to false and provide error reason
                    rtnObj.setStatus(false);
                    rtnObj.setErrMsg("Detected rawData as NULL in function processHttpAsynResult");

                    return rtnObj;
                }

                //Reached here if we receive raw Data
                rtnObj.setRawHttpData(rawData);

            }
            catch (IllegalStateException | IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return rtnObj;
        }

    } // private class performAsynRequestTask



}