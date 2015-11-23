package com.example.casey.noteboardr2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.tv.TvView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by casey on 11/23/15.
 */
public class NoteCameraFragment extends Fragment {
    private static final String TAG = "NoteCameraFragment";

    public static final String EXTRA_PHOTO_FILENAME = "com.example.casey.noteboardr2.photo_filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;

    private View mProgressContainer;

    @SuppressWarnings("deprecation")
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            //Display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    @SuppressWarnings("deprecation")
    private Camera.PictureCallback mJPEGCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //Create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            //Save jpeg to disk
            FileOutputStream os = null;
            boolean success = true;
            try{
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e){
                Log.e(TAG, "Error writing to file" + filename, e);
                success = false;
            } finally {
                try{
                    if (os != null) {
                        os.close();
                    }
                    }  catch (Exception e){
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
            }
            if (success) {
//                Log.i(TAG, "JPEG saved at " + filename);
                //Set photo filename on the result intent
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };

//    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note_camera, parent, false);

        mProgressContainer = v.findViewById(R.id.note_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button)v.findViewById(R.id.note_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJPEGCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.note_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                //Tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(TAG,"Error setting up preview display", e);
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mCamera == null) return;

                //The surface changed size, update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try{
                    mCamera.startPreview();
                } catch (Exception e){
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

        });

        return v;
    }

    @TargetApi(9)
    @SuppressWarnings("deprecation")
    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(0);
        } else {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @SuppressWarnings("deprecation")
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }


}
