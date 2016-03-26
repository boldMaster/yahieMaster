package com.yippie.android.classes;

import android.content.ContentValues;
import android.database.Cursor;

import com.yippie.android.library.SqliteDB;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * Written by Zi Yang
 * Custom Class that handle use in the multi level category
 */
public class CategoryRef {
    /**
     * Sub class of the category item. It will handle the Category Group Obj
     */
    public static class CategoryGroup {
        public Integer categoryGroupId;
        public String categoryGroupName;

        /**
         * Constructor
         */
        public CategoryGroup(Integer categoryGroupId, String categoryGroupName) {
            this.categoryGroupId = categoryGroupId;
            this.categoryGroupName = categoryGroupName;
        }

        /**
         * Function to retreieve the list of Category Group from Database
         */
        public static List<CategoryGroup>getCategoryGroupList() {
            List<CategoryGroup> newList = new ArrayList<>();

            // Set table name
            String tableName = SqliteDB.TABLE_CATEGORY_GROUP;
            SqliteDB db = SqliteDB.getInstance();

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, null, null, null, null, null, null);
            if(cursor.getCount() > 0) {
                // If the result is not empty
                while (cursor.moveToNext()) {
                    Integer categoryGroupId = cursor.getInt(cursor.getColumnIndex("category_group_id"));
                    String categoryGroupName = cursor.getString(cursor.getColumnIndex("category_group_name"));

                    // Construct new Category Group Object
                    CategoryGroup newObj = new CategoryGroup(categoryGroupId,categoryGroupName);
                    newList.add(newObj);
                }
            }

            // Close the cursor
            cursor.close();

            return newList;
        }

        /**
         * Function to get the category group information
         */
        public static CategoryGroup getCategoryGroupInfo(Integer categoryGroupId) {
            CategoryGroup newInfo = null;

            // Set table name
            String tableName = SqliteDB.TABLE_CATEGORY_GROUP;
            SqliteDB db = SqliteDB.getInstance();

            String whereQuery = "category_group_id=?";
            String whereArgs[] = {String.valueOf(categoryGroupId)};
            String limit = "1";

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);
            if(cursor.getCount() > 0) {
                // If the result is not empty
                while (cursor.moveToNext()) {
                    String categoryGroupName = cursor.getString(cursor.getColumnIndex("category_group_name"));

                    // Construct new Category Group Object
                    newInfo = new CategoryGroup(categoryGroupId,categoryGroupName);
                }
            }

