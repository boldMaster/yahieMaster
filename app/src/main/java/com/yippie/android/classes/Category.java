package com.yippie.android.classes;

import com.yippie.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Class that handle use in the multi level category
 */
public class Category
{
    int categoryId;
    int categoryImage;
    String categoryName;
    List<SubCategory> subCategoryList;

    /**
     * Constructor
     * @param categoryId        - The id index of the category
     * @param image             - The image resource for category image. E.g: R.drawable.xxxx
     * @param categoryName      - The name of the category
     * @param subCategoryList   - The list of sub category list
     */
    public Category(int categoryId, int image, String categoryName, List<SubCategory> subCategoryList)
    {
        this.categoryId = categoryId;
        this.categoryImage = image;
        this.categoryName = categoryName;
        this.subCategoryList = subCategoryList;
    }

    /**
     * Getter
     */
    public int getCategoryId(){
        return categoryId;
    }
    public int getCategoryImage(){
        return categoryImage;
    }
    public String getCategoryName(){
        return categoryName;
    }
    public List<SubCategory> getSubCategoryList(){
        return subCategoryList;
    }


    /**
     * Sub class of the category item. It will handle the sub category
     */
    public static class SubCategory
    {
        int subCategoryId;
        String subCategoryName;
        List<CategoryItem> categoryItemList;

        /**
         * Constructor
         * @param subCategoryId     - The id index of the category
         * @param subCategoryName   - The name of the sub category
         * @param categoryItemList  - The further expandable category item in this sub category
         */
        public SubCategory(int subCategoryId, String subCategoryName, List<CategoryItem>categoryItemList)
        {
            this.subCategoryId = subCategoryId;
            this.subCategoryName = subCategoryName;
            this.categoryItemList = categoryItemList;
        }

        /**
         * Getter
         */
        public int getSubCategoryId(){
            return subCategoryId;
        }
        public String getSubCategoryName(){
            return subCategoryName;
        }
        public List<CategoryItem>getCategoryItemList(){
            return categoryItemList;
        }


    }

    /**
     * Sub class of the category item. It will handle the item list object
     */
    public static class CategoryItem
    {
        int categoryItemId;
        String categoryItemName;

        /**
         * Constructor
         * @param categoryItemId    - The name of the category item
         * @param categoryItemName  - The
         */
        public CategoryItem(int categoryItemId, String categoryItemName)
        {
//                this.categoryItemId = categoryItemId;
            this.categoryItemName = categoryItemName;
        }

        /**
         * Getter
         */
        public int getCategoryItemId(){
            return categoryItemId;
        }
        public String getCategoryItemName(){
            return categoryItemName;
        }
    }



    /**
     * Function to get the list of category menu items
     */
    public static List<Category> getCategoryList()
    {
        // Initialize the list
        List<Category> categoryList = new ArrayList<>();

        // Tourism Hotspot
        Category tourismHotspot = getTourismHotspotCategory();
        categoryList.add(tourismHotspot);

        // Accommodation
        Category accommodation = getAccommodationCategory();
        categoryList.add(accommodation);

        // Food and Beverage section
        Category foodBeverage = getFoodBeverageCategory();
        categoryList.add(foodBeverage);

        // Clothing and Apparel
        Category clothingApparel = getClothingApparelCategory();
        categoryList.add(clothingApparel);

        // Shops and Retailers
        Category shopsRetailers = getShopsRetailersCategory();
        categoryList.add(shopsRetailers);

        // Petrol Station
        Category petrolStation = getPetrolStationCategory();
        categoryList.add(petrolStation);

        // Auto Repair Workshop
        Category autoRepair = getAutoRepairWorkshopCategory();
        categoryList.add(autoRepair);

        // hHir and Beauty Saloon
        Category beautySaloon = getHairbeautySaloonCategory();
        categoryList.add(beautySaloon);

        return categoryList;
    }

    /**
     * Function to return the complete tourism hotpost category object
     */
    private static Category getTourismHotspotCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        // Construct the tourism hotspot object & return
        String categoryName = "Tourism Hotspot";
        int categoryImage = R.drawable.food_icon;

