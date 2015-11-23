package com.example.casey.noteboardr2;

import android.support.v4.app.Fragment;

/**
 * Created by casey on 11/22/15.
 */
public class NoteListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }
}
