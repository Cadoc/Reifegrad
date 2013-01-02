package com.example.reifegrad;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
	}
	
}
