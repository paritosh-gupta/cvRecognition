

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



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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


import static  com.googlecode.javacv.cpp.opencv_highgui.*;
import static  com.googlecode.javacv.cpp.opencv_core.*;

import static  com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_contrib.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ViewSwitcher;



public class notes extends Activity {

    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    private ImageView Iv;

    private String fName;

    private TextView textFill;
    private TextView relationFill;
    private EditText notesFill; // fill notes
    private TextView noteR; // read notes

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.second);
         Bitmap bmp;

        byte[] byteArray = getIntent().getByteArrayExtra("face");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Bundle extras = getIntent().getExtras();
        String userName=new String();
        if (extras != null) {
            userName = extras.getString("name");
        }

        textFill = (TextView) findViewById(R.id.name);
        textFill.setText(userName);
        fName=userName;
        relationFill = (TextView) findViewById(R.id.relation);
        EditText notesFill = (EditText) findViewById(R.id.notes);
        noteR = (TextView) findViewById(R.id.noteRead);
        Iv=(ImageView) findViewById(R.id.face);
        Iv.setImageBitmap(bmp);
        String abc=new String();

        try {
            //Toast.makeText(getApplicationContext(),"file eader", Toast.LENGTH_SHORT).show();
               InputStream inputStream = openFileInput(userName + ".txt");

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    //-------------------relationship ship incase needs to be added

                    //receiveString = bufferedReader.readLine();
                    //relationFill.setText(receiveString);

                    //-------------------
                    while ( (receiveString = bufferedReader.readLine()) != null )
                        {
                           stringBuilder.append(receiveString);
                        //   Toast.makeText(getApplicationContext(),"s =" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        }
                    inputStream.close();
                    noteR.setText(stringBuilder.toString() +noteR.getText().toString());
                    //Toast.makeText(getApplicationContext(),"noteR set", Toast.LENGTH_SHORT).show();

                }
            }
            catch (FileNotFoundException e) {
              //  Toast.makeText(getApplicationContext(),"f not found", Toast.LENGTH_SHORT).show();
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(),"cannot read file", Toast.LENGTH_SHORT ). show();
                Log.e("login activity", "Can not read file: " + e.toString());
            }



        //text.setText("Name=");
        //setContentView(text);
       // Mat Dface=new Mat();
        //Utils.bitmapToMat(bmp,Dface);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.catalog_view);
        //Canvas canvas = new Canvas();
        //canvas.setBitmap(bmp);
          //   Iv=(ImageView)findViewById(R.id.imageView1);
        //Iv.setImageBitmap(bmp);*/

    }

    private void writeToFile(String data) {
//        Toast.makeText(getApplicationContext(),"asdasd", Toast.LENGTH_SHORT).show();
        try {
            //Toast.makeText(getApplicationContext(),"in try", Toast.LENGTH_SHORT).show();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fName+".txt", Context.MODE_PRIVATE));
            outputStreamWriter.append(data);
            outputStreamWriter.close();
            Toast.makeText(getApplicationContext(),"data written", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            //Toast.makeText(getApplicationContext(),"write failed", Toast.LENGTH_LONG).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void writer(View view) {
        EditText notesFill = (EditText) findViewById(R.id.notes);
        writeToFile(notesFill.getText().toString());

    }

}

