package com.altermovement.www.program01;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.altermovement.www.program01.Waveform.Oscillators;
import com.jjoe64.graphview.GraphView;


public class oscillator extends Activity implements View.OnClickListener {

    private static int FACTOR_VOL = 327;

    private int amp_main;
    private int wavefreq_min = 2;
    private int currentVol;

    private int oscOneState = 0;
    private int oscTwoState = 0;
    private int oscThreeState = 0;

    public static GraphView graphview;

    Oscillators wave;

    TextView freqtext_a;
    TextView freqtext_b;
    TextView freqtext_c;

    SeekBar wavefrequency_a;
    SeekBar wavefrequency_b;
    SeekBar wavefrequency_c;

    Button aminus;
    Button bminus;
    Button cminus;

    Button aplus;
    Button bplus;
    Button cplus;

    SeekBar volumeseek_a;
    SeekBar volumeseek_b;
    SeekBar volumeseek_c;

    SeekBar wavemode_a;
    SeekBar wavemode_b;
    SeekBar wavemode_c;

    SeekBar volumeseek_main;

    SeekBar modulate_freq;
    SeekBar modulate_amp;

    ToggleButton startstop;

    ImageView waveicona;
    ImageView waveiconb;
    ImageView waveiconc;

    public static AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
        setContentView(R.layout.oscillator_layout);
        initializeView();

        amp_main = (volumeseek_main.getProgress() * FACTOR_VOL) + 1;
        graphview = (GraphView) findViewById(R.id.graph);

        // WAVEFORM ICONS FUNCTIONALITY


        waveicona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (oscOneState){
                    case 0:
                        oscOneState = 1;
                        waveicona.setBackgroundResource(R.drawable.sinewave_icon);
                        wave.mode_a = 2;
                        break;
                    case 1:
                        oscOneState = 2;
                        waveicona.setBackgroundResource(R.drawable.triangularwave_icon);
                        wave.mode_a = 3;
                        break;
                    case 2:
                        oscOneState = 0;
                        waveicona.setBackgroundResource(R.drawable.squarewave_icon);
                        wave.mode_a = 1;
                        break;
                }
            }
        });

        waveiconb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (oscTwoState){
                    case 0:
                        oscTwoState = 1;
                        waveiconb.setBackgroundResource(R.drawable.sinewave_icon);
                        wave.mode_b = 2;
                        break;
                    case 1:
                        oscTwoState = 2;
                        waveiconb.setBackgroundResource(R.drawable.triangularwave_icon);
                        wave.mode_b = 3;
                        break;
                    case 2:
                        oscTwoState = 0;
                        waveiconb.setBackgroundResource(R.drawable.squarewave_icon);
                        wave.mode_b = 1;
                        break;
                }
            }
        });

        waveiconc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (oscThreeState){
                    case 0:
                        oscThreeState = 1;
                        waveiconc.setBackgroundResource(R.drawable.sinewave_icon);
                        wave.mode_c = 2;
                        break;
                    case 1:
                        oscThreeState = 2;
                        waveiconc.setBackgroundResource(R.drawable.triangularwave_icon);
                        wave.mode_c = 3;
                        break;
                    case 2:
                        oscThreeState = 0;
                        waveiconc.setBackgroundResource(R.drawable.squarewave_icon);
                        wave.mode_c = 1;
                        break;
                }
            }
        });

        // WAVEFORM A B C FREQUENCY

        wavefrequency_a.setOnClickListener(this);
        wavefrequency_a.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.frequency_a = ((wavefreq_min + wavefrequency_a.getProgress()) * 5);
                freqtext_a.setText(String.valueOf((wavefreq_min + wavefrequency_a.getProgress()) * 5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.frequency_a = ((wavefreq_min + wavefrequency_a.getProgress()) * 5);
                freqtext_a.setText(String.valueOf((wavefreq_min + wavefrequency_a.getProgress()) * 5));
            }
        });

        wavefrequency_b.setOnClickListener(this);
        wavefrequency_b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.frequency_b = ((wavefreq_min + wavefrequency_b.getProgress()) * 5);
                freqtext_b.setText(String.valueOf((wavefreq_min + wavefrequency_b.getProgress()) * 5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.frequency_b = ((wavefreq_min + wavefrequency_b.getProgress()) * 5);
                freqtext_b.setText(String.valueOf((wavefreq_min + wavefrequency_b.getProgress()) * 5));
            }
        });

        wavefrequency_c.setOnClickListener(this);
        wavefrequency_c.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.frequency_c = ((wavefreq_min + wavefrequency_c.getProgress()) * 5);
                freqtext_c.setText(String.valueOf((wavefreq_min + wavefrequency_c.getProgress()) * 5));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.frequency_c = ((wavefreq_min + wavefrequency_c.getProgress()) * 5);
                freqtext_c.setText(String.valueOf((wavefreq_min + wavefrequency_c.getProgress()) * 5));
            }
        });

        // WAVEFORM A B C AMPLITUDE
        volumeseek_a.setProgress(50);
        volumeseek_a.setOnClickListener(this);
        volumeseek_a.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.amplitude_a = (volumeseek_a.getProgress() * 327);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.amplitude_a = (volumeseek_a.getProgress() * 327);
            }
        });

        volumeseek_b.setOnClickListener(this);
        volumeseek_b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.amplitude_b = (volumeseek_b.getProgress() * 327);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.amplitude_b = (volumeseek_b.getProgress() * 327);
            }
        });

        volumeseek_c.setOnClickListener(this);
        volumeseek_c.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.amplitude_c = (volumeseek_c.getProgress() * 327);
        }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.amplitude_a = (volumeseek_c.getProgress() * 327);
            }
        });

        // WAVEFORM A B C MODE

