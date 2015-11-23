package com.example.casey.noteboardr2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by casey on 11/22/15.
 */
public class Note {

    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";

    private UUID mNoteID;
    private String mNoteTitle;
    private Date mDate;
    private Photo mPhoto;



    public Note() {
        mNoteID = UUID.randomUUID();
        mDate = new Date();
    }

    public Note(JSONObject json) throws JSONException {
        mNoteID = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_TITLE)) {
            mNoteTitle = json.getString(JSON_TITLE);
        }
        mDate = new Date(json.getLong(JSON_DATE));
        if (json.has(JSON_PHOTO))
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mNoteID.toString());
        json.put(JSON_TITLE, mNoteTitle.toString());
        json.put(JSON_DATE, mDate.getTime());
        if (mPhoto != null) {
            json.put(JSON_PHOTO, mPhoto.toJSON());
        }
        return json;
    }

    //getters
    public UUID getmNoteID() { return mNoteID; }
    public String getmNoteTitle() { return mNoteTitle; }
    public Date getmDate() { return mDate; }
    public Photo getPhoto(){ return mPhoto; }

    //setters
    public void setmNoteTitle(String title) { this.mNoteTitle = title; }
    public void setmDate(Date date) { this.mDate = date; }
    public void setPhoto(Photo p) {
        mPhoto = p;
    }

    @Override
    public String toString(){
        return mNoteTitle;
    }


}
