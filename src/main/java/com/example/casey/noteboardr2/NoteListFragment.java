package com.example.casey.noteboardr2;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by casey on 11/22/15.
 */
public class NoteListFragment extends ListFragment {

    private static final String TAG = "NoteListFragment";
    private ArrayList<Note> mNotes;
    private boolean mSubtitleVisible;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_list, menu);
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_note:
                Note note = new Note();
                NoteLab.get(getActivity()).addNote(note);
                Intent i  = new Intent(getActivity(), NotePagerActivity.class);
                i.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getmNoteID());
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_item_show_subtitle:
                if (getActivity().getActionBar().getSubtitle() == null) {
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    mSubtitleVisible = true;
                    item.setTitle(R.string.hide_subtitle);
                } else {
                    getActivity().getActionBar().setSubtitle(null);
                    mSubtitleVisible = false;
                    item.setTitle(R.string.show_subtitle);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.note_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        NoteAdapter adapter = (NoteAdapter)getListAdapter();
        Note note = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_note:
                NoteLab.get(getActivity()).deleteNote(note);
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.notes_title);
        mNotes = NoteLab.get(getActivity()).getNotes();

//        ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(getActivity(), android.R.layout.simple_list_item_1, mNotes);
        NoteAdapter adapter = new NoteAdapter(mNotes);
        setListAdapter(adapter);
        setRetainInstance(true);
        mSubtitleVisible = false;

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Note n = (Note) (getListAdapter()).getItem(position);

//        //Start NoteActivity
//        Intent i = new Intent(getActivity(), NoteActivity.class);
        //Start NotePagerActivity with this note
        Intent i = new Intent(getActivity(), NotePagerActivity.class);
        i.putExtra(NoteFragment.EXTRA_NOTE_ID, n.getmNoteID());
        startActivityForResult(i, position);
    }

    private class NoteAdapter extends ArrayAdapter<Note> {

        public NoteAdapter(ArrayList<Note> notes) {
            super(getActivity(), 0, notes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //If we aren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_note, null);

            }
            //configure the view for the Note
            Note n = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.note_list_item_titleTextView);
            titleTextView.setText(n.getmNoteTitle());

            TextView dateTextView = (TextView)convertView.findViewById(R.id.note_list_item_dateTextView);
            dateTextView.setText(n.getmDate().toString());

            return convertView;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NoteAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mSubtitleVisible) {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }

        ListView listView = (ListView)v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView);
        } else {
            //use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    //required but not used
                }

                //ActionMode.Callback methods
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.note_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                    //unused
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_note:
                            NoteAdapter adapter = (NoteAdapter)getListAdapter();
                            NoteLab noteLab = NoteLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; 1 >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    noteLab.deleteNote(adapter.getItem(i));
                                }
                                mode.finish();
                                adapter.notifyDataSetChanged();
                                return true;
                            }

                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }


        return v;
    }


}
