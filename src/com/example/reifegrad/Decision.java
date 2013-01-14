package com.example.reifegrad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class Decision extends Activity {

	private ImageView selectedImageView;
	private TextView shapeTextView;
	private TextView colorTextView;
	private Bitmap originalImageBitmap;
	private Bitmap bwbitmap;
	private byte[][] blackwhiteValueArray;
	private int width;
	private int height;
	private ColorDecider dec;
	private int[] rowSumArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Choose Layout.xml
		setContentView(R.layout.activity_decision);

		//Reference layout elements
		selectedImageView = (ImageView) findViewById(R.id.imageView1);
		shapeTextView = (TextView) findViewById(R.id.textView3);
		colorTextView = (TextView) findViewById(R.id.textView5);
		
		//Get data from previews Intent (= Main Menu)
		Uri imageURI = getIntent().getData();
		
		//Initialize Variables
		originalImageBitmap = null;
		bwbitmap = null;

		//Open Picture and load it into variables
		File imageFile;
		//For some awkward reason uri.fromfile can return a path instead of an uri. Therefor this hack had to be done.
		if(imageURI.toString().startsWith("file://")) imageFile = new File(imageURI.toString().substring(7));
		else imageFile = new File(getRealPathFromURI(imageURI));
		originalImageBitmap = decodeFile(imageFile);
		bwbitmap = originalImageBitmap.copy(originalImageBitmap.getConfig(),
				true);

		width = originalImageBitmap.getWidth();
		height = originalImageBitmap.getHeight();
		
		//Show picture in the interface
		selectedImageView.setImageBitmap(originalImageBitmap);

		//Start shape decision
		shapeDecision();

		//Show black and white image for testing purposes
		displayBWImg();

		//Start color decision
		dec = new ColorDecider(this, originalImageBitmap, blackwhiteValueArray);

		dec.execute();

	}

	/**
	 * Decides weather or not an Image shows an object with a banana like shape.
	 * 
	 * First the black & white array of the image is generated via greyscale.
	 * 
	 * Secondly the row sums of the array are calculated and a weight arithmetic average is calculated.
	 * 
	 * Based on the arithmetic average a decision is made. A banana like object has an weighted average between 100 and 200. 
	 */
	private void shapeDecision() {

		//Generate black & white array
		greyscale();
		
		//prepare average variable & Textview
		double average = 0;
		shapeTextView = (TextView) findViewById(R.id.textView3);
		
		//Calculate row sums
		zeilensummen();

		//Put rowSumArray in HashMap to have the occurrences of values counted
		int[] possibleNumbers;
		Log.d("Height:", ""+height);
		Log.d("Width:", ""+width);
		Log.d("rowSumArray.length:",""+rowSumArray.length);
		if(width<height) {
			possibleNumbers = new int[height];
		} else {
			possibleNumbers = new int[width];
		}
		
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (int i = 0; i < rowSumArray.length; ++i) {
			
			possibleNumbers[rowSumArray[i]] = possibleNumbers[rowSumArray[i]] + 1;
			result.put(rowSumArray[i], possibleNumbers[rowSumArray[i]]);
		}
		
		//Iterate through HashMap and calculate weighted average (Weight = occurrence)
		for (Map.Entry<Integer, Integer> entry : result.entrySet()) {
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			average += key * value;// ...
		}
		average = average / result.size();

		Log.d("average", ""+average);
		//Decision: If average is between 100 and 200 it is most likely a banana like shaped object (Source of Value: Testimage average = 116,66..)
		if (average >= 110 && average <= 120) shapeTextView.setText("Object has a Banana like shape");
	}

	/**
	 * Creates a black (=0) and white (=1) array from a Bitmap object.
	 * First it iterates through the image vertically and saves the r,g and b values of every pixel.
	 * Then it calculates the grey value of every pixel and decides if it is below or above the black threshhold.
	 * Black or white values are then written in the black and white array and a black and white Bitmap object for display is generated
	 * Source mostly taken from course examples with slight additions from us. 
	 */
	private void greyscale() {
		blackwhiteValueArray = new byte[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int v = originalImageBitmap.getPixel(i, j);
				int r = (v & 0xFF);
				int g = (v >> 8 & 0xFF);
				int b = (v >> 16 & 0xFF);

				int grey = (int) (0.299 * r + 0.587 * g + 0.114 * b);

				if (grey > 200) {
					blackwhiteValueArray[i][j] = 0;
					bwbitmap.setPixel(i, j, Color.WHITE);
				} else {
					blackwhiteValueArray[i][j] = 1;
					bwbitmap.setPixel(i, j, Color.BLACK);
				}
			}
		}
	}

	/**
	 * Display black and white Bitmap in ImageView for testing purposes.
	 * Source Imageview Documentation
	 */
	private void displayBWImg() {
		selectedImageView.setImageBitmap(bwbitmap);
	}

	/**
	 * Iterate through the black and white value array and calculate the row sums.
	 * Source ourselfs.
	 */
	private void zeilensummen() {
		rowSumArray = new int[blackwhiteValueArray.length];
		int Zeilensumme = 0;
		for (int i = 0; i < blackwhiteValueArray.length; i++) {
			Zeilensumme = 0;
			for (int j = 0; j < blackwhiteValueArray[i].length; j++)
				Zeilensumme += (int) blackwhiteValueArray[i][j];
			rowSumArray[i] = Zeilensumme;
		}
	}
	
	/** 
	 * Decode an image file before loading it into a bitmap object in order to prevent a possible out of memory exception when handling large bitmaps.
	 * Quality is of course lost but it is need due to the fact that camera images are 2,26 MB in size when taken with android camera application.
	 * Source: http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object?lq=1
	 * @param f Image File
	 * @return Bitmap Bitmap
	 */
	private Bitmap decodeFile(File f) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	/**
	 * Returns the path from an URI
	 * Source: http://stackoverflow.com/questions/2789276/android-get-real-path-by-uri-getpath
	 * @param contentURI
	 * @return
	 */
	private String getRealPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    cursor.moveToFirst(); 
	    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
	    return cursor.getString(idx); 
	}

	public void setColorText(String string) {
		colorTextView.setText(string);
	}

}
