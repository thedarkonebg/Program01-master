package com.altermovement.www.program01.Waveform;

/**
 * Created by The Dark One on 23.6.2017 г..
 */

public class Fft {

    public short[] data = new short[22051];
    public Fft(short a[], int N) {
        for (int i = 0; i < 22051; i++) {
            data[i] = 0;
        }
        int f = 44100;
        short[] freal = new short[N];
        short[] fimag = new short[N];
        short[] freqreal = new short[f];
        short[] freqimag = new short[f];

        int n = 0;


        int k;
        for (k = 0; k < f; k++) {

            while (n < N) {

                a[n] = a[n];
                double b = (-(2 * Math.PI * k * n) / 2);
                freal[n] = (short) (a[n] * Math.cos(b));
                fimag[n] = (short) (a[n] * Math.sin(b));

                freqreal[k] = (short) (freqreal[k] + freal[n]);
                freqimag[k] = (short) (freqimag[k] + fimag[n]);

                n++;
            }

            n = 0;
        }

        for (k = 0; k <= f ; k = k + 2) {
            int p = k / 2;
            data[p] = (short) (Math.sqrt(freqimag[k] * freqimag[k] + freqimag[k + 1] * freqimag[k + 1]));
        }
    }

}

