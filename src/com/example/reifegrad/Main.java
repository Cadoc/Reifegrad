package com.example.reifegrad;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
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
	
	//Variable definition
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	final static int ACTIVITY_SELECT_PHOTO = 1;
	final static int ACTIVITY_CAMERA_PHOTO = 0;

	private String mCurrentPhotoPath;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	
	
	/**
	 * Get the hard coded album name from the strings.xml
	 * Source: https://developer.android.com/training/camera/photobasics.html
	 */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	/**
	 * Get the local storage path to the album "Reifegrad"
	 * Source: https://developer.android.com/training/camera/photobasics.html
	 */
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

	/**
	 * Create the image file object with name and album.
	 * Source: https://developer.android.com/training/camera/photobasics.html
	 */
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	/**
	 * Set up the preliminary file object
	 * Source: https://developer.android.com/training/camera/photobasics.html
	 */
	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}

	/**
	 * Add the newly taken photo to the gallery
	 * Source: https://developer.android.com/training/camera/photobasics.html
	 */
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

		//Choose Layout.xml
		setContentView(R.layout.activity_menu);

		//Reference Buttons
		Button buttonTakeImage = (Button) findViewById(R.id.button1);
		Button buttonLoadImage = (Button) findViewById(R.id.button2);
		

		/**
		 * Launch camera application and let the user take a photo.
		 * The new photo is saved on the phone in the album "Reifegrad" in the "Pictures/" directory.
		 */
		buttonTakeImage.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File f = null;
				try {
					f = setUpPhotoFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			    startActivityForResult(takePictureIntent, 0);
			    
			}
		});
		
		/**
		 * Launch android picture gallery and let the user choose a photo.
		 * Source: https://developer.android.com/training/camera/photobasics.html
		 */
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
	/**
	 * Called when an intent is started via an activity to gather results.
	 * We start two intents in this way. Taking a photo with a camera application or choosing a photo from the gallery.
	 * Depending on what intent was started different methods are used to set the URI of the image the user choose for the decision intent.
	 * Source partly from https://developer.android.com/training/camera/photobasics.html and partly from basic android documentation on how to switch user interfaces.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
		//Reference intent we want to start
		Intent decisionIntent = new Intent(getApplicationContext(), Decision.class);
		
		//Check wheater the user choose an image or took a photo
		switch(requestCode) {
			case ACTIVITY_CAMERA_PHOTO:
				if (resultCode == RESULT_OK) {
					//Get URI from photo and set it as data for the new intent
					galleryAddPic();
					File f = new File(mCurrentPhotoPath);
				    Uri contentUri = Uri.fromFile(f);
				    decisionIntent.setData(contentUri);
				    break;
				}
			case ACTIVITY_SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					//Set URI as data for new intent
					decisionIntent.setData(data.getData());
					break;
				}
		}
		startActivity(decisionIntent);
		}
	}
	
	/**
	 * Options Menu stub. This is automatically generated when creating a new Android project.
	 * We use no options menu in our application.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}
}
