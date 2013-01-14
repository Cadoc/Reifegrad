package com.example.reifegrad;

import java.io.File;

import android.os.Environment;
/**
 * Source https://developer.android.com/training/camera/photobasics.html
 *
 */
public final class BaseAlbumDirFactory extends AlbumStorageDirFactory {

	// Standard storage location for digital camera files
	private static final String CAMERA_DIR = "/dcim/";

	@Override
	public File getAlbumStorageDir(String albumName) {
		return new File (
				Environment.getExternalStorageDirectory()
				+ CAMERA_DIR
				+ albumName
		);
	}
}