        return new Category(1, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to return the complete Accommodation category object
     */
    private static Category getAccommodationCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

         /* Hotel Section */
        List<CategoryItem> hotelItems = new ArrayList<>();
        hotelItems.add(new CategoryItem(1, "Hotel - Luxury (5 Star)"));
        hotelItems.add(new CategoryItem(2, "Hotel - Firsy Class (4 Star)"));
        hotelItems.add(new CategoryItem(3, "Hotel - Comfort (3 Star)"));
        hotelItems.add(new CategoryItem(4, "Hotel - Standard (2 Star)"));
        hotelItems.add(new CategoryItem(5, "Apartments"));
        hotelItems.add(new CategoryItem(6, "Guesthouse"));
        hotelItems.add(new CategoryItem(7, "SHOW ALL"));
        // Add Hotel into the list
        subCategoryList.add(new SubCategory(1, "Hotel", hotelItems));

        // Construct the shopping object & return
        String categoryName = "Accommodation";
        int categoryImage = R.drawable.food_icon;

        return new Category(2, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to return the complete Food & Beverage category object
     */
    private static Category getFoodBeverageCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        /* Fine Dining Section */
        List<CategoryItem> fineDiningItems = new ArrayList<>();
        // Add Fine Dining into the list
        subCategoryList.add(new SubCategory(1, "Fine Dining", fineDiningItems));

        /* Resaurant Section */
        List<CategoryItem> restaurantItems = new ArrayList<>();
        restaurantItems.add(new CategoryItem(1, "Malay"));
        restaurantItems.add(new CategoryItem(2, "Chinese"));
        restaurantItems.add(new CategoryItem(3, "Indian"));
        restaurantItems.add(new CategoryItem(4, "Japanese"));
        restaurantItems.add(new CategoryItem(5, "Korean"));
        restaurantItems.add(new CategoryItem(6, "Middle East"));
        restaurantItems.add(new CategoryItem(7, "French"));
        restaurantItems.add(new CategoryItem(8, "Italian"));
        restaurantItems.add(new CategoryItem(9, "SHOW ALL"));
        // Add Resaurant into the list
        subCategoryList.add(new SubCategory(2, "Restaurant", restaurantItems));

        /* Fast Food Section */
        List<CategoryItem> fastFoodItems = new ArrayList<>();
        fastFoodItems.add(new CategoryItem(1, "Domino's Pizza"));
        fastFoodItems.add(new CategoryItem(2, "A & W"));
        fastFoodItems.add(new CategoryItem(3, "Pizza Hut"));
        fastFoodItems.add(new CategoryItem(4, "McDonaldt"));
        fastFoodItems.add(new CategoryItem(5, "Marrybrown"));
        fastFoodItems.add(new CategoryItem(6, "Others"));
        fastFoodItems.add(new CategoryItem(7, "SHOW ALL"));
        // Add Fast Food into the list
        subCategoryList.add(new SubCategory(3, "Fast Food", fastFoodItems));

        /* Bakery Section */
        List<CategoryItem> bakeryItems = new ArrayList<>();
        // Add Bakery into the list
        subCategoryList.add(new SubCategory(4, "Bakery", bakeryItems));

         /* Food Courts Section */
        List<CategoryItem> foodCourtsItems = new ArrayList<>();
        // Add Food Courts into the list
        subCategoryList.add(new SubCategory(5, "Food Courts", foodCourtsItems));

         /* Cafe Section */
        List<CategoryItem> cafeItems = new ArrayList<>();
        // Add Cafe into the list
        subCategoryList.add(new SubCategory(6, "Cafe", cafeItems));

         /* Brasserie and Bistro Section */
        List<CategoryItem> brasserieBistroItems = new ArrayList<>();
        // Add Brasserie and Bistro into the list
        subCategoryList.add(new SubCategory(7, "Brasserie and Bistro", brasserieBistroItems));

         /* Pub Section */
        List<CategoryItem> pubItems = new ArrayList<>();
        // Add Pub into the list
        subCategoryList.add(new SubCategory(8, "Pub", pubItems));

        // Construct the food & beverage object & return
        String categoryName = "Food & Beverage";
        int categoryImage = R.drawable.food_icon;

        return new Category(3, categoryImage, categoryName, subCategoryList);

    }

    /**
     * Function to return the complete Clothing and Apparel category object
     */
    private static Category getClothingApparelCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        /* Men Section */
        List<CategoryItem> menItems = new ArrayList<>();
        // Add Men into the list
        subCategoryList.add(new SubCategory(1, "Men", menItems));

        /* Woman Section */
        List<CategoryItem> womanItems = new ArrayList<>();
        // Add Womanr into the list
        subCategoryList.add(new SubCategory(2, "Woman", womanItems));

        /* Children Section */
        List<CategoryItem> childrenItems = new ArrayList<>();
        // Add Children into the list
        subCategoryList.add(new SubCategory(3, "Children", childrenItems));

        /* Cultural Section */
        List<CategoryItem> culturalItems = new ArrayList<>();
        // Add Cultural into the list
        subCategoryList.add(new SubCategory(4, "Cultural", culturalItems));

        /* Others Section */
        List<CategoryItem> othersItems = new ArrayList<>();
        // Add Others into the list
        subCategoryList.add(new SubCategory(5, "Others", othersItems));

        /* Show all Section */
        List<CategoryItem> showAllItems = new ArrayList<>();
        // Add Show all into the list
        subCategoryList.add(new SubCategory(6, "SHOW ALL", showAllItems));

        // Construct the Clothing and Apparel object & return
        String categoryName = "Clothing and Apparel";
        int categoryImage = R.drawable.food_icon;

        return new Category(4, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to return the complete shops and retailers category object
     */
    private static Category getShopsRetailersCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        // Add Department Store into the list
        subCategoryList.add(new SubCategory(1, "Department Store", new ArrayList<CategoryItem>()));

        // Add Electrical into the list
        subCategoryList.add(new SubCategory(1, "Electrical", new ArrayList<CategoryItem>()));

        // Add Convenience Store into the list
        subCategoryList.add(new SubCategory(1, "Convenience Store", new ArrayList<CategoryItem>()));

        // Add Pharmacy into the list
        subCategoryList.add(new SubCategory(1, "Pharmacy", new ArrayList<CategoryItem>()));

        // Add Photo Studio into the list
        subCategoryList.add(new SubCategory(1, "Photo Studio", new ArrayList<CategoryItem>()));

        // Construct the shopping object & return
        String categoryName = "Shops and Retailers";
        int categoryImage = R.drawable.food_icon;

        return new Category(5, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to return the complete petrol station category object
     */
    private static Category getPetrolStationCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        // Add Shell into the list
        subCategoryList.add(new SubCategory(1, "Shell", new ArrayList<CategoryItem>()));

        // Add Caltex into the list
        subCategoryList.add(new SubCategory(2, "Caltex", new ArrayList<CategoryItem>()));

        // Add Petron into the list
        subCategoryList.add(new SubCategory(3, "Petron", new ArrayList<CategoryItem>()));

        // Add Petronas into the list
        subCategoryList.add(new SubCategory(4, "Petronas", new ArrayList<CategoryItem>()));

        // Add SHOW ALL into the list
        subCategoryList.add(new SubCategory(5, "SHOW ALL", new ArrayList<CategoryItem>()));

        // Construct the petrol station object & return
        String categoryName = "Petrol Station";
        int categoryImage = R.drawable.food_icon;

        return new Category(6, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to return the complete auto repair workshop category object
     */
    private static Category getAutoRepairWorkshopCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        // Construct the Auto Repair Workshop object & return
        String categoryName = "Auto Repair Workshop";
        int categoryImage = R.drawable.food_icon;

        return new Category(7, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to return the complete Hair and Beauty Saloon category object
     */
    private static Category getHairbeautySaloonCategory()
    {
        // List down the sub category object

        List<SubCategory> subCategoryList = new ArrayList<>();

        // Add Medicure and Pedicure into the list
        subCategoryList.add(new SubCategory(1, "Medicure and Pedicure", new ArrayList<CategoryItem>()));

        // Add Massage Centres into the list
        subCategoryList.add(new SubCategory(2, "Massage Centres", new ArrayList<CategoryItem>()));

        // Add Beauty Saloon into the list
        subCategoryList.add(new SubCategory(3, "Beauty Saloon", new ArrayList<CategoryItem>()));

        // Add Show all into the list
        subCategoryList.add(new SubCategory(4, "SHOW ALL", new ArrayList<CategoryItem>()));

        // Construct the Clothing and Apparel object & return
        String categoryName = "Clothing and Apparel";
        int categoryImage = R.drawable.food_icon;

        return new Category(8, categoryImage, categoryName, subCategoryList);
    }

    /**
     * Function to get the category by category id
     */
    public static Category getCategoryById(Integer categoryId)
    {
        // Get the category list
        List<Category> categorylist = getCategoryList();
        Category category = null;

        // Loop through the list, and find the subCategory list
        for(Category cat : categorylist){
            if(categoryId.equals(cat.getCategoryId())){
                category = cat;
            }
        }

        return category;
    }

    /**
     * Function to get the category by category id
     */
    public SubCategory getSubCategoryById(Integer subCategoryId)
    {
        Category mainCategory = getCategoryById(this.categoryId);

        // Get the category list
        List<SubCategory> subCategorylist = mainCategory.getSubCategoryList();
        SubCategory subCategory = null;

        // Loop through the list, and find the subCategory list
        for(SubCategory cat : subCategorylist){
            if(subCategoryId.equals(cat.getSubCategoryId())){
                subCategory = cat;
            }
        }

        return subCategory;
    }

    public static final int MAIN_CATEGORY = 1;
    public static final int SECOND_CATEGORY = 2;
    public static final int THIRD_CATEGORY = 3;
}