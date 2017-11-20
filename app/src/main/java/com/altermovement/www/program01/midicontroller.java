package com.altermovement.www.program01;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class midicontroller extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

        setContentView(R.layout.activity_midicontroller);

        final Button but1 = (Button) findViewById(R.id.but1);
        final Button but2 = (Button) findViewById(R.id.cont1_button);
        final Button but3 = (Button) findViewById(R.id.but3);
        final Button but4 = (Button) findViewById(R.id.but4);
        final Button but5 = (Button) findViewById(R.id.signal);
        final Button but6 = (Button) findViewById(R.id.exit_button);

        but1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    Intent midi = new Intent(midicontroller.this, midi_settings.class);
                    startActivity(midi);
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });

        but3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



            }
        });

        but4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



            }
        });

        but5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //nothing


            }
        });

        but6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(midicontroller.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                        .setMessage("Exit and go back to main menu?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(midicontroller.this, mainmenu.class));
                                midicontroller.this.finish();
                            }
                        }).setNegativeButton("No", null).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent ob = new Intent(midicontroller.this, mainmenu.class);
        startActivity(ob);
        midicontroller.this.finish();
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
