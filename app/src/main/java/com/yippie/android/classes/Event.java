package com.yippie.android.classes;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.facebook.AccessToken;
import com.yippie.android.library.SqliteDB;
import com.yippie.android.library.Utility;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.yippie.android.library.Utility.changeStringtoDate;

/**
 * Created by Ming on 2/1/2016.
 */
public class Event
{
    private Integer eventId;
    private Integer placeId;
    private Integer advertiserId;
    private String eventTitle;
    private String eventDesc;
    private Integer amountPerVoucher;
    private Integer totalAmount;
    private Integer totalWinner;
    private java.util.Date startDate;
    private java.util.Date endDate;
    private Integer redeemDuration;
    private java.util.Date updatedAt;

    // Constructor
    public Event()
    {
        this.eventId = 0;
        this.placeId = 0;
        this.advertiserId = 0;
        this.eventTitle = "";
        this.eventDesc = "";
        this.totalAmount = 0;
        this.amountPerVoucher = 0;
        this.totalWinner = 0;
        this.startDate = new Date(0);
        this.endDate = new Date(0);
        this.redeemDuration = 0;
        this.updatedAt =  new Date(0);
    }

    // Constructor
    public Event(Integer eid, Integer pid, Integer aid, String eTitle, String eDesc, Integer apv, Integer ta, Integer tw, String startD, String endD,Integer redeemD, String updAt)
    {
        this.eventId = eid;
        this.placeId = pid;
        this.advertiserId = aid;
        this.eventTitle = eTitle;
        this.eventDesc = eDesc;
        this.amountPerVoucher = apv;
        this.totalAmount = ta;
        this.totalWinner = tw;
        this.startDate = changeStringtoDate(startD);
        this.endDate = changeStringtoDate(endD);
        this.redeemDuration = redeemD;
        this.updatedAt = changeStringtoDate(updAt);
    }

    public void setEventId(Integer eid){this.eventId = eid;}
    public void setPlaceId(Integer pid){this.placeId = pid;}
    public void setAdvertiserId(Integer adId){this.advertiserId = adId;}
    public void setEventTitle(String eTitle){this.eventTitle = eTitle;}
    public void setEventDesc(String eDesc){this.eventDesc = eDesc;}
    public void setAmountPerVoucher(Integer amount){this.amountPerVoucher = amount;}
    public void setTotalAmount(Integer ta){this.totalAmount = ta;}
    public void setTotalWinner(Integer winners){this.totalWinner = winners;}
    public void setStartDate(Date sDate){this.startDate = sDate;}
    public void setEndDate(Date eDate){this.endDate = eDate;}
    public void setRedeemDuration(Integer duration){this.redeemDuration = duration;}

    public Integer getEventId(){return this.eventId;}
    public Integer getPlaceId(){return this.placeId;}
    public Integer getAdvertiserId(){return this.advertiserId;}
    public String getEventTitle(){return this.eventTitle;}
    public String getEventDesc(){return this.eventDesc;}
    public Integer getAmount(){return this.amountPerVoucher;}
    public Integer getTotalWinner(){return this.totalWinner;}
    public java.util.Date getStartDate(){return this.startDate;}
    public java.util.Date getEndDate(){return this.endDate;}
    public Integer getDuration(){return this.redeemDuration;}

    public Boolean insertEvent(Integer eventId, Integer placeId, Integer advertiserId, String eventTitle, String eventDesc, Integer amountPerVoucher, Integer totalAmount, Integer totalWinner, Date startDate, Date endDate, Integer duration, Date updatedAt)
    {
        // Define table name
        String tableName = SqliteDB.TABLE_EVENT;
        SqliteDB db = SqliteDB.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        // Construct the Content Values
        ContentValues insertInfo = new ContentValues();
        insertInfo.put("event_id", eventId);
        insertInfo.put("place_id", placeId);
        insertInfo.put("advertiser_id", advertiserId);
        insertInfo.put("event_title", eventTitle);
        insertInfo.put("event_desc", eventDesc);
        insertInfo.put("total_amount", totalAmount);
        insertInfo.put("total_winner", totalWinner);
        insertInfo.put("amount_per_voucher", amountPerVoucher);
        insertInfo.put("start_date", df.format(startDate));
        insertInfo.put("end_date", df.format(endDate));
        insertInfo.put("redeem_duration", duration);
        insertInfo.put("updated_at", df.format(updatedAt));
        System.out.println("INSERT INFO::::"+insertInfo.toString());
        return db.insertOrUpdateDBRow(tableName,insertInfo);
    }

    public Boolean insertEvent()
    {
        // Define table name
        String tableName = SqliteDB.TABLE_EVENT;
        SqliteDB db = SqliteDB.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        // Construct the Content Values
        ContentValues insertInfo = new ContentValues();
        insertInfo.put("event_id", this.eventId);
        insertInfo.put("place_id", this.placeId);
        insertInfo.put("advertiser_id", this.advertiserId);
        insertInfo.put("event_title", this.eventTitle);
        insertInfo.put("event_desc", this.eventDesc);
        insertInfo.put("total_amount", this.totalAmount);
        insertInfo.put("total_winner", this.totalWinner);
        insertInfo.put("amount_per_voucher", this.amountPerVoucher);
        insertInfo.put("start_date", df.format(this.startDate));
        insertInfo.put("end_date", df.format(this.endDate));
        insertInfo.put("redeem_duration", this.redeemDuration);
        insertInfo.put("updated_at", df.format(this.updatedAt));
        System.out.println("INSERT INFO::::"+insertInfo.toString());
        return db.insertOrUpdateDBRow(tableName,insertInfo);
    }

