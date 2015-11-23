package com.example.casey.noteboardr2;

import android.support.v4.app.Fragment;

/**
 * Created by casey on 11/23/15.
 */
public class NoteCameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new NoteCameraFragment();
    }
}
