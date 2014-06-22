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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;

import org.opencv.javacv.facerecognition.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
//import android.provider.MediaStore.Files;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import us.feras.ecogallery.EcoGallery;
import us.feras.ecogallery.EcoGallery.LayoutParams;
import us.feras.ecogallery.EcoGalleryAdapterView;
import android.view.MotionEvent;
import android.database.Cursor;
import android.widget.Toast;
 
public class ImageGallery extends Activity implements
        AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
 
	
	labels thelabels;
	int count=0;
	Bitmap bmlist[];
	String namelist[];

    Bitmap multibmlist[][];
    String multinamelist[][];

	String mPath="";
	TextView name;
	Button buttonDel;
	ImageButton buttonBack;
    EcoGallery ecoGallery;

    float initialX;
    private Cursor cursor;
    private  int indeximage=0, parentposition = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = event.getX();

                if (initialX < finalX) {
                    // Swipe Left
                    if(indeximage==0) {
                        Toast.makeText(getApplicationContext(), "Reached Beginning",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        indeximage--;
                        mSwitcher.setImageDrawable(new BitmapDrawable(getResources(), multibmlist[parentposition][indeximage]));
                        //name.setText(multinamelist[parentposition][indeximage--]);
                        name.setText(Integer.toString(indeximage));
                    }
                } else {
                    // Swipe Right
                    if(indeximage<19) {
                        indeximage++;
                        mSwitcher.setImageDrawable(new BitmapDrawable(getResources(), multibmlist[parentposition][indeximage]));
                        //name.setText(multinamelist[parentposition][indeximage++]);
                        name.setText(Integer.toString(indeximage));
                    } else {
                        Toast.makeText(getApplicationContext(), "Reached End",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return false;
    }

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        setContentView(R.layout.catalog_view);
        name=(TextView)findViewById(R.id.textView);
        buttonDel=(Button)findViewById(R.id.buttonDel);
        buttonBack=(ImageButton)findViewById(R.id.imageButton1);

 
        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
 
        Bundle bundle = getIntent().getExtras();
        mPath=bundle.getString("path");
        
        thelabels=new labels(mPath);
        thelabels.Read();
        
        count=0;
    	int max=thelabels.max();
    	
    	for (int i=0;i<=max;i++)
    		
    	{
    		if (thelabels.get(i)!="")
    		{
    			count++;       			
    		}
    	}
    	
    	bmlist=new Bitmap[count];
    	namelist = new String[count];

        multibmlist = new Bitmap[count][20];
        multinamelist = new String[count][20];

    	count=0;
    	for (int i=0;i<=max;i++)
    	{
    		if (thelabels.get(i)!="")
    		{
    			File root = new File(mPath);
    			final String fname=thelabels.get(i);
    	        FilenameFilter pngFilter = new FilenameFilter() {
    	            public boolean accept(File dir, String name) {
    	                return name.toLowerCase().startsWith(fname.toLowerCase()+"-");
    	            
    	        };
    	        };
    	        File[] imageFiles = root.listFiles(pngFilter);

                for(int j=0; j<imageFiles.length && j<20; j++) {
                    InputStream is;
                    try {
                        is = new FileInputStream(imageFiles[j]);

                        multibmlist[count][j]=BitmapFactory.decodeStream(is);
                        multinamelist[count][j]=thelabels.get(i);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        Log.e("File erro", e.getMessage()+" "+e.getCause());
                        e.printStackTrace();
                    }
                }

    	        if (imageFiles.length>0)
    	        {
    	        	InputStream is;
					try {
						is = new FileInputStream(imageFiles[0]);

						bmlist[count]=BitmapFactory.decodeStream(is);
						namelist[count]=thelabels.get(i);

						} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
							Log.e("File erro", e.getMessage()+" "+e.getCause());
							e.printStackTrace();
					}

    	        }

    			count++;       			
    		}
    	}

        ecoGallery = (EcoGallery) findViewById(R.id.gallery);
        ecoGallery.setAdapter(new ImageAdapter(this));
        ecoGallery.setOnItemSelectedListener(
                new EcoGalleryAdapterView.OnItemSelectedListener() {
                    public void onItemSelected(EcoGalleryAdapterView<?> parent, View v, int position, long id) {
                        //mSwitcher.setImageURI(bmlist[0]);
                        parentposition = position;
                        indeximage=0;
                        mSwitcher.setImageDrawable(new BitmapDrawable(getResources(),multibmlist[position][indeximage]));
                        name.setText(multinamelist[position][indeximage]);
                    }

                    public void onNothingSelected(EcoGalleryAdapterView<?> parent) {
                    }
                }
        );


        
        
        buttonBack.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		finish();
        		
        	}
        });
        
        buttonDel.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        	
        		File root = new File(mPath);
    			
    	        FilenameFilter pngFilter = new FilenameFilter() {
    	            public boolean accept(File dir, String n) {
    	            	String s=name.getText().toString();
    	                return n.toLowerCase().startsWith(s.toLowerCase()+"-");
    	            
    	        };
    	        };
    	        File[] imageFiles = root.listFiles(pngFilter);
    	        for (File image : imageFiles) {
    	        	image.delete();
    	        int i;
    	        for (i=0;i<count;i++)
    	        {
    	        	if (namelist[i].equalsIgnoreCase(name.getText().toString()))
    	        			{
    	        			  int j;
    	        			  for (j=i;j<count-1;j++)
    	        			  {
    	        				  namelist[j]=namelist[j+1];
    	        				  bmlist[j]=bmlist[j+1];
    	        			  }
    	        			  count--;
    	        			  refresh();
    	        			  //     	        			  finish();
    	        			  // startActivity(getIntent());
    	        			  
    	        			  //
    	        			  break;
    	        			}
    	        }
    	        }
        	}
        });
    }
 
    public void refresh() {
        ecoGallery.setAdapter(new ImageAdapter(this));
    }
    
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        //mSwitcher.setImageURI(bmlist[0]);
    	mSwitcher.setImageDrawable(new BitmapDrawable(getResources(),bmlist[position]));
    	name.setText(namelist[position]);
    }
 
    public void onNothingSelected(AdapterView<?> parent) {
    }
 
    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        return i;
    }
 
    private ImageSwitcher mSwitcher;
 
    public class ImageAdapter extends BaseAdapter {
        public ImageAdapter(Context c) {
            mContext = c;
        }
 
        public int getCount() {
        	
        	
            return count;
        }
 
        public Object getItem(int position) {
            return bmlist[position];
        }
 
        public long getItemId(int position) {
            return position;
        }
 
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            i.setImageBitmap(bmlist[position]);
            
           // i.setImageResource(mThumbIds[position]);
         
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new EcoGallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
 //           i.setBackgroundResource(R.drawable.picture_frame);
            return i;
        }
 
        private Context mContext;
 
    }
 
 
 
}