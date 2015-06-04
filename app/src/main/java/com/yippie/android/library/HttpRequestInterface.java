package com.yippie.android.library;
/**
 * Http Request Interface. Please import and include this interface you need
 */
public interface HttpRequestInterface
{
    // This is the delegate method to call before we perform a HTTP Response
    public void HttpRequestPreExecuteDelegate();

    // This is the delegate method to call once we have received a HTTP Response
    public void HttpRequestDoInBackgroundDelegate(HttpReturnObject response);

    // This is the delegate method to call when we received an error in our interface
    public void HttpRequestDoInBackgroundErrorDelegate(HttpReturnObject response);

    // This is the delegate method to call for progress update
    public void HttpRequestProgressUpdateDelegate(HttpReturnObject response);

    // This is the delegate method to call for on post execute update
    public void HttpRequestOnPostExecuteUpdateDelegate(HttpReturnObject response);
}