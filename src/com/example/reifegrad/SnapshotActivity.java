package com.example.reifegrad;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class SnapshotActivity extends Activity {
	Camera camera;
	Preview preview;
	Button buttonClick;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
				}
		});
	}
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
		}
	};
	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};
	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(String.format(
					"/sdcard/foto%d.jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				Log.d("SnapshotActivity", "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
		}
	};
}
