package com.example.casey.noteboardr2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;

/**
 * Created by casey on 11/22/15.
 */
public class NoteFragment extends Fragment {

    private static final String TAG = "NoteFragment";
    public static final String EXTRA_NOTE_ID = "com.example.casey.noteboardr2.note_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final String DIALOG_IMAGE = "image";

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private EditText mTitleField;
    private Note mNote;
    private Button mDateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        UUID noteId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_NOTE_ID);
        UUID noteId = (UUID)getArguments().getSerializable(EXTRA_NOTE_ID);

        mNote = NoteLab.get(getActivity()).getNote(noteId);

        setHasOptionsMenu(true);
    }

    private void updateDate() {
        mDateButton.setText(mNote.getmDate().toString());
    }

    @TargetApi(11)
    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }
        mDateButton = (Button)v.findViewById(R.id.note_date);
//        mDateButton.setText(mNote.getmDate().toString());
//        mDateButton.setEnabled(false);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mNote.getmDate());
                dialog.setTargetFragment(NoteFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTitleField = (EditText)v.findViewById(R.id.note_title);
        mTitleField.setText(mNote.getmNoteTitle());

        mPhotoButton = (ImageButton)v.findViewById(R.id.note_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NoteCameraActivity.class);
//                startActivity(i);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.note_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mNote.getPhoto();
                if (p == null)
                    return;

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);

            }
        });

        //If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature((PackageManager.FEATURE_CAMERA_FRONT)) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
                        Camera.getNumberOfCameras() > 0);

        if (!hasACamera) {
            mPhotoButton.setEnabled(false);
        }


        return v;
    }

    private void showPhoto() {
        //reset the image button's image based on out photo
        Photo p = mNote.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    public static NoteFragment newInstance(UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_NOTE_ID, noteId);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mNote.setmDate(date);
//            mDateButton.setText(mNote.getmDate().toString());
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            //Create a new Photo object and attach it to the Note
            String filename = data.getStringExtra(NoteCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
//                Log.i(TAG, "filename: " + filename);
                Photo p = new Photo(filename);
                mNote.setPhoto(p);
//                Log.i(TAG, "Note:" + mNote.getmNoteTitle() + " has a photo");
                showPhoto();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NoteLab.get(getActivity()).saveNotes();
    }
}
