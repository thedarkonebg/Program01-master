package com.altermovement.www.program01;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import de.humatic.nmj.NMJSystemListener;
import de.humatic.nmj.NetworkMidiListener;

public class midi_settings extends Activity implements NMJSystemListener, NetworkMidiListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

        setContentView(R.layout.midi_settings);

    }

    @Override
    public void systemChanged(int i, int i1, int i2) {

    }

    @Override
    public void systemError(int i, int i1, String s) {

    }

    @Override
    public void midiReceived(int i, int i1, byte[] bytes, long l) {

    }
}
