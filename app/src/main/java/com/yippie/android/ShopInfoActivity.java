package com.yippie.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yippie.android.library.HttpRequestInterface;
import com.yippie.android.library.HttpReturnObject;

public class ShopInfoActivity extends Activity implements HttpRequestInterface
{
    ImageView shopImage;
    ImageView btnLocationGo;
    ImageView btnLocationPhone;
    ImageView btnLocationSite;
    ImageView btnLocationAddFav;
    ImageView btnLocationRemoveFav;
    TextView shopName;
    TextView shopCategory;
    TextView shopAddress;
    LinearLayout shopPromotionCont;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout
        setContentView(R.layout.shop_info);

        // Import Shop Info assets
        shopImage = (ImageView) findViewById(R.id.shop_image);
        shopName = (TextView) findViewById(R.id.shop_name);
        shopCategory = (TextView) findViewById(R.id.shop_category);
        shopAddress = (TextView) findViewById(R.id.shop_address);
        shopPromotionCont = (LinearLayout) findViewById(R.id.location_promotion_list);

        // Shop action image button assets
         btnLocationGo = (ImageView) findViewById(R.id.btn_location_go);
         btnLocationPhone = (ImageView) findViewById(R.id.btn_location_phone);
         btnLocationSite = (ImageView) findViewById(R.id.btn_location_page);
         btnLocationAddFav = (ImageView) findViewById(R.id.btn_location_favorite);
         btnLocationRemoveFav = (ImageView) findViewById(R.id.btn_location_favorite);
        
        /* Set the click event for each button */

        // Go location button Click Event
        btnLocationGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        // Phone shop button Click Event
        btnLocationPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:01-2345678"));
                startActivity(intent);
            }
        });

        // Go main site button Click Event
        btnLocationSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        // Add Favorite image button Click Event
        btnLocationAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        // Remove Favorite image button Click Event
        btnLocationRemoveFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public void HttpRequestPreExecuteDelegate() {

        // Put your progress bar code in here

    }

    @Override
    public void HttpRequestDoInBackgroundDelegate(HttpReturnObject response) {

    }

    @Override
    public void HttpRequestDoInBackgroundErrorDelegate(HttpReturnObject response) {

    }

    @Override
    public void HttpRequestProgressUpdateDelegate(HttpReturnObject response) {

    }

    @Override
    public void HttpRequestOnPostExecuteUpdateDelegate(HttpReturnObject response) {

    }

}