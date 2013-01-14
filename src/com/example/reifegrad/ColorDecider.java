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
	double redHistogramSum = 0, greenHistogramSum = 0, blueHistogramSum = 0;
	private String result;
	
	public ColorDecider(Decision decision, Bitmap bmp, byte [][] mask) {
		
		this.bmp= bmp;
		this.mask= mask;
			
		mRedHistogram = new int[256];
        mGreenHistogram = new int[256];
        mBlueHistogram = new int[256];
        
        this.dec= decision;
	}
	
	/**
	 * Computes the histogram
	 * Values are stored in mRedHistogram, mGreenHistogram, mBlueHistogram
	 */
	public void calculateIntensityHistogram() {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
    	for (int bin = 0; bin < 256; bin++)
    	{
    		mRedHistogram[bin] = 0;
    		mGreenHistogram[bin] = 0;
    		mBlueHistogram[bin] = 0;
    		
    	} // bin
    	
    	for (int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
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
	
	/**
	 * Compute the color histogram and decide about the Reifegrad
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
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
	
	/**
	 * Decide about the Reifegrad
	 * Decision is stored in Result
	 */
	private void decide() {
		//   hier dann die Entscheidung mittels entscheidungsbaum ...
		if( imageRedMean < 50 && imageGreenMean > 150 && imageBlueMean > 150 )
		{
			// color = yellow, normal
			result= "Normal";
		}
		else if( imageRedMean > 50 && imageGreenMean > 150 && imageBlueMean > 150 )
		{
			// color = yellow + brown, reif
			result= "Reif";
		}
		else if( imageGreenMean > imageRedMean && imageGreenMean > imageBlueMean  )
		{
			// color = green, green
			result= "Gr\u00FCn";
		}
		else if( imageBlueMean > imageRedMean && imageBlueMean > imageGreenMean  )
		{
			// color = yellow + a lot of brown, ueberreif
			result= "\u00FCberreif";
		}
		else
		{
			// no banana?
			result= "Keine Banane";
		}
	}

	/**
	 * Calculate the mean of RGB in the color histogram
	 * Results are stored in imageRedMean, imageGreenMean, imageBlueMean
	 */
	private void calclulateMean() {
		// Calculate mean
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

	/**
	 * Display the result
	 * This is the only method from which a modification of Decision.class is allowed
	 */
	protected void onPostExecute(Void res) {
		Log.d("ColorDecider", "r" + imageRedMean + " g" + imageGreenMean + " b" + imageBlueMean);
		Log.d("ColorDecider", "1r" + redHistogramSum + " g" + greenHistogramSum + " b" + blueHistogramSum);
		// for(int i= 0; i < 256; i++)
		//	Log.w("ColorHist", "r" + mRedHistogram[i] + " g" + mGreenHistogram[i] + " b" + mBlueHistogram[i]);
		// TODO: display result using dec
		dec.setColorText(this.result);
    }
}
