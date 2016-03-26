package com.yippie.android.classes;

import com.yippie.android.library.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tan on 1/23/2016.
 * This class will store the category selection by user in
 */
public class CriteriaV2
{
    protected int selectedCountry;
    protected int selectedState;
    protected int selectedLocation;
    protected int selectedCategoryGroup;
    protected int selectedCategory;
    protected int selectedSubCategory;

    /**
     * Get instance from Singleton
     * @return CriterialV2 instance
     */
    public static CriteriaV2 getInstance() {
        // Initialize the instance of Singleton Class
        // Return the instance
        return Singleton.getInstance().criteriaV2;
    }

    /**
     * Constructor
     */
    public CriteriaV2() {
        this.selectedCountry = 60;
        this.selectedState = 0;
        this.selectedLocation = 0;
        this.selectedCategoryGroup = 0;
        this.selectedCategory = 0;
        this.selectedSubCategory = 0;
    }
    
    // Setter
    public void setCountry(int selectedCountry){
        this.selectedCountry = selectedCountry;
    }
    public void setState(int selectedState){
        this.selectedState = selectedState;
    }
    public void setLocation(int selectedLocation){
        this.selectedLocation = selectedLocation;
    }
    public void setCategoryGroup(int selectedCategoryGroup){
        this.selectedCategoryGroup = selectedCategoryGroup;
    }
    public void setCategory(int selectedCategory){
        this.selectedCategory = selectedCategory;
    }
    public void setSubCategory(int selectedSubCategory){
        this.selectedSubCategory = selectedSubCategory;
    }

    // Getter
    public int getSelectedCountry(){
        return this.selectedCountry;
    }
    public int getSelectedState(){
        return this.selectedState;
    }
    public int getSelectedLocation(){
        return this.selectedLocation;
    }
    public int getCategoryGroup(){
        return this.selectedCategoryGroup;
    }
    public int getCategory(){
        return this.selectedCategory;
    }
    public int getSubCategory(){
        return this.selectedSubCategory;
    }

    /**
     * Function to reset the criteria
     */
    public static void reset() {
        // Reset it by reconstruct the object
        Singleton.getInstance().criteriaV2 = new CriteriaV2();
    }

    public void resetCountry(){
        this.selectedCountry = 0;
    }
    public void resetState(){
        this.selectedState = 0;
    }
    public void resetLocation(){
        this.selectedLocation = 0;
    }
    public void resetCategoryGroup(){
        this.selectedCategoryGroup = 0;
    }
    public void resetCategory(){
        this.selectedCategory = 0;
    }
    public void resetSubCategory(){
        this.selectedSubCategory = 0;
    }
    
    /**
     * Function to construct JSON encoded critiria
     */
    public static String getEncoded() {

        // Get current Criteria
        CriteriaV2 currentCriteria = getInstance();

        // COnstruct new JSONObject to store the Criteria
        JSONObject json = new JSONObject();
        try {
            // Store all Criteria info JSONObject
            json.put("country",currentCriteria.getSelectedCountry());
            json.put("state",currentCriteria.getSelectedState());
            json.put("location",currentCriteria.getSelectedLocation());
            json.put("categoryGroup",currentCriteria.getCategoryGroup());
            json.put("category",currentCriteria.getCategory());
            json.put("subCategory",currentCriteria.getSubCategory());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }


}