//        wavemode_a.setProgress(1);
//        wavemode_a.setOnClickListener(this);
//        wavemode_a.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar wavemode_a, int progressValue, boolean fromUser) {
//                wave.mode_a = (wavemode_a.getProgress() + 1);
//                switch (wavemode_a.getProgress()) {
//                    case 0:
//                        waveicona.setBackgroundResource(R.drawable.squarewave_icon);
//                        break;
//                    case 1:
//                        waveicona.setBackgroundResource(R.drawable.sinewave_icon);
//                        break;
//                    case 2:
//                        waveicona.setBackgroundResource(R.drawable.triangularwave_icon);
//                        break;
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar wavemode_a) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar wavemode_a) {
//                wave.mode_a = (wavemode_a.getProgress() + 1);
//            }
//        });
//
//        wavemode_b.setProgress(1);
//        wavemode_b.setOnClickListener(this);
//        wavemode_b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar wavemode_b, int progressValue, boolean fromUser) {
//                wave.mode_b = (wavemode_b.getProgress() + 1);
//                switch (wavemode_b.getProgress()) {
//                    case 0:
//                        waveiconb.setBackgroundResource(R.drawable.squarewave_icon);
//                        break;
//                    case 1:
//                        waveiconb.setBackgroundResource(R.drawable.sinewave_icon);
//                        break;
//                    case 2:
//                        waveiconb.setBackgroundResource(R.drawable.triangularwave_icon);
//                        break;
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar wavemode_b) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar wavemode_b) {
//                wave.mode_b = (wavemode_b.getProgress() + 1);
//            }
//        });
//
//        wavemode_c.setProgress(1);
//        wavemode_c.setOnClickListener(this);
//        wavemode_c.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar wavemode_c, int progressValue, boolean fromUser) {
//                wave.mode_c = (wavemode_c.getProgress() + 1);
//                switch (wavemode_c.getProgress()){
//                    case 0:
//                        waveiconc.setBackgroundResource(R.drawable.squarewave_icon);
//                        break;
//                    case 1:
//                        waveiconc.setBackgroundResource(R.drawable.sinewave_icon);
//                        break;
//                    case 2:
//                        waveiconc.setBackgroundResource(R.drawable.triangularwave_icon);
//                        break;
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar wavemode_c) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar wavemode_c) {
//                wave.mode_c = (wavemode_c.getProgress() + 1);
//            }
//        });

        // MODULATE FREQUENCY AND AMPLITUDE
        modulate_amp.setProgress(0);
        modulate_amp.setOnClickListener(this);
        modulate_amp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.mod_amp = modulate_amp.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.mod_amp = modulate_amp.getProgress();
            }
        });
        modulate_freq.setProgress(0);
        modulate_freq.setOnClickListener(this);
        modulate_freq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.mod = modulate_freq.getProgress() * 0.1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.mod = modulate_freq.getProgress() * 0.1;
            }
        });

        // MAIN WAVE AMPLITUDE

        volumeseek_main.setProgress(50);
        volumeseek_main.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                wave.amplitude = (volumeseek_main.getProgress() * FACTOR_VOL) + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wave.amplitude = (volumeseek_main.getProgress() * FACTOR_VOL) + 1;
            }
        });

        // START / STOP TOGGLE

        freqtext_a.setText(String.valueOf((wavefreq_min + wavefrequency_a.getProgress()) * 5));
        freqtext_b.setText(String.valueOf((wavefreq_min + wavefrequency_b.getProgress()) * 5));
        freqtext_c.setText(String.valueOf((wavefreq_min + wavefrequency_c.getProgress()) * 5));

        startstop.setOnClickListener(this);


        aminus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wavefrequency_a.setProgress(wavefrequency_a.getProgress() - 1);
            }
        });

        bminus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wavefrequency_b.setProgress(wavefrequency_b.getProgress() - 1);
            }
        });

        cminus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wavefrequency_c.setProgress(wavefrequency_c.getProgress() - 1);
            }
        });


        aplus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wavefrequency_a.setProgress(wavefrequency_a.getProgress() + 1);
            }
        });

        bplus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wavefrequency_b.setProgress(wavefrequency_b.getProgress() + 1);
            }
        });

        cplus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wavefrequency_c.setProgress(wavefrequency_c.getProgress() + 1);
            }
        });
    }

    private void initializeView() {

        waveicona = (ImageView) findViewById(R.id.waveai);
        waveiconb = (ImageView) findViewById(R.id.wavebi);
        waveiconc = (ImageView) findViewById(R.id.waveci);

        aminus = (Button) findViewById(R.id.aminus);
        bminus = (Button) findViewById(R.id.bminus);
        cminus = (Button) findViewById(R.id.cminus);

        aplus = (Button) findViewById(R.id.aplus);
        bplus = (Button) findViewById(R.id.bplus);
        cplus = (Button) findViewById(R.id.cplus);

        freqtext_a = (TextView) findViewById(R.id.frequency_a);
        freqtext_b = (TextView) findViewById(R.id.frequency_b);
        freqtext_c = (TextView) findViewById(R.id.frequency_c);

        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);

        wavefrequency_a = (SeekBar) findViewById(R.id.freqseek_a);
        wavefrequency_b = (SeekBar) findViewById(R.id.freqseek_b);
        wavefrequency_c = (SeekBar) findViewById(R.id.freqseek_c);

        volumeseek_a = (SeekBar) findViewById(R.id.ampseek_a);
        volumeseek_b = (SeekBar) findViewById(R.id.ampseek_b);
        volumeseek_c = (SeekBar) findViewById(R.id.ampseek_c);

        //wavemode_a = (SeekBar) findViewById(R.id.modeseek_a);
        //wavemode_b = (SeekBar) findViewById(R.id.modeseek_b);
        //wavemode_c = (SeekBar) findViewById(R.id.modeseek_c);

        volumeseek_main = (SeekBar) findViewById(R.id.ampseek_main);

        modulate_freq = (SeekBar) findViewById(R.id.modfreq_seek);
        modulate_amp = (SeekBar) findViewById(R.id.modamp_seek);

        startstop = (ToggleButton) findViewById(R.id.startstop);
        wave = new Oscillators();
    }

    @Override
    public void onClick(View v) {

        boolean on = startstop.isChecked();
        if (on) {
            if (wave != null)
                fadein();
        }

        if (!on) {
                fadeout();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (wave != null)
            wave.stop();
    }

    @Override
    public void onPause() {

        super.onPause();
        if (wave != null)
            wave.stop();
    }

    public void fadeout(){

        int targetVol = 0;
        int STEP_DOWN=2;
        currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int nextVol=currentVol;
        // fade music gently
        while(currentVol > targetVol) {
            try {
                Thread.sleep(25);
            }catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol - STEP_DOWN,0);
            currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        wave.stop();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nextVol, 0);

    }

    public void fadein(){

        int initialVol = 0;
        int STEP_UP=2;
        currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        wave.frequency_a = (wavefreq_min + wavefrequency_a.getProgress()) * 5;
        wave.frequency_b = (wavefreq_min + wavefrequency_b.getProgress()) * 5;
        wave.frequency_c = (wavefreq_min + wavefrequency_c.getProgress()) * 5;

        wave.amplitude_a = volumeseek_a.getProgress() * 327;
        wave.amplitude_b = volumeseek_b.getProgress() * 327;
        wave.amplitude_c = volumeseek_c.getProgress() * 327;

        wave.mode_a = 2;
        wave.mode_b = 2;
        wave.mode_c = 2;

        wave.amplitude = amp_main;
        volumeseek_main.setProgress(50);

        wave.mod = ( modulate_freq.getProgress() * 0.1);
        wave.mod_amp = modulate_amp.getProgress();

        wave.start();

        while(initialVol < currentVol) {
            try {
                Thread.sleep(25);
            }catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initialVol + STEP_UP,0);
            initialVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
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
        Intent ob = new Intent(oscillator.this, mainmenu.class);
        startActivity(ob);
        oscillator.this.finish();
    }




}