    public static Event getActiveEvent()
    {
        Event currentEvent = null;

        // Define table name
        String tableName = SqliteDB.TABLE_EVENT;
        // Get Sqlite DB instance
        SqliteDB db = SqliteDB.getInstance();
        // Set the data retrieve limit to max 1
        String limit = "1";
        String currentTimeStamp = Utility.getTimeStamp();
        // Get the active Event
        String whereQuery = "start_date <= ? AND end_date >= ?";
        String whereArgs[] = {currentTimeStamp, currentTimeStamp};
        String orderBy = "start_date";

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, orderBy, limit);
        // No Active Event found
        if(cursor.getCount()<=0)
        {
            // Enter here if the result is empty, means the no active Event
            // Return Null to be handle by the http call request
        } else {
            // If the result is not empty
            while(cursor.moveToNext())
            {
                Integer eid = cursor.getInt(cursor.getColumnIndex("event_id"));
                Integer pid = cursor.getInt(cursor.getColumnIndex("place_id"));
                Integer aid = cursor.getInt(cursor.getColumnIndex("advertiser_id"));
                String eTitle = cursor.getString(cursor.getColumnIndex("event_title"));
                String eDesc = cursor.getString(cursor.getColumnIndex("event_desc"));
                Integer ta = cursor.getInt(cursor.getColumnIndex("total_amount"));
                Integer tw = cursor.getInt(cursor.getColumnIndex("total_winner"));
                Integer apv = cursor.getInt(cursor.getColumnIndex("amount_per_voucher"));
                String startD = cursor.getString(cursor.getColumnIndex("start_date"));
                String endD = cursor.getString(cursor.getColumnIndex("end_date"));
                Integer redeemD = cursor.getInt(cursor.getColumnIndex("redeem_duration"));
                String updAt = cursor.getString(cursor.getColumnIndex("updated_at"));

                currentEvent = new Event(eid, pid, aid, eTitle, eDesc, apv, ta, tw, startD, endD, redeemD, updAt);
                System.out.println("RESULT::"+currentEvent.getEventTestString());
            }
            // Close cursor
            cursor.close();
        }

        return currentEvent;
    }

    public Boolean isEventExist(Integer eid){
            // Define table name
            String tableName = SqliteDB.TABLE_EVENT;
            // Get Sqlite DB instance
            SqliteDB db = SqliteDB.getInstance();
            // Set the data retrieve limit to max 1
            String limit = "1";
            // Get the Event by id
            String whereQuery = "event_id<=?";
            String whereArgs[] = {eid.toString()};

            // Retrieve result from database
            Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);
            // No Active Event found
            if(cursor.getCount()<=0)
            {
                // Enter here if the result is empty, means the no active Event
                // Will call to request to get active event
                System.out.println("Event Not Found !");
                return false;
            }
            if(cursor.getCount()>0)
            {
                System.out.println("Event Found");
                    return true;
            }
            // Close cursor
            cursor.close();
            return false;
    }

    public Event getEventInfo(Integer intEventId) {
        // Define table name
        String tableName = SqliteDB.TABLE_EVENT;
        // Get Sqlite DB instance
        SqliteDB db = SqliteDB.getInstance();
        // Set the data retrieve limit to max 1
        String limit = "1";
        // Get the Event by id
        String whereQuery = "event_id =?";
        String whereArgs[] = {intEventId.toString()};

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, whereQuery, whereArgs, null, null, null, limit);
        // No Active Event found
        if (cursor.getCount() > 0) {
            // Enter here if the result is empty, means the no active Event
            // Will call to request to get active event
            System.out.println("Event Not Found !");
            // Close cursor
            cursor.close();
            return new Event();
        } else {
            // Get the event Detail
            Event event = createEventFromDb(cursor);
            // Close cursor
            cursor.close();
            return event;
        }
    }

    public Event createEventFromDb(Cursor cursor){
        Integer eid = cursor.getInt(cursor.getColumnIndex("event_id"));
        Integer pid = cursor.getInt(cursor.getColumnIndex("place_id"));
        Integer aid = cursor.getInt(cursor.getColumnIndex("advertiser_id"));
        String eTitle = cursor.getString(cursor.getColumnIndex("event_title"));
        String eDesc = cursor.getString(cursor.getColumnIndex("event_desc"));
        Integer ta = cursor.getInt(cursor.getColumnIndex("total_amount"));
        Integer tw = cursor.getInt(cursor.getColumnIndex("total_winner"));
        Integer apv = cursor.getInt(cursor.getColumnIndex("amount_per_voucher"));
        String startD = cursor.getString(cursor.getColumnIndex("start_date"));
        String endD = cursor.getString(cursor.getColumnIndex("end_date"));
        Integer redeemD = cursor.getInt(cursor.getColumnIndex("redeem_duration"));
        String updAt = cursor.getString(cursor.getColumnIndex("updated_at"));

        Event objEvent = new Event(eid, pid, aid, eTitle, eDesc, apv, ta, tw, startD, endD, redeemD, updAt);
        return objEvent;
    }


    public String getEventTestString(){
        return "Event :"+this.eventId+": Place: "+this.placeId+": Advertiser :"+this.advertiserId+" :Total AMount :"+this.totalWinner+"Amount per Voucher"+this.amountPerVoucher+" :startDate :"+this.startDate+" :endDate :"+this.endDate+" :redeemDuration :"+this.redeemDuration+" :updDate :"+this.updatedAt;
    }
}
