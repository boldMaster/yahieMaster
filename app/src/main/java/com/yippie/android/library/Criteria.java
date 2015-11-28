package com.yippie.android.library;

import android.content.Context;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ming on 11/1/2015.
 */
public class Criteria
{
    //DB column name for the filterable criteria
    public static final String COUNTRY = "country_id";
    public static final String STATE = "state_id";
    public static final String LOCATION = "location_id";
    public static final String CATEGORY_GROUP = "category_group_id";
    public static final String CATEGORY = "category_id";

    private static Criteria instance;
    protected int intCountry;
    protected int intState;
    protected int intLocation;
    protected int intCategoryGroup;
    protected int intCategory;

    public static Criteria initialize()
    {
        // Use the application context, which will ensure that you don't accidentally leak an Activity's context.
        if (instance == null)
        {
            instance = new Criteria();
        }

        return instance;
    }

    protected static Criteria getInstance()
    {
        // Initialize the instance of Singleton Class
        // Return the instance
        return Singleton.getInstance().criteria;
    }

    // Constructor
    private Criteria()
    {

    }

    public void onCreate()
    {
        // Initialize the singletons so their instances are bound to the application process.
        initialize();
    }

    public void setCriteria(String strType , int value)
    {
        switch(strType){
            case COUNTRY:
                intCountry = value;
                break;
            case STATE:
                intState = value;
                break;
            case LOCATION:
                intLocation = value;
                break;
            case CATEGORY_GROUP:
                intCategoryGroup = value;
                break;
            case CATEGORY:
                intCategory = value;
                break;
            default:
                break;
        }
    }

    public Integer getCriteria(String strType)
    {
        switch(strType){
            case COUNTRY:
                return this.intCountry;
            case STATE:
                return this.intState;
            case LOCATION:
                return this.intLocation;
            case CATEGORY_GROUP:
                return this.intCategoryGroup;
            case CATEGORY:
                return this.intCategory;
        }
        return 1001;
    }

    public void setAllCriteria(Map<String, Integer> mapCriteria)
    {
        //Process input criteria

    }

    public Map<String, Integer> getAllCriteria()
    {
        Map<String, Integer> mapCriteria = new HashMap<String, Integer>();
        //return an unmodifiable map collection
        mapCriteria.put(COUNTRY,this.intCountry);
        mapCriteria.put(STATE,this.intState);
        mapCriteria.put(LOCATION,this.intLocation);
        mapCriteria.put(CATEGORY_GROUP,this.intCategoryGroup);
        mapCriteria.put(CATEGORY,this.intCategory);
        return Collections.unmodifiableMap(mapCriteria);
    }

    public JSONObject getAllCriteriaInJson()
    {
        Map<String, Integer> mapCriteria = new HashMap<String, Integer>();
        //return an unmodifiable map collection
        mapCriteria.put(COUNTRY,this.intCountry);
        mapCriteria.put(STATE,this.intState);
        mapCriteria.put(LOCATION,this.intLocation);
        mapCriteria.put(CATEGORY_GROUP,this.intCategoryGroup);
        mapCriteria.put(CATEGORY,this.intCategory);
        return new JSONObject(Collections.unmodifiableMap(mapCriteria));
    }

}
