package com.altermovement.www.program01;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

public class converter extends Activity {

    @SuppressWarnings("ConstantConditions")
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private TextView bpm_tap;       // TEXT FIELD TAP BPM

    // TAP BPM CALCULATION VARIABLES

    private static final int MAX_WAIT = 2000;
    private long mPreviousBeat;

    // ARRAY FOR LAST 8 TAPS

    private long[] mLastBeats = new long[8];
    private int mCurrentBeat;
    private int bb;
    // TAP BPM CALCULATION VARIABLES

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

        setContentView(R.layout.activity_program01);

        // CONVERSION BUTTONS

        final Button button = (Button) findViewById(R.id.button);
        final Button calc_reset = (Button) findViewById(R.id.calc_reset);


        // CONVERSION TEXT INPUT OUTPUT FIELDS

        final EditText field1 = (EditText) findViewById(R.id.field1);
        final EditText field2 = (EditText) findViewById(R.id.field2);
        final TextView change = (TextView) findViewById(R.id.change);



        // RESET BUTTON

        calc_reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hideSoftKeyboard();
                field1.setText("");
                field2.setText("");
                change.setText(R.string.pitch1);
                change.setTextColor(Color.WHITE);

            }

        });

        // CONVERSION BUTTON

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // CHECK FOR EMPTY FIELDS
                // RETURNS ERROR IF FOUND
                hideSoftKeyboard();
                if ((field1.getText().length() == 0) || (field2.getText().length() == 0)) {

                    String error = "INVALID BPM !!!";
                    change.setText(error);
                    change.setTextColor(Color.RED);

                } else {

                    double bpm1;
                    double bpm2;
                    double bpmc;

                    bpm1 = Double.parseDouble(field1.getText().toString());
                    bpm2 = Double.parseDouble(field2.getText().toString());

                    if (bpm2 / bpm1 <= 0.5) {

                        String error1 = "TOO LOW !!!";
                        change.setText(error1);
                        change.setTextColor(Color.RED);

                    } else if (bpm2 / bpm1 >= 2) {
                        String error1 = "TOO HIGH !!!";
                        change.setText(error1);
                        change.setTextColor(Color.RED);

                    } else {

                        bpmc = ((bpm2 / bpm1) - 1) * 100;

                        if (bpmc > 0) {
                            change.setText(String.format("+ %.3f", bpmc));
                            change.setTextColor(Color.GREEN);   // POSITIVE NUMBERS = GREEN

                        } else {
                            change.setText(String.format("%.3f", bpmc));
                            change.setTextColor(Color.RED);     // NEGATIVE NUMBERS = RED
                        }
                    }
                }

            }

        });

        // TAP BPM CALCULATION

        bpm_tap = (TextView) findViewById(R.id.bpm_tap);        // TAP BPM OUTPUT FIELD

        final Button reset = (Button) findViewById(R.id.reset); // TAP BPM RESET BUTTON

        // RESET OUTPUT FIELD AND CREATE NEW EMPTY ARRAY FOR 8 TAPS

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                mLastBeats = new long[8];
                mCurrentBeat = 0;
                mPreviousBeat = 0;
                bpm_tap.setText(R.string.tapbut);

            }

        });

        // TAP BPM BUTTON FUNCTION

        findViewById(R.id.tap).setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mPreviousBeat > 0) {
                        long beat = System.currentTimeMillis();
                        long diff = beat - mPreviousBeat;

                        // reset
                        if (diff >= MAX_WAIT) {
                            Arrays.fill(mLastBeats, 0);
                        } else {
                            mLastBeats[mCurrentBeat] = diff;
                            mCurrentBeat++;
                            if (mCurrentBeat >= mLastBeats.length)
                                mCurrentBeat = 0;
                        }

                    }

                    // OUTPUTS AVERAGE BPM FROM TAP WHEN FIRST ENTRY IN ARRAY IS > 0

                    if (mLastBeats[0] > 0) {
                        long avgDiff = average(mLastBeats);
                        final int bpmt = (int) (60000 / avgDiff);
                        bpm_tap.setText(String.format("%d BPM", bpmt));
                        bb = bpmt;
                    }

                    mPreviousBeat = System.currentTimeMillis();

                    return true;
                }

                return false;

            }

            // TAP BPM AVERAGE CALCULATION

            private long average(long[] values) {
                long sum = 0;
                long count = 0;
                for (long v : values) {
                    if (v > 0) {
                        sum += v;
                        count++;
                    }
                }

                return sum / count;
            }

        });

        // SEND TAP BPM TO BPM 1 BUTTON

        final Button sendbpm = (Button) findViewById(R.id.sendbpm);

        sendbpm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                if (bpm_tap.getText().equals(getString(R.string.taptext))) {

                    field1.setText("");

                } else if (bpm_tap.getText().length() == 0) {

                    field1.setText(R.string.showtap);

                } else {
                    String strb = Integer.toString(bb);
                    field1.setText(strb);

                }

            }

        });

        // RESET OUTPUT FIELD AND CREATE NEW EMPTY ARRAY FOR 8 TAPS

        // EXIT BUTTON

        final Button exit = (Button) findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(converter.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent ob = new Intent(converter.this, mainmenu.class);
                                startActivity(ob);
                                converter.this.finish();
                            }
                        }).setNegativeButton("No", null).show();
            }


        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent ob = new Intent(converter.this, mainmenu.class);
        startActivity(ob);
        converter.this.finish();
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
