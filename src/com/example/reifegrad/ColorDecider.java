package com.example.reifegrad;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

/*
 * Mostly inspired by ViewFinderEE368.java
 * http://www.stanford.edu/class/ee368/Android/ViewfinderEE368/ViewfinderEE368.java
 */

public class ColorDecider extends AsyncTask<Void,Void,Void> {
	
	Bitmap bmp;
	int[] mRedHistogram;
	int[] mGreenHistogram;
	int[] mBlueHistogram;
	double imageRedMean = 0;
	double imageGreenMean = 0;
	double imageBlueMean = 0;
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
		Log.d("ColorDecider", "ColorHist running");
		calculateIntensityHistogram();
		Log.d("ColorDecider", "ColorHist finished");
		calclulateMean();
		Log.d("ColorDecider", "Mean Computation finished");
		decide();
		Log.d("ColorDecider", "Decided!");
		return null;
	}
	
	private void decide() {
		// TODO Auto-generated method stub
		//   hier dann die Entscheidung ... entscheidungsbaum?
	}

	private void calclulateMean() {
		// Calculate mean
    	double redHistogramSum = 0, greenHistogramSum = 0, blueHistogramSum = 0;
    	for (int bin = 0; bin < 256; bin++)
    	{
    		imageRedMean += mRedHistogram[bin] * bin;
    		redHistogramSum += mRedHistogram[bin];
    		imageGreenMean += mGreenHistogram[bin] * bin;
    		greenHistogramSum += mGreenHistogram[bin];
    		imageBlueMean += mBlueHistogram[bin] * bin;
    		blueHistogramSum += mBlueHistogram[bin];
    	} // bin
    	imageRedMean /= redHistogramSum;
    	imageGreenMean /= greenHistogramSum;
    	imageBlueMean /= blueHistogramSum;
	}

	protected void onPostExecute(Void result) {
		Log.d("ColorDecider", "r" + imageRedMean + " g" + imageGreenMean + " b" + imageBlueMean);
		// for(int i= 0; i < 256; i++)
		//	Log.w("ColorHist", "r" + mRedHistogram[i] + " g" + mGreenHistogram[i] + " b" + mBlueHistogram[i]);
		// TODO: display result using dec
    }
}
