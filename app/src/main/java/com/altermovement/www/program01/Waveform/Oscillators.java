package com.altermovement.www.program01.Waveform;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.altermovement.www.program01.oscillator;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Oscillators implements Runnable{

    private Thread thread;

    // WAVE GENERATOR PUBLIC VARIABLES

    public int frequency_a;
    public int mode_a;
    public int amplitude_a;

    public int frequency_b;
    public int mode_b;
    public int amplitude_b;

    public int frequency_c;
    public int mode_c;
    public int amplitude_c;

    public int amplitude = 32700;

    public double mod = 0;
    public boolean mod_a;
    public boolean mod_b;
    public boolean mod_c;
    public int mod_amp;

    // DATAPOINT AND GRAPH SERIES

    private static LineGraphSeries<DataPoint> waveseries;

    // AudioTrack

    private static AudioTrack myWave;

    private synchronized void setWave() {

        // AUDIOTRACK INIT PARAMETERS

        int rate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        int minSize = AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        int sizes[] = {1024, 2048, 4096, 8192, 16384, 32768};
        int size = 0;

        for (int s : sizes) {
            if (s > minSize) {
                size = s;
                break;
            }
        }

        myWave = new AudioTrack(AudioManager.STREAM_MUSIC, rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, size, AudioTrack.MODE_STREAM);

        int state = myWave.getState();

        if (state != AudioTrack.STATE_INITIALIZED) {
            myWave.release();
            return;
        }

        // MOD BOOLEAN INITIAL STATE = FALSE

        mod_a = true;
        mod_b = true;
        mod_c = true;

        // WAVEFORM DATA CARRIERS

        short[] samples_a = new short[size];
        short[] samples_b = new short[size];
        short[] samples_c = new short[size];

        // MAIN DATA CARRIER

        short[] samples = new short[size];

        // MODULATION DATA CARRIERS

        short carrier_a[] = new short[size];
        short carrier_b[] = new short[size];
        short carrier_c[] = new short[size];

        short carrier_mod[] = new short[size];

        double doublepi = 8. * Math.atan(1.0);

        // WAVEFORM A B C PHASE

        double phase_a = 0.0;
        double phase_b = 0.0;
        double phase_c = 0.0;

        // MODULATOR WAVE PHASE

        double phasemod = 0.0;

        // ITERATOR PARAMETERS

        int x = 0;
        int i;

        // GRAPH ARRAY DATA SIZE
        int datasize = 72;

        // GRAPH INIT PARAMETERS

        DataPoint[] datapp = new DataPoint[datasize];

        oscillator.graphview.getViewport().setXAxisBoundsManual(true);
        oscillator.graphview.getViewport().setMinX(4);
        oscillator.graphview.getViewport().setMaxX(datasize-4);

        oscillator.graphview.getViewport().setYAxisBoundsManual(true);
        oscillator.graphview.getViewport().setMinY(-amplitude);
        oscillator.graphview.getViewport().setMaxY(+amplitude);

        oscillator.graphview.getViewport().setBackgroundColor(Color.rgb(64, 64, 64));

        oscillator.graphview.getLegendRenderer().setVisible(false);
        oscillator.graphview.getGridLabelRenderer().setVerticalLabelsVisible(false);
        oscillator.graphview.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        oscillator.graphview.getGridLabelRenderer().setHighlightZeroLines(false);

        waveseries = new LineGraphSeries<>();
        oscillator.graphview.addSeries(waveseries);

        // WAVEFORM GENERATOR MAIN THREAD
        myWave.play();
        while (thread != null) {

            for (i = 0; i < samples.length; i++) {

                // WAVE {A, B, C, MOD} PHASE
                // WAVE A PHASE

                if (phase_a < Math.PI) {
                    phase_a += doublepi * frequency_a / rate;
                } else {
                    phase_a += (doublepi * frequency_a / rate) - (2 * Math.PI);
                }

                // WAVE B PHASE

                if (phase_b < Math.PI) {
                    phase_b += doublepi * frequency_b / rate;
                } else {
                    phase_b += (doublepi * frequency_b / rate) - (2 * Math.PI);
                }

                // WAVE C PHASE

                if (phase_c < Math.PI) {
                    phase_c += doublepi * frequency_c / rate;
                } else {
                    phase_c += (doublepi * frequency_c / rate) - (2 * Math.PI);
                }

                // WAVE MODULATOR PHASE

                if (phasemod < Math.PI) {
                    phasemod += doublepi * mod / rate;
                } else {
                    phasemod += (doublepi * mod / rate) - (2 * Math.PI);
                }

                carrier_mod[i] = sine(mod_amp, phasemod);

                switch (mode_a) {

                    case 1: // SQUARE WAVE

                        if (mod == 0 && mod_a) {
                            samples_a[i] = square(amplitude_a, phase_a);
                        } else if (mod != 0){
                            carrier_a[i] = square(amplitude_a, phase_a);
                            samples_a[i] = modulate(carrier_a[i], phase_a, phasemod, carrier_mod[i]);
                        }
                        break;

                    case 2: // SINE WAVE

                        if (mod == 0 && mod_a) {
                            samples_a[i] = sine(amplitude_a, phase_a);
                        } else if (mod != 0){
                            carrier_a[i] = sine(amplitude_a, phase_a);
                            samples_a[i] = modulate(carrier_a[i], phase_a, phasemod, carrier_mod[i]);
                        }
                        break;

                    case 3: // TRIANGLE WAVE

                        if (mod == 0 && mod_a) {
                            samples_a[i] = saw(amplitude_a, phase_a);
                        } else if (mod != 0){
                            carrier_a[i] = saw(amplitude_a, phase_a);
                            samples_a[i] = modulate(carrier_a[i], phase_a, phasemod, carrier_mod[i]);
                        }
                        break;
                }

                // GENERATE WAVEFORM B

                switch (mode_b) {

                    case 1: // SQUARE WAVE

                        if (mod == 0 && mod_b) {
                            samples_b[i] = square(amplitude_b, phase_b);
                        } else if (mod != 0){
                            carrier_b[i] = square(amplitude_b, phase_b);
                            samples_b[i] = modulate(carrier_b[i], phase_b, phasemod, carrier_mod[i]);
                        }
                        break;

                    case 2: // SINE WAVE

                        if (mod == 0 && mod_b) {
                            samples_b[i] = sine(amplitude_b, phase_b);
                        } else if (mod != 0){
                            carrier_b[i] = sine(amplitude_b, phase_b);
                            samples_b[i] = modulate(carrier_b[i], phase_b, phasemod, carrier_mod[i]);
                        }
                        break;

                    case 3: // TRIANGLE WAVE

                        if (mod == 0 && mod_b) {
                            samples_b[i] = saw(amplitude_b, phase_b);
                        } else if (mod != 0){
                            carrier_b[i] = saw(amplitude_b, phase_b);
                            samples_b[i] = modulate(carrier_b[i], phase_b, phasemod, carrier_mod[i]);
                        }
                        break;
                }

                // GENERATE WAVEFORM C

                switch (mode_c) {

                    case 1: // SQUARE WAVE

                        if (mod == 0 && mod_c) {
                            samples_c[i] = square(amplitude_c, phase_c);
                        } else if (mod != 0){
                            carrier_c[i] = square(amplitude_c, phase_c);
                            samples_c[i] = modulate(carrier_c[i], phase_c, phasemod, carrier_mod[i]);
                        }
                        break;

                    case 2: // SINE WAVE

                        if (mod == 0 && mod_c) {
                            samples_c[i] = sine(amplitude_c, phase_c);
                        } else if (mod != 0){
                            carrier_c[i] = sine(amplitude_c, phase_c);
                            samples_c[i] = modulate(carrier_c[i], phase_c, phasemod, carrier_mod[i]);
                        }
                        break;

                    case 3: // TRIANGLE WAVE

                        if (mod == 0 && mod_c) {
                            samples_c[i] = saw(amplitude_c, phase_c);
                        } else if (mod != 0){
                            carrier_c[i] = saw(amplitude_c, phase_c);
                            samples_c[i] = modulate(carrier_c[i], phase_c, phasemod, carrier_mod[i]);
                        }
                        break;
                }

                samples[i] = (short) (mixwaves(samples_a[i], samples_b[i], samples_c[i]) * amplitude / 32767);

                if (x >= datasize) {
                    x = 0;
                }
                if (i == x * 8 || x == 0) {
                    int temp_graph = samples[i];
                    datapp[x] = new DataPoint(x, temp_graph);
                    x += 1;
                }
            }

            writebuffer(samples);
            writeseries(datapp);
        }

        myWave.stop();
        myWave.release();
    }

    public void start() {

        thread = new Thread(this, "myWave");
        thread.start();
    }

    public void stop() {

        oscillator.graphview.removeAllSeries();
        Thread t = thread;
        thread = null;
        // Wait for the thread to exit
        while (t != null && t.isAlive())
            Thread.yield();
    }

    @Override
    public void run()
    {
        setWave();
    }

    private static short modulate(short am, double ph, double fph, short fm) {
        return (short) (am * Math.cos(ph + fm * Math.sin(fph)));
    }

    private static short square(int am, double ph) {
        return (short) Math.round(am * Math.signum(Math.sin(ph)));
    }

    private static short sine(int am, double ph) {
        return (short)(am * Math.sin(ph));
    }

    private static short saw(int am, double ph) {
        return (short) Math.round(am * Math.round((ph) / Math.PI));
    }

    private static void writebuffer(short[] shortbuffer){
        myWave.write(shortbuffer, 0, shortbuffer.length);
    }

    private static void writeseries(DataPoint[] series){
        waveseries.resetData(series);
    }

    private static short mixwaves(short wavea, short waveb, short wavec){
        short three = 3;
        return (short)((wavea + waveb + wavec) / three);
    }

}

