/*
 * Copyright (C) 2014 Siddhanathan Shanmugam <siddhanathan@gmail.com>, Paritosh Gupta <paritosh.gupta@yahoo.co.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.opencv.javacv.facerecognition;

import java.io.FileOutputStream;
import java.util.List;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class Tutorial3View extends JavaCameraView {

    private static final String TAG = "Sample::Tutorial3View";

    public Tutorial3View(Context context, AttributeSet attrs) {
        super(context, attrs);
     
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }
    
    public void setResolution(int w,int h) {
        disconnectCamera();
        mMaxHeight = h;
        mMaxWidth = w;
       
        connectCamera(getWidth(), getHeight());
    }
    
    public void setAutofocus()
    {
    	Camera.Parameters parameters = mCamera.getParameters();
    	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//    	 if (parameters.isVideoStabilizationSupported())
//         {
//      	   parameters.setVideoStabilization(true);
//         }
    	 mCamera.setParameters(parameters);
		     
    }
    public void setCamFront()
    {
    	 disconnectCamera();
    	 setCameraIndex(org.opencv.android.CameraBridgeViewBase.CAMERA_ID_FRONT );
    	 connectCamera(getWidth(), getHeight());
    }
    public void setCamBack()
    {
    	 disconnectCamera();    	 
    	 setCameraIndex(org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK );
    	 connectCamera(getWidth(), getHeight());
    }

    public int numberCameras()
    {
     return	Camera.getNumberOfCameras();
    }
    
    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Tacking picture");
        PictureCallback callback = new PictureCallback() {

            private String mPictureFileName = fileName;

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "Saving a bitmap to file");
                Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);
                try {
                    FileOutputStream out = new FileOutputStream(mPictureFileName);
                    picture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    picture.recycle();
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mCamera.takePicture(null, null, callback);
    }
}
