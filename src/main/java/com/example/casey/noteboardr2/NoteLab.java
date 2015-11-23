package com.example.casey.noteboardr2;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by casey on 11/22/15.
 */
public class NoteLab {
    private static final String TAG = "NoteLab";
    private static final String FILENAME = "crimes.json";

    private ArrayList<Note> mNotes;

    private NoteJSONSerializer mSerializer;

    private static NoteLab sNoteLab;
    private Context mAppContext;

    private NoteLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new NoteJSONSerializer(mAppContext, FILENAME);

        try {
            mNotes = mSerializer.loadNotes();
        }catch (Exception e) {
            mNotes = new ArrayList<Note>();
            Log.e(TAG, "Error loading Notes: ", e);
        }

    }

    public static NoteLab get(Context c) {
        if (sNoteLab == null) {
            sNoteLab = new NoteLab(c.getApplicationContext());
        }

        return sNoteLab;
    }

    public void addNote(Note n) {
        mNotes.add(n);
    }

    public void deleteNote(Note n) {
        mNotes.remove(n);
    }

    public boolean saveNotes(){
        try{
            mSerializer.saveNotes(mNotes);
            Log.d(TAG, "Notes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving notes: ", e);
            return false;
        }
    }

    public ArrayList<Note> getNotes(){
        return mNotes;
    }

    public Note getNote(UUID id){
        for (Note n : mNotes) {
            if (n.getmNoteID().equals(id))
                return n;
        }
        return null;
    }
}
