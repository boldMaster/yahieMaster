package com.yippie.android.classes;

/**
 * Created by Tan on 5/20/2015.
 */
public class ShopInfo
{
    /**
     * Sub class of the ShopInfo
     * Note: This is used in the
     */
    public static class ShopInfoLite {
        public Integer shopId;
        public String shopImageUrl;
        public String shopTitle;
        public String shopAddressFull;
        public String shopContact;
        public String shopMapLongitude;
        public String shopMapLatitude;

        /**
         * Constructor
         */
        public ShopInfoLite(int shopId,String shopImageUrl,String shopTitle,String shopAddressFull,String shopContact,String shopMapLongitude,String shopMapLatitude) {
            this.shopId = shopId;
            this.shopImageUrl = shopImageUrl;
            this.shopTitle = shopTitle;
            this.shopAddressFull = shopAddressFull;
            this.shopContact = shopContact;
            this.shopMapLongitude = shopMapLongitude;
            this.shopMapLatitude = shopMapLatitude;
        }
    }
}
