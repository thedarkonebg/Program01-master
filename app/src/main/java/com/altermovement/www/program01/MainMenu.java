package com.altermovement.www.program01;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainMenu extends Activity {

	private Context mContext;
	private Activity mActivity;
	private PopupWindow mPopupWindow;
	private RelativeLayout parent_layout;
	private DisplayMetrics metrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

		setContentView(R.layout.mainmenu_layout);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		mContext = getApplicationContext();
		mActivity = MainMenu.this;
		parent_layout = (RelativeLayout) findViewById(R.id.parent_layout);

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		final Button aboutUsBtn = (Button) findViewById(R.id.but1);
		final Button bpmConvertBtn = (Button) findViewById(R.id.but2);
		final Button midiControllerBtn = (Button) findViewById(R.id.but3);
		final Button djPlayerBtn = (Button) findViewById(R.id.but4);
		final Button synthBtn = (Button) findViewById(R.id.signal);
		final Button exitBtn = (Button) findViewById(R.id.but6);

		aboutUsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
				View customView = inflater.inflate(R.layout.popup_layout, null);

				mPopupWindow = new PopupWindow(
						customView,
						(int)(metrics.widthPixels * 0.7 ),
						(int)(metrics.heightPixels * 0.7 )
				);

				if(Build.VERSION.SDK_INT>=21){
					mPopupWindow.setElevation(5.0f);
				}

				ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

				closeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// Dismiss the popup window
						mPopupWindow.dismiss();
					}
				});

				mPopupWindow.showAtLocation(parent_layout, Gravity.CENTER,0,0);
			}
		});

		bpmConvertBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Converter.class));
			}
		});

		midiControllerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast toast;
				String toasttext = "This feature is not ready yet.";
				toast = Toast.makeText(getApplicationContext(), toasttext, Toast.LENGTH_SHORT);
				toast.show();
			}
		});

		djPlayerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), DJPlayer.class));
			}
		});

		synthBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Oscillator.class));
			}
		});

		exitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MainMenu.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
						.setMessage("Are you sure you want to exit?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								MainMenu.this.finish();
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
						MainMenu.this.finish();
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
