package com.example.reifegrad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

public class Decision extends Activity{
	
	private ImageView selectedImageView;
	private Bitmap originalImageBitmap;
	private Bitmap bwbitmap;
	private byte [][] blackwhiteValueArray;
	private int width;
	private int height;
	private ColorDecider dec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout.xml Ausw�hlen
		setContentView(R.layout.activity_decision);

		// Layout Elemente referenzieren
		selectedImageView = (ImageView) findViewById(R.id.imageView1);
		
		Uri imageURI = getIntent().getData();
		
		//selectedImageView.setImageURI(imageURI);
		
		originalImageBitmap = null;
		bwbitmap= null;
		
		File imageFile = new File(getRealPathFromURI(imageURI));
		
		originalImageBitmap = decodeFile(imageFile);
		
		selectedImageView.setImageBitmap(originalImageBitmap);
		
		Log.e("Original   dimensions", originalImageBitmap.getWidth()+" "+originalImageBitmap.getHeight());
		/*
		//Bitmap original = BitmapFactory.decodeStream(getAssets().open("1024x768.jpg"));
		Bitmap original = null;
		try {
			original = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageURI));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		original.compress(Bitmap.CompressFormat.PNG, 30, out);
		Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
		
		Log.e("Original   dimensions", original.getWidth()+" "+original.getHeight());
		Log.e("Compressed dimensions", decoded.getWidth()+" "+decoded.getHeight());
		
		original.recycle();
		
		originalImageBitmap = decoded;
		
		//decoded.recycle();
		*/
		bwbitmap= originalImageBitmap.copy(originalImageBitmap.getConfig(), true);
		
		width = originalImageBitmap.getWidth();
		height = originalImageBitmap.getHeight();
		
		greyscale();
		displayBWImg();
		
		dec = new ColorDecider(this, originalImageBitmap, blackwhiteValueArray);
		
		dec.execute();
		
		zeilensummen();
		
		/*
		try {
			originalImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
			// bwbitmap= Bitmap.createBitmap(originalImageBitmap);
			bwbitmap= originalImageBitmap.copy(originalImageBitmap.getConfig(), true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		width = originalImageBitmap.getWidth();
		height = originalImageBitmap.getHeight();
		
		greyscale();
		displayBWImg();
		// zeilensummen();
		
		dec= new ColorDecider(this, originalImageBitmap, blackwhiteValueArray);
		// dec.calculateIntensityHistogram();
		dec.execute();
		*/
	}
	
	private void greyscale() {
		blackwhiteValueArray= new byte [width][height];
		
		for (int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				int v = originalImageBitmap.getPixel(i,j);
				int r = (v & 0xFF);
				int g = (v >> 8 & 0xFF);
				int b = (v >> 16 & 0xFF);
				
				int grey = (int) (0.299*r + 0.587*g + 0.114*b);
				
				if (grey > 128)
				{
					blackwhiteValueArray[i][j]= 0;
					bwbitmap.setPixel(i, j, Color.WHITE);
				}
				else
				{
					blackwhiteValueArray[i][j]= 1;
					bwbitmap.setPixel(i, j, Color.BLACK);
				}
			}
		}
	}
	
	private void displayBWImg() {
		/*try
		{
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			File file = new File(path, "bwimg.jpg");
			fOut = new FileOutputStream(file);
	
			bwbitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();
			
			// Add Image to gallery ?
			// MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
			
			// Display B/W Image
			selectedImageView.setImageURI(Uri.fromFile(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		selectedImageView.setImageBitmap(bwbitmap);
	}
	
	private void zeilensummen() {
		int Zeilensumme = 0;
	    for ( int i = 0; i < blackwhiteValueArray.length; i++ )
	    {
	    	Zeilensumme = 0;
	    	for ( int j=0; j < blackwhiteValueArray[i].length; j++ )
	    		Zeilensumme += (int) blackwhiteValueArray[i][j];
			Log.w("Array Zeile " + i +": ", ""+Zeilensumme);			
	    }
	}
	
	//decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=70;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
	private String getRealPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver()
	               .query(contentURI, null, null, null, null); 
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	    return cursor.getString(idx); 
	}
	
}
