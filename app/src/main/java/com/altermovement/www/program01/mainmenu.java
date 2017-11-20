package com.altermovement.www.program01;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class mainmenu extends Activity {
	@Override

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

		setContentView(R.layout.activity_main);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		final Button but11 = (Button) findViewById(R.id.but1);
		final Button but22 = (Button) findViewById(R.id.but2);
		final Button but33 = (Button) findViewById(R.id.but3);
		final Button but44 = (Button) findViewById(R.id.but4);
		final Button but55 = (Button) findViewById(R.id.signal);
		final Button but66 = (Button) findViewById(R.id.but6);

		but11.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//TODO

			}
		});

		but22.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getApplicationContext(), converter.class));

			}
		});

		but33.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getApplicationContext(), midicontroller.class));
			}
		});

		but44.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getApplicationContext(), DJPlayer.class));

			}
		});

		but55.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getApplicationContext(), oscillator.class));
			}
		});

		but66.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(mainmenu.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
						.setMessage("Are you sure you want to exit?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mainmenu.this.finish();
							}
						}).setNegativeButton("No", null).show();
			}
		});

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
				.setMessage("Are you sure you want to exit?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mainmenu.this.finish();
					}
				}).setNegativeButton("No", null).show();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.finish();
	}


	@Override
	protected void onPause() {
		super.onPause();
		this.finish();
	}
}
