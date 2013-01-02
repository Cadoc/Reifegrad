package com.example.reifegrad;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

public class Decision extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout.xml Auswählen
		setContentView(R.layout.activity_decision);

		// Layout Elemente referenzieren
		ImageView selectedImageView = (ImageView) findViewById(R.id.imageView1);
		
		Uri imageURI = getIntent().getData();
		
		selectedImageView.setImageURI(imageURI);
		
		Bitmap originalImageBitmap = null;
		
		try {
			originalImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int width = originalImageBitmap.getWidth();
		int height = originalImageBitmap.getHeight();
		
		byte [][] blackwhiteValueArray = new byte [width][height];
		
		for (int i=40; i<width-40; i++) {
			for(int j=40; j<height-40; j++) {
				int v = originalImageBitmap.getPixel(i,j);
				int r = (v & 0xFF);
				int g = (v >> 8 & 0xFF);
				int b = (v >> 16 & 0xFF);
				
				int grey = (int) (0.299*r + 0.587*g + 0.114*b);
				
				if (grey > 128)
					blackwhiteValueArray[i][j]=0;
				else
					blackwhiteValueArray[i][j]=1;
			}
		}
		int Zeilensumme = 0;
	    for ( int i = 0; i < blackwhiteValueArray.length; i++ )
	    {
	    	Zeilensumme = 0;
	    	for ( int j=0; j < blackwhiteValueArray[i].length; j++ )
	    		Zeilensumme += (int) blackwhiteValueArray[i][j];
			Log.w("Array Zeile " + i +": ", ""+Zeilensumme);			
	    }
	}
	
}
