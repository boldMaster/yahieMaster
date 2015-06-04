package com.yippie.android.library;

import org.json.JSONObject;

public class HttpReturnObject {
    private JSONObject json;
    private String 	errMsg;
    private Boolean status;
    private String rawHttpData;

    // Constructor
    public HttpReturnObject()
    {
        json = new JSONObject();
        errMsg = "";
        status = false;
        rawHttpData = "";
    }

    // Get JSON
    public JSONObject getJSON()
    {
        return json;
    }

    // Get Error Message
    public String getErrMsg()
    {
        return errMsg;
    }

    // Get status
    public Boolean getStatus()
    {
        return status;
    }

    // Get Raw HTTP Data
    public String getRawHttpData()
    {
        return rawHttpData;
    }

    // Set JSON
    public void setJSON(JSONObject newJson)
    {
        json = newJson;
    }

    // Set Error Message
    public void setErrMsg(String newErrMsg)
    {
        errMsg = newErrMsg;
    }

    // Set Status
    public void setStatus(Boolean newStatus)
    {
        status = newStatus;
    }

    // Set Raw HTTP Data
    public void setRawHttpData(String newRawHttpData)
    {
        rawHttpData = newRawHttpData;
    }

}