package com.example.reifegrad;

import java.io.File;
/**
 * Source https://developer.android.com/training/camera/photobasics.html
 *
 */
abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}