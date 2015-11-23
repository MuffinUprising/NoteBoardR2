package com.example.casey.noteboardr2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by casey on 11/22/15.
 */
public class NotePagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<Note> mNotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mNotes = NoteLab.get(this).getNotes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Note note = mNotes.get(position);
                return NoteFragment.newInstance(note.getmNoteID());
            }

            @Override
            public int getCount() {
                return mNotes.size();
            }
        });

        UUID noteID = (UUID)getIntent().getSerializableExtra(NoteFragment.EXTRA_NOTE_ID);
        for (int i =0; i < mNotes.size(); i++) {
            if (mNotes.get(i).getmNoteID().equals(noteID)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
