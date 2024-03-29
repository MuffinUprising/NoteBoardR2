package com.example.casey.noteboardr2;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by casey on 11/22/15.
 */
public class NoteJSONSerializer {

    private Context mContext;
    private String mFilename;

    public NoteJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    public ArrayList<Note> loadNotes() throws IOException, JSONException {
        ArrayList<Note> notes = new ArrayList<Note>();
        BufferedReader reader = null;
        try{
            //Open and read the file into a StringBuffer
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            //Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //Build the array of notes from JSONObjects
            for (int i= 0; i < array.length(); i++) {
                notes.add(new Note(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {

        } finally {
            if (reader != null)
                reader.close();
        }
        return notes;
    }

    public void saveNotes(ArrayList<Note> notes) throws JSONException, IOException {
        //Build an Array in JSON
        JSONArray array = new JSONArray();
        for (Note n : notes) {
            array.put(n.toJSON());
        }

        //Write file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}

