package com.yippie.android.classes;

import android.database.Cursor;

import com.yippie.android.library.SqliteDB;

import java.util.ArrayList;

import static com.yippie.android.library.Utility.changeStringtoDate;

/**
 * Created by Ming on 3/26/2016.
 */
public class ParticipantEvent {
    private Integer eventId;
    private Integer weight;
    private java.util.Date updated_at;
    private java.util.Date created_at;

    public Integer getEventId(){return eventId;}
    public Integer getWeight(){return weight;}
    public java.util.Date getUpdateDate(){return updated_at;}
    public java.util.Date getCreateDate(){return created_at;}

    public void setEventId(Integer eid) {eventId = eid; }
    public void setWeight(Integer w) {weight = w; }
    public void setUpdateDate(java.util.Date upd) {updated_at = upd; }
    public void setCreateDate(java.util.Date crd) {created_at = crd; }

    public ParticipantEvent(){

    }

    public ParticipantEvent(Integer eid, Integer w, String upd, String crd){
        this.eventId = eid;
        this.weight = w;
        this.updated_at = changeStringtoDate(upd);
        this.created_at = changeStringtoDate(crd);
    }

    public ParticipantEvent createParticipantEventFromDb (Cursor c){
        ParticipantEvent pe;
        Integer eid = c.getInt(c.getColumnIndex("event_id"));
        Integer w = c.getInt(c.getColumnIndex("weight"));
        String upd = c.getString(c.getColumnIndex("updated_at"));
        String crd = c.getString(c.getColumnIndex("created_at"));
        pe = new ParticipantEvent(eid, w, upd, crd);
        return pe;
    }

    public ArrayList<Event> getAllParticipantEvent(){
        ArrayList<Event> allEvent = new ArrayList<Event>();
        Event event = null;

        // Define table name
        String tableName = SqliteDB.TABLE_PARTICIPANT_EVENT;
        // Get Sqlite DB instance
        SqliteDB db = SqliteDB.getInstance();
        // Get the active Event
        String whereQuery = "date_add(`end_date`, INTERVAL `redeem_duration` DAY) > NOW()";
        String orderBy = "end_date DESC";

        // Retrieve result from database
        Cursor cursor = db.getRow(tableName, null, whereQuery, null, null, null, orderBy, null);
        // No Active Event found
        if(cursor.getCount()<=0)
        {
            // Enter here if the result is empty, means the no active Event
            // Return Null to be handle by the http call request
        } else {
            // If the result is not empty
            while(cursor.moveToNext())
            {
                ParticipantEvent participantEvent = this.createParticipantEventFromDb(cursor);
                event = event.getEventInfo(participantEvent.getEventId());
                allEvent.add(event);
            }
            // Close cursor
            cursor.close();
        }
        return allEvent;
    }
}
