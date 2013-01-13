package com.example.reifegrad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class Main extends Activity {
	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	final static int ACTIVITY_SELECT_PHOTO = 1;
	final static int ACTIVITY_CAMERA_PHOTO = 0;

	private String mCurrentPhotoPath;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	
	
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("Reifegrad", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout.xml AuswŠhlen
		setContentView(R.layout.activity_menu);

		// Buttons referenzieren
		Button buttonTakeImage = (Button) findViewById(R.id.button1);
		Button buttonLoadImage = (Button) findViewById(R.id.button2);
		

		// ClickListener erstellen
		buttonTakeImage.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File f = null;
				try {
					f = setUpPhotoFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			    startActivityForResult(takePictureIntent, 0);
			    
			}
		});
		
		buttonLoadImage.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent choosePictureintent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(choosePictureintent, 1);
			}
		});
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Intent decisionIntent = new Intent(getApplicationContext(), Decision.class);
		
		switch(requestCode) {
			case ACTIVITY_CAMERA_PHOTO:
				if (resultCode == RESULT_OK) {
					// Aufruf Vergleichscode
					Log.d("Case", "Fotoaufnahme");
					File f = new File(mCurrentPhotoPath);
				    Uri contentUri = Uri.fromFile(f);
				    decisionIntent.setData(contentUri);
				    break;
				}
			case ACTIVITY_SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					// Aufruf Vergleichscode
					Log.d("Case", "Fotoauswahl");
					decisionIntent.setData(data.getData());
					break;
				}
		}
		startActivity(decisionIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}
	

}
