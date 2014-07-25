package org.opencv.javacv.facerecognition;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


/**
 * Created by Paritosh on 7/22/14.
 */

public class textFileIO {



    private void writeFile(String fName,String data,boolean append) {
        OutputStreamWriter outputFile = null;
        try {
            outputFile = new OutputStreamWriter(new FileOutputStream(fName + ".txt"));
            outputFile.append(data);
            outputFile.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void readFile(String fName,String data,boolean append) {
        OutputStreamWriter outputFile=null;
        try {
            outputFile= new OutputStreamWriter( new FileOutputStream (fName + ".txt"));
            outputFile.append(data);
            outputFile.close();
        }
        catch (IOException e) {
            //Toast.makeText(getApplicationContext(),"write failed", Toast.LENGTH_LONG).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }











}
