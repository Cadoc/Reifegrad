package com.example.reifegrad;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class ColorDecider extends AsyncTask<Void,Void,Void> {
	
	Bitmap bmp;
	int[] mRedHistogram;
	int[] mGreenHistogram;
	int[] mBlueHistogram;
	byte[][] mask;
	Decision dec;
	
	public ColorDecider(Decision decision, Bitmap bmp, byte [][] mask) {
		
		this.bmp= bmp;
		this.mask= mask;
			
		mRedHistogram = new int[256];
        mGreenHistogram = new int[256];
        mBlueHistogram = new int[256];
        
        this.dec= decision;
	}
	
	public void calculateIntensityHistogram() {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
    	for (int bin = 0; bin < 256; bin++)
    	{
    		mRedHistogram[bin] = 0;
    		mGreenHistogram[bin] = 0;
    		mBlueHistogram[bin] = 0;
    		
    	} // bin
    	
    	for (int i=40; i<width-40; i++) {
			for(int j=40; j<height-40; j++) {
	    		if( mask[i][j] == 1 )
	    		{
	    			int v = bmp.getPixel(i,j);
					int r = (v & 0xFF);
					int g = (v >> 8 & 0xFF);
					int b = (v >> 16 & 0xFF);
		    		// pixVal = (rgb[pix] >> 16) & 0xff; //red
		    		mRedHistogram[ r ]++;
		    		// pixVal = (rgb[pix] >> 8) & 0xff; //green
		    		mGreenHistogram[ g ]++;
		    		// pixVal = rgb[pix] & 0xff; // blue
		    		mBlueHistogram[ b ]++;
	    		}
			}
		}
    }
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("Run", "ColorHist running");
		calculateIntensityHistogram();
		Log.d("Run", "ColorHist finished");
		return null;
	}
	
	protected void onPostExecute(Void result) {
        // showDialog("Downloaded " + result + " bytes");
		for(int i= 0; i < 256; i++)
			Log.w("ColorHist", "r" + mRedHistogram[i] + " g" + mGreenHistogram[i] + " b" + mBlueHistogram[i]);
    }
}
