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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import android.view.MenuInflater;
import android.app.ActionBar;






public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;
    
    public static final int TRAINING= 0;
    public static final int SEARCHING= 1;
    public static final int IDLE= 2;
    
    private static final int frontCam =1;
    private static final int backCam =2;

    public int current_cam = 0;
    	    		
    
    private int faceState=IDLE;
    private MenuItem               nBackCam;
    private MenuItem               mFrontCam;
    

    private Mat                    mRgba;
    private String                    pName;
    private Mat                    mGray;
    public Mat                    face;
    File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;

    int                    mDetectorType       = JAVA_DETECTOR;
    String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private int mLikely=999;
    
    String mPath="";

    private Tutorial3View   mOpenCvCameraView;
    private int mChooseCamera = backCam;
    
    EditText text;
    TextView textresult;
    private  ImageView Iv;
    Bitmap mBitmap;
    Handler mHandler;
  
    PersonRecognizer fr;
    ToggleButton toggleButtonGrabar,toggleButtonTrain,buttonSearch;
    ImageView ivGreen,ivYellow,ivRed; 
    ImageButton imCamera;
    
    TextView textState;
    com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer;
   
    
    static final long MAXIMG = 10;

    int[] labels = new int[(int)MAXIMG];
    int countImages=0;
    
    labels labelsFile;
    
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    
 
                    fr=new PersonRecognizer(mPath);
                    String s = getResources().getString(R.string.Straininig);
                    Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
                    fr.load();
                    
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
              
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
                
                
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);
   
        mOpenCvCameraView.setCvCameraViewListener(this);
       
        
        mPath=getFilesDir()+"/facerecogOCV/";
        		
        labelsFile= new labels(mPath);
                 
        Iv=(ImageView)findViewById(R.id.imageView1);
        textresult = (TextView) findViewById(R.id.textView1);
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	if (msg.obj=="IMG")
            	{
            	 Canvas canvas = new Canvas();
                 canvas.setBitmap(mBitmap);
                 Iv.setImageBitmap(mBitmap);
                 if (countImages>=MAXIMG-1)
                 {
                	 toggleButtonGrabar.setChecked(false);
                 	 grabarOnclick();
                 }
            	}
            	else
            	{
            		 textresult.setText(msg.obj.toString());
            		 ivGreen.setVisibility(View.INVISIBLE);
            	     ivYellow.setVisibility(View.INVISIBLE);
            	     ivRed.setVisibility(View.INVISIBLE);
            	     
            	     if (mLikely<40) {

                         ivGreen.setVisibility(View.VISIBLE);
                         pName=msg.obj.toString();

                         Intent i = new Intent(org.opencv.javacv.facerecognition.FdActivity.this,
                                 org.opencv.javacv.facerecognition.notes.class);

                         ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                         Bitmap bit;
                         bit = Bitmap.createBitmap(face.width(),face.height(), Bitmap.Config.ARGB_8888);
                         Utils.matToBitmap(face, bit);
                         bit.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                         byte[] byteArray = bStream.toByteArray();
                         i.putExtra("face",byteArray);
                         i.putExtra("name",pName);
                         startActivity(i);

                     }

                     else if (mLikely<60) {
                         ivYellow.setVisibility(View.VISIBLE);

                         pName=msg.obj.toString();

                         Intent i = new Intent(org.opencv.javacv.facerecognition.FdActivity.this,
                                 org.opencv.javacv.facerecognition.notes.class);

                         ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                         Bitmap bit;
                         bit = Bitmap.createBitmap(face.width(),face.height(), Bitmap.Config.ARGB_8888);
                         Utils.matToBitmap(face, bit);
                         bit.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                         byte[] byteArray = bStream.toByteArray();
                         i.putExtra("face",byteArray);
                         i.putExtra("name",pName);
                         startActivity(i);
                     }
            		else if (mLikely<80) {
                         ivRed.setVisibility(View.VISIBLE);
                         textresult.setText("");
                     } else {
                        textresult.setText("");
                        ivRed.setVisibility(View.INVISIBLE);
                        ivGreen.setVisibility(View.INVISIBLE);
                        ivYellow.setVisibility(View.INVISIBLE);
                     }
            	}
            }
        };
        text=(EditText)findViewById(R.id.editText1);
        toggleButtonGrabar=(ToggleButton)findViewById(R.id.toggleButtonGrabar);
        buttonSearch=(ToggleButton)findViewById(R.id.buttonBuscar);
        toggleButtonTrain=(ToggleButton)findViewById(R.id.toggleButton1);
        textState= (TextView)findViewById(R.id.textViewState);
        ivGreen=(ImageView)findViewById(R.id.imageView3);
        ivYellow=(ImageView)findViewById(R.id.imageView4);
        ivRed=(ImageView)findViewById(R.id.imageView2);
        imCamera=(ImageButton)findViewById(R.id.imageButton1);
        
        ivGreen.setVisibility(View.INVISIBLE);
        ivYellow.setVisibility(View.INVISIBLE);
        ivRed.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        textresult.setVisibility(View.INVISIBLE);
    

      
        toggleButtonGrabar.setVisibility(View.INVISIBLE);
        
        
        text.setOnKeyListener(new View.OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if ((text.getText().toString().length()>0)&&(toggleButtonTrain.isChecked()))
        			toggleButtonGrabar.setVisibility(View.VISIBLE);
        		else
        			toggleButtonGrabar.setVisibility(View.INVISIBLE);
        		
                return false;
        	}
        });
			

        
		toggleButtonTrain.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (toggleButtonTrain.isChecked()) {
					textState.setText(getResources().getString(R.string.SEnter));
					buttonSearch.setVisibility(View.INVISIBLE);
					textresult.setVisibility(View.VISIBLE);
					text.setVisibility(View.VISIBLE);
					textresult.setText(getResources().getString(R.string.SFaceName));
					if (text.getText().toString().length() > 0)
						toggleButtonGrabar.setVisibility(View.VISIBLE);
					

					ivGreen.setVisibility(View.INVISIBLE);
					ivYellow.setVisibility(View.INVISIBLE);
					ivRed.setVisibility(View.INVISIBLE);
					

				} else {
					textState.setText(R.string.Straininig); 
					textresult.setText("");
					text.setVisibility(View.INVISIBLE);
					
					buttonSearch.setVisibility(View.VISIBLE);
					textresult.setText("");
					{
						toggleButtonGrabar.setVisibility(View.INVISIBLE);
						text.setVisibility(View.INVISIBLE);
					}
			        Toast.makeText(getApplicationContext(),getResources().getString(R.string.Straininig), Toast.LENGTH_LONG).show();
					fr.train();
					textState.setText(getResources().getString(R.string.SIdle));

				}
			}

		});
        
     

        toggleButtonGrabar.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				grabarOnclick();
			}
		});
        
        buttonSearch.setOnClickListener(new View.OnClickListener() {

     			public void onClick(View v) {
     				if (buttonSearch.isChecked())
     				{
     					if (!fr.canPredict())
     						{
     						buttonSearch.setChecked(false);
     			            Toast.makeText(getApplicationContext(), getResources().getString(R.string.SCanntoPredic), Toast.LENGTH_LONG).show();
     			            return;
     						}
     					textState.setText(getResources().getString(R.string.SSearching));
     					toggleButtonGrabar.setVisibility(View.INVISIBLE);
     					toggleButtonTrain.setVisibility(View.INVISIBLE);
     					text.setVisibility(View.INVISIBLE);
     					faceState=SEARCHING;
     					textresult.setVisibility(View.VISIBLE);
     				}
     				else
     				{
     					faceState=IDLE;
     					textState.setText(getResources().getString(R.string.SIdle));
     					toggleButtonGrabar.setVisibility(View.INVISIBLE);
     					toggleButtonTrain.setVisibility(View.VISIBLE);
     					text.setVisibility(View.INVISIBLE);
     					textresult.setVisibility(View.INVISIBLE);
     					
     				}
     			}
     		});
        
        boolean success=(new File(mPath)).mkdirs();
        if (!success)
        {
        	Log.e("Error","Error creating directory");
        }
    }
    
    void grabarOnclick()
    {
    	if (toggleButtonGrabar.isChecked())
			faceState=TRAINING;
			else
			{
			  countImages=0;
			  faceState=IDLE;
			}
		

    }
    
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();       
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
       
      	
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        
        if ((facesArray.length==1)&&(faceState==TRAINING)&&(countImages<MAXIMG)&&(!text.getText().toString().isEmpty()))
        {
        

        Mat m=new Mat();
        Rect r=facesArray[0];
       
        
        m=mRgba.submat(r);
        mBitmap = Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);
        
        
        Utils.matToBitmap(m, mBitmap);
        
        Message msg = new Message();
        String textTochange = "IMG";
        msg.obj = textTochange;
        mHandler.sendMessage(msg);
        if (countImages<MAXIMG)
        {
        	fr.add(m, text.getText().toString());
        	countImages++;
        }

        }
        else
        	 if ((facesArray.length>0)&& (faceState==SEARCHING))
          {
        	  Mat m=new Mat();
        	  m=mGray.submat(facesArray[0]);
              face=mRgba.submat(facesArray[0]);
        	  mBitmap = Bitmap.createBitmap(m.width(),m.height(), Bitmap.Config.ARGB_8888);
        
             
              Utils.matToBitmap(m, mBitmap);
              Message msg = new Message();
              String textTochange = "IMG";
              msg.obj = textTochange;
              mHandler.sendMessage(msg);
        	  
              textTochange=fr.predict(m);
              mLikely=fr.getProb();
        	  msg = new Message();
        	  msg.obj = textTochange;
        	  mHandler.sendMessage(msg);
        	  
          }
        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

        return mRgba;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        current_cam = 2;
        return super.onCreateOptionsMenu(menu);
    }

    public Mat getFace(){
        return face;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_camera:
                if(current_cam == 1) {
                    mChooseCamera=backCam;
                    mOpenCvCameraView.setCamBack();
                    current_cam = 2;
                } else {
                    mChooseCamera=frontCam;
                    mOpenCvCameraView.setCamFront();
                    current_cam = 1;
                }
                break;
            case R.id.action_settings:
                current_cam = current_cam;
                break;
            case R.id.action_gallery:
                Intent i = new Intent(org.opencv.javacv.facerecognition.FdActivity.this,
                        org.opencv.javacv.facerecognition.ImageGallery.class);
                i.putExtra("path", mPath);
                startActivity(i);
                break;
            case R.id.action_write:
                Intent t = new Intent(org.opencv.javacv.facerecognition.FdActivity.this,
                        org.opencv.javacv.facerecognition.input.class);
                startActivity(t);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