            return newInfo;
        }

        /**
         * Function to insert / update the category group information
         */
        public static void bulkInsertORRupdateCategoryGroup(List<CategoryGroup> categoryGroupList)
        {
            // Define table name
            String tableName = SqliteDB.TABLE_CATEGORY_GROUP;
            SqliteDB db = SqliteDB.getInstance();

            // Array that store the ContentValues
            List<ContentValues> contentValuesList = new ArrayList<>();

            // Loop through all category group object and create content values object
            for(CategoryGroup singleCategoryGroup : categoryGroupList)
            {
                ContentValues value = new ContentValues();

                value.put("category_group_id", singleCategoryGroup.categoryGroupId);
                value.put("category_group_name", singleCategoryGroup.categoryGroupName);

                contentValuesList.add(value);
            }

            db.bulkInsertOrUpdateDBRows(tableName, contentValuesList);
        }

    }

    /**
     * Sub class of the category group class. It will handle the Category Obj
     */
    public static class Category {
        public Integer categoryId;
        public Integer categoryGroupId;
        public String categoryName;

        /**
         * Constructor
         */
        public Category(Integer categoryId, Integer categoryGroupId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryGroupId = categoryGroupId;
            this.categoryName = categoryName;
        }

        /**
         * Function to retreieve the list of Category from Database
         */
        public static List<Category>getCategoryList(Integer categoryGroupId) {
            List<Category> newList = new ArrayList<>();

            // Set table name
            String tableName = SqliteDB.TABLE_CATEGORY;
            SqliteDB db = SqliteDB.getInstance();

            String whereQuery = "category_group_id=?";
            String whereArgs[] = {String.valueOf(categoryGroupId)};

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, null);
            if(cursor.getCount() > 0) {
                // If the result is not empty
                while (cursor.moveToNext()) {
                    Integer categoryId = cursor.getInt(cursor.getColumnIndex("category_id"));
                    String categoryName = cursor.getString(cursor.getColumnIndex("category_name"));

                    // Construct new Category Group Object
                    Category newObj = new Category(categoryId,categoryGroupId,categoryName);
                    newList.add(newObj);
                }
            }

            // Close the cursor
            cursor.close();

            return newList;
        }

        /**
         * Function to get the category group information
         */
        public static Category getCategoryInfo(Integer categoryGroupId, Integer categoryId) {
            Category newInfo = null;

            // Set table name
            String tableName = SqliteDB.TABLE_CATEGORY;
            SqliteDB db = SqliteDB.getInstance();

            String whereQuery = "category_id=?";
            String whereArgs[] = {String.valueOf(categoryId)};
            String limit = "1";

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);
            if(cursor.getCount() > 0) {
                // If the result is not empty
                while (cursor.moveToNext()) {
                    String categoryName = cursor.getString(cursor.getColumnIndex("category_name"));

                    // Construct new Category Group Object
                    newInfo = new Category(categoryId,categoryGroupId,categoryName);
                }
            }

            // Close the cursor
            cursor.close();

            return newInfo;
        }

        /**
         * Function to insert / update the category information
         */
        public static void bulkInsertORRupdateCategory(List<Category> categoryList) {
            // Define table name
            String tableName = SqliteDB.TABLE_CATEGORY;
            SqliteDB db = SqliteDB.getInstance();

            // Array that store the ContentValues
            List<ContentValues> contentValuesList = new ArrayList<>();

            // Loop through all category group object and create content values object
            for (Category singleCategory : categoryList) {
                ContentValues value = new ContentValues();

                value.put("category_group_id", singleCategory.categoryGroupId);
                value.put("category_id", singleCategory.categoryId);
                value.put("category_name", singleCategory.categoryName);

                contentValuesList.add(value);
            }

            db.bulkInsertOrUpdateDBRows(tableName, contentValuesList);
        }
    }

    /**
     * Sub class of the category class. It will handle the sub category
     */
    public static class SubCategory {
        public Integer subCategoryId;
        public Integer categoryId;
        public Integer categoryGroupId;
        public String subCategoryName;

        /**
         * Constructor
         */
        public SubCategory(Integer subCategoryId, Integer categoryId, Integer categoryGroupId, String subCategoryName) {
            this.subCategoryId = subCategoryId;
            this.categoryId = categoryId;
            this.categoryGroupId = categoryGroupId;
            this.subCategoryName = subCategoryName;
        }

        /**
         * Function to retreieve the list of Sub Category from Database, based on the Category Id
         */
        public static List<SubCategory>getSubCategoryList(Integer categoryId) {
            List<SubCategory> newList = new ArrayList<>();

            // Set table name
            String tableName = SqliteDB.TABLE_SUB_CATEGORY;
            SqliteDB db = SqliteDB.getInstance();

            String whereQuery = "category_id=?";
            String whereArgs[] = {String.valueOf(categoryId)};

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, null);
            if(cursor.getCount() > 0) {
                // If the result is not empty
                while (cursor.moveToNext()) {
                    Integer categoryGroupId = cursor.getInt(cursor.getColumnIndex("category_group_id"));
                    Integer subCategoryId = cursor.getInt(cursor.getColumnIndex("sub_category_id"));
                    String subCategoryName = cursor.getString(cursor.getColumnIndex("sub_category_name"));

                    // Construct new Category Group Object
                    SubCategory newObj = new SubCategory(subCategoryId,categoryId,categoryGroupId,subCategoryName);
                    newList.add(newObj);
                }
            }

            // Close the cursor
            cursor.close();

            return newList;
        }

        /**
         * Function to get the sub category information
         */
        public static SubCategory getSubCategoryInfo(Integer subCategoryId) {
            SubCategory newInfo = null;

            // Set table name
            String tableName = SqliteDB.TABLE_SUB_CATEGORY;
            SqliteDB db = SqliteDB.getInstance();

            String whereQuery = "sub_category_id=?";
            String whereArgs[] = {String.valueOf(subCategoryId)};
            String limit = "1";

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);
            if(cursor.getCount() > 0) {
                // If the result is not empty
                while (cursor.moveToNext()) {
                    Integer categoryGroupId = cursor.getInt(cursor.getColumnIndex("category_group_id"));
                    Integer categoryId = cursor.getInt(cursor.getColumnIndex("category_id"));
                    String subCategoryName = cursor.getString(cursor.getColumnIndex("sub_category_name"));

                    // Construct new Category Group Object
                    newInfo = new SubCategory(subCategoryId,categoryId,categoryGroupId,subCategoryName);
                }
            }

            // Close the cursor
            cursor.close();

            return newInfo;
        }

        /**
         * Function to insert / update the sub category information
         */
        public static void bulkInsertORRupdateSubCategory(List<SubCategory> subCategoryList) {
            // Define table name
            String tableName = SqliteDB.TABLE_SUB_CATEGORY;
            SqliteDB db = SqliteDB.getInstance();

            // Array that store the ContentValues
            List<ContentValues> contentValuesList = new ArrayList<>();

            // Loop through all category group object and create content values object
            for (SubCategory singleSubCategory : subCategoryList) {
                ContentValues value = new ContentValues();

                value.put("category_group_id", singleSubCategory.categoryGroupId);
                value.put("category_id", singleSubCategory.categoryId);
                value.put("sub_category_id", singleSubCategory.subCategoryId);
                value.put("sub_category_name", singleSubCategory.subCategoryName);

                contentValuesList.add(value);
            }

            db.bulkInsertOrUpdateDBRows(tableName, contentValuesList);
        }
    }
}