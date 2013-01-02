package com.example.reifegrad;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout.xml Auswählen
		setContentView(R.layout.activity_menu);

		// Buttons referenzieren
		Button buttonTakeImage = (Button) findViewById(R.id.button1);
		Button buttonLoadImage = (Button) findViewById(R.id.button2);
		

		// ClickListener erstellen
		buttonLoadImage.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			// URI des Bildes
			Uri targetUri = data.getData();
			// Aufruf Vergleichscode
			Intent decisionIntent = new Intent(getApplicationContext(), Decision.class);
			decisionIntent.setData(data.getData());

			startActivity(decisionIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

}
