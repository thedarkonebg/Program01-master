package com.altermovement.www.program01;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.altermovement.www.program01.Controls.OpenFile;
import com.github.derlio.waveform.SimpleWaveformView;
import com.github.derlio.waveform.soundfile.SoundFile;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;
public class DJPlayer extends Activity {

    // GESTURE DETECTOR //

    private GestureDetector gdt;
    private static final int MIN_SWIPPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;

    // TAG //

    private static final String TAG = "AudioPlayer";

    // USER INTERFACE //

    private ImageButton     button_play;
    private ImageButton     button_cue;

    private Button          button_bpm;
    private Button          button_load;
    private Button          button_pitchplus;
    private Button          button_pitchminus;
    private Button          button_range;

    private SeekBar         seekbar_pitch;

    private ImageView       image_disk;

    private ProgressBar     progressbar_time;
    private ProgressBar     load_file_progressbar;

    private TextView        text_time;
    private TextView        text_pitch;
    private TextView        text_artist;
    private TextView        text_track;

    private SimpleWaveformView waveform;

    private Toast toast;

    // CHOOSE FILE DIALOG WINDOW //

    private OpenFile filedialog;
    Thread loadSongfileThread;

    // EXOPLAYER PLAYER AND UTILITIES //

    private static ExoPlayer audioPlayer;
	private Context mContext;
    private Handler mHandler;
    PlaybackParameters playbackparams;

    // AUDIOFILE //

    private Uri audiofile;
    private File waveformfile;

    // PLAYBACK CONTROL VARIABLES //

    private boolean isPlaying = false;
    private double pitch_coeff;
    private int sw_case = 0;
    private double pitch_factor = 0.02;
    private double temp_coeff_max;
    private long cuetime = 0;
    private static long loopStartPosition = 0;
	private static boolean isLooped = false;

    // UI STRINGS //

    private String toasttext;
    private String[] meta = new String[2];
    private String endtime;
    private String currenttime;

    // BUTTON LOGIC //

    private String[] trackstate = new String[]{"PLAY", "STOP", "LOOP"};
    private int i = 1;

    // ON CREATE //

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);

        setContentView(R.layout.player_layout);

        mContext = getApplicationContext();

        initializeView();
        initializePlayer();

        // JOG WHEEL GESTURE DETECTOR //

        gdt = new GestureDetector(new GestureListener());
        image_disk.setOnTouchListener(new View.OnTouchListener()
        {
			@Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            }

        });

        // LOAD BUTTON //

        button_load.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                filedialog.setFileListener(new OpenFile.FileSelectedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
                    @Override public void fileSelected(final File file) {
                        audiofile = Uri.fromFile(file);
                        waveformfile = file;
                        cuetime = 0;
                        loadTrack(audiofile);
                        setMeta(audiofile);
                        loadSoundWave();
						zeroCue();

                    }
                });
                filedialog.setExtension("mp3");
                filedialog.showDialog();
            }
        });

        // PLAY AND CUE //

        button_play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                switch(trackstate[i]){

                    case "PLAY":
						i = 2;
						isLooped = true;
                        loopStartPosition = audioPlayer.getCurrentPosition();
                        loopCue();
                        break;

                    case "STOP":
                        i = 0;
                        audioPlayer.setPlayWhenReady(true);
                        isPlaying = true;
                        isLooped = false;
                        setTime();
                        break;

                    case "LOOP":
                        i = 0;
						isLooped = false;
						isPlaying = true;
                        break;
                }

            }
        });

        button_cue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    switch(trackstate[i]){

                        case "PLAY":
							audioPlayer.seekTo(cuetime);
                            isPlaying = true;
                            isLooped = false;
                            break;

                        case "STOP":
                            audioPlayer.seekTo(cuetime);
                            audioPlayer.setPlayWhenReady(true);
							isPlaying = true;
                            isLooped = false;
                            break;

                        case "LOOP":
                            cuetime = loopStartPosition;
							audioPlayer.seekTo(cuetime);
							isPlaying = true;
                            isLooped = false;
                            break;
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
					i = 1;
					audioPlayer.setPlayWhenReady(false);
					audioPlayer.seekTo(cuetime);
					isPlaying = false;
                    isLooped = false;
                }
                return true;
            }

        });

        // PITCH CONTROLS //

        seekbar_pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @SuppressLint("DefaultLocale")
			@Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                temp_coeff_max = seekbar_pitch.getMax() * pitch_factor;

                int temp_coeff = seekbar_pitch.getProgress();
                if (temp_coeff == 0){
                    pitch_coeff = - temp_coeff_max * 0.5;
                } else {
                    pitch_coeff = (temp_coeff * pitch_factor) - temp_coeff_max * 0.5;

                }
                playbackparams = new PlaybackParameters((float)(1 + (pitch_coeff * 0.01)), 1);
                audioPlayer.setPlaybackParameters(playbackparams);
                text_pitch.setText(String.format("%.2f", pitch_coeff));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @SuppressLint("DefaultLocale")
			@Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                temp_coeff_max = seekbar_pitch.getMax() * pitch_factor;
                double temp_coeff = seekbar_pitch.getProgress();
                if (temp_coeff == 0){
                    pitch_coeff = - temp_coeff_max * 0.5;
                } else {
                    pitch_coeff = (temp_coeff * pitch_factor) - temp_coeff_max * 0.5;
                }
                playbackparams = new PlaybackParameters((float)(1 + (pitch_coeff * 0.01)), 1);
                audioPlayer.setPlaybackParameters(playbackparams);
                text_pitch.setText(String.format("%.2f", pitch_coeff));
            }
        });

        button_pitchplus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seekbar_pitch.setProgress(seekbar_pitch.getProgress() + 1);
            }
        });

        button_pitchminus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                seekbar_pitch.setProgress(seekbar_pitch.getProgress() - 1);
            }
        });

        // PITCH RANGE CHANGER //

        button_range.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
			public void onClick(View v) {
                int temp_progress;
                switch (sw_case) {

                    case 0:
                        temp_progress = seekbar_pitch.getProgress();
                        seekbar_pitch.setMax(400);
                        pitch_factor = 0.05;
                        seekbar_pitch.setProgress(temp_progress * 400 / 600);
                        temp_coeff_max = seekbar_pitch.getMax() * pitch_factor;
                        text_pitch.setText(String.format("%.2f", (seekbar_pitch.getProgress() * pitch_factor) - temp_coeff_max * 0.5));
                        toasttext = "RANGE +/- 10 %";
                        sw_case += 1;
                        break;

                    case 1:
                        temp_progress = seekbar_pitch.getProgress();
                        seekbar_pitch.setMax(640);
                        pitch_factor = 0.05;
                        seekbar_pitch.setProgress(temp_progress * 640 / 400);
                        temp_coeff_max=(int)(seekbar_pitch.getMax() * pitch_factor);
                        text_pitch.setText(String.format("%.2f", (seekbar_pitch.getProgress() * pitch_factor) - temp_coeff_max * 0.5));
                        toasttext = "RANGE +/- 16 %";
                        sw_case += 1;

                        break;

                    case 2:
                        temp_progress = seekbar_pitch.getProgress();
                        seekbar_pitch.setMax(600);
                        pitch_factor = 0.02;
                        seekbar_pitch.setProgress(temp_progress * 600 / 640);
                        temp_coeff_max=(int)(seekbar_pitch.getMax() * pitch_factor);
                        text_pitch.setText(String.format("%.2f", (seekbar_pitch.getProgress() * pitch_factor) - temp_coeff_max * 0.5));
                        toasttext = "RANGE +/- 6 %";
                        sw_case = 0;
                        break;
                }

                playbackparams = new PlaybackParameters((float)(1 + (pitch_coeff * 0.01)), 1);
                audioPlayer.setPlaybackParameters(playbackparams);
                if (toast != null){
                    toast.cancel();
                }
                toast = Toast.makeText(getApplicationContext(), toasttext, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    // WAVERFORM VIEW //

	// Starts the file explorer. When track is selected, sends it to ExoPlayer and to WaveForm display

    private void loadSoundWave() {
        loadSongfileThread = new Thread() {
            @Override
            public void run() {
                try {
                    String path = waveformfile.getAbsolutePath();
                    load_file_progressbar = (ProgressBar) findViewById(R.id.load_file_progressbar);
                    load_file_progressbar.setVisibility(View.VISIBLE);
                    final SoundFile soundFile = SoundFile.create(path, new SoundFile.ProgressListener() {
                                int lastProgress = 0;
                                @Override
                                public boolean reportProgress(double fractionComplete) {
                                    final int progress = (int) (fractionComplete * 100);
                                    if (lastProgress == progress) {
                                        return true;
                                    }
                                    lastProgress = progress;
                                    Log.i(TAG, "LOAD FILE PROGRESS:" + progress);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            load_file_progressbar.setProgress(progress);
                                            if (progress >= 99){
                                                load_file_progressbar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    return true;
                                }
                            });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            waveform.millisecsToPixels((int) (audioPlayer.getDuration() / 100));
                            waveform.setAudioFile(soundFile);
                            waveform.invalidate();

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SoundFile.InvalidInputException e) {
                    e.printStackTrace();
                }
            }
        };
        loadSongfileThread.start();

    }

    // Initializes the ExoPlayer

    private void initializePlayer() {

        mHandler = new Handler();
		DefaultLoadControl loadControl = new DefaultLoadControl();
		TrackSelector trackSelector = new DefaultTrackSelector();
        audioPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        audioPlayer.addListener(eventListener);
    }

    private void initializeView() {

        // DISPLAY //

        progressbar_time = (ProgressBar) findViewById(R.id.progressbar_time);


        text_time = (TextView) findViewById(R.id.text_time);
        text_pitch = (TextView) findViewById(R.id.text_pitch);
        text_artist = (TextView) findViewById(R.id.text_artist);
        text_track = (TextView) findViewById(R.id.text_track);

        text_artist.setSelected(true);
        text_track.setSelected(true);
        text_pitch.setText(R.string.default_tempo);

        // CONTROLS //

        button_play = (ImageButton) findViewById(R.id.button_play);
        button_cue = (ImageButton) findViewById(R.id.button_cue);

        button_bpm = (Button) findViewById(R.id.button_bpm);
        button_load = (Button) findViewById(R.id.button_load);
        button_pitchplus = (Button) findViewById(R.id.button_pitchplus);
        button_pitchminus = (Button) findViewById(R.id.button_pitchminus);
        button_range = (Button) findViewById(R.id.button_range);

        seekbar_pitch = (SeekBar) findViewById(R.id.seekbar_pitch);
        seekbar_pitch.setMax(600);
        seekbar_pitch.setProgress(300);

        image_disk = (ImageView) findViewById(R.id.image_disk);
        waveform = (SimpleWaveformView) findViewById(R.id.waveform);
        filedialog = new OpenFile(this);
    }

    // Creates instance of FileDataSourceFactory, ExtractorMediaSource and prepares track for playback

    private void loadTrack(Uri filename) {

        FileDataSourceFactory dataSourceFactory = new FileDataSourceFactory();
        ExtractorMediaSource extractor = new ExtractorMediaSource(filename, dataSourceFactory, new AudioExtractorsFactory(), mHandler, null);
        audioPlayer.prepare(extractor, false, false);
        audioPlayer.setPlayWhenReady(false);
        audioPlayer.seekTo(0);
    }

	// Creates instances for file extractor

	private static class AudioExtractorsFactory implements ExtractorsFactory {

        @Override
        public Extractor[] createExtractors() {
            return new Extractor[]{
                    new OggExtractor(),
                    new WavExtractor(),
                    new Mp3Extractor()};
        }
    }

    // Takes META data from file and displays on screen the track and artist name

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD_MR1)
    private void setMeta(Uri path){
        meta[0] = "Not Availible";
        meta[1] = "Not Availible";
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getApplicationContext(), path);
            meta[0] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            meta[1] = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        text_artist.setText(meta[0]);
        text_track.setText(meta[1]);
    }

    // Sets the time and position of progressbar for time

    @SuppressLint("SetTextI18n")
	private void setTime() {
        progressbar_time.setProgress(0);
        progressbar_time.setMax((int) audioPlayer.getDuration()/1000);

        endtime = stringForTime((int) audioPlayer.getDuration());
        currenttime = stringForTime((int) audioPlayer.getCurrentPosition());
        text_time.setText(currenttime + " / " + endtime);

        if(mHandler == null)mHandler = new Handler();

        //Make sure you update Seekbar on UI thread

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (audioPlayer != null && isPlaying) {
                    progressbar_time.setMax((int) audioPlayer.getDuration()/1000);
                    int mCurrentPosition = (int) audioPlayer.getCurrentPosition() / 1000;
                    progressbar_time.setProgress(mCurrentPosition);
                    currenttime = stringForTime((int)audioPlayer.getCurrentPosition());
                    endtime = stringForTime((int)audioPlayer.getDuration());
                    text_time.setText(currenttime + " / " + endtime);
                    mHandler.postDelayed(this, 1000);
                    updateDisplay();
                }
            }
        });
    }

    // Updates the playback position cursor

	private synchronized void updateDisplay() {
        if (audioPlayer != null && isPlaying) {
            int now = (int) audioPlayer.getCurrentPosition();
            waveform.setPlaybackPosition(now);
        }
    }

    // Loops 150 ms of the track after loopStartPosition

    private void loopCue() {
        Thread loopNowThread = new Thread() {
            @Override
            public void run() {
                while(isLooped) {
                    try {
                        Thread.sleep(150);
                        audioPlayer.seekTo(loopStartPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        loopNowThread.start();
    }

    // Zeroes the cue point

	private void zeroCue(){
		cuetime = 0;
		loopStartPosition = 0;
	}

	// Convert time in milliseconds to hour/minutes/seconds

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds =  timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    // This is ExoPlayer listener that detects changes in playback events

    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            Log.i(TAG,"onTimelineChanged");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG,"onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG,"onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.i(TAG,"onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady) + " playbackState = " + playbackState);

            switch (playbackState){

                case ExoPlayer.STATE_ENDED:
                    Log.i(TAG,"Playback ended!");
                    //Stop playback and return to start position
                    audioPlayer.setPlayWhenReady(false);
                    audioPlayer.seekTo(0);
                    break;

                case ExoPlayer.STATE_READY:
                    Log.i(TAG,"ExoPlayer ready! pos: "+audioPlayer.getCurrentPosition()
                            +" max: "+stringForTime((int)audioPlayer.getDuration()));
                    setTime();
                    break;

                case ExoPlayer.STATE_BUFFERING:
                    Log.i(TAG,"Playback buffering!");
                    break;

                case ExoPlayer.STATE_IDLE:
                    Log.i(TAG,"ExoPlayer idle!");
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.i(TAG,"onPlaybackError: "+error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {
            Log.i(TAG,"onPositionDiscontinuity");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.i(TAG, String.valueOf(playbackParameters));
        }
    };

	// The gesture listener detects the direction of swipe and moves the jog in that particular direction. //
	// The property animator animates the tempo of the track up/down and then back to normal //
	// The jog is working like on DJ players, if user swipes faster, the tempo bend is higher and for more time //
	// If the track is on pause, it moves back or forward the current position of audiotrack //

	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			if (e1.getX() - e2.getX() > MIN_SWIPPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
			{
			    if(!isLooped) {
                    float velocityPercentX = Math.abs(velocityX / 10000);          // the percent is a value in the range of (0, 1]

                    float bend = 1 * velocityPercentX;
                    float bendmin = 1 - bend;
                    long duration = (long) (bend * 1000);

                    ValueAnimator downAnimator = ValueAnimator.ofFloat(1, bendmin);
                    downAnimator.setDuration(duration);
                    ValueAnimator backAnimator = ValueAnimator.ofFloat(bendmin, 1);
                    backAnimator.setDuration(duration);

                    downAnimator.start();
                    downAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float momentbend = (float) animation.getAnimatedValue();
                            Log.i(TAG, String.valueOf(momentbend));
                            playbackparams = new PlaybackParameters(momentbend, momentbend);
                            audioPlayer.setPlaybackParameters(playbackparams);
                        }
                    });

                    backAnimator.start();
                    backAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float momentbend = (float) animation.getAnimatedValue();
                            Log.i(TAG, String.valueOf(momentbend));
                            playbackparams = new PlaybackParameters(momentbend, momentbend);
                            audioPlayer.setPlaybackParameters(playbackparams);
                        }
                    });
                    return true;
                } else {
                    float velocityPercentX = Math.abs(velocityX / 10000);          // the percent is a value in the range of (0, 1]
                    if (velocityPercentX < 0.5) {
                        loopStartPosition = loopStartPosition - 25;
                    } else {
                        loopStartPosition = loopStartPosition - (long)(250 * velocityPercentX);
                    }
                    return true;
                }
			}
			else if (e2.getX() - e1.getX() > MIN_SWIPPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
			{
			    if (!isLooped) {
                    float velocityPercentX = Math.abs(velocityX / 10000);

                    float bend = 1 * velocityPercentX;
                    float bendmax = 1 + bend;
                    long duration = (long) (bend * 1000);

                    ValueAnimator upAnimator = ValueAnimator.ofFloat(1, bendmax);
                    upAnimator.setDuration(duration);

                    ValueAnimator backAnimator = ValueAnimator.ofFloat(bendmax, 1);
                    backAnimator.setDuration(duration);

                    upAnimator.start();
                    upAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float momentbend = (float) animation.getAnimatedValue();
                            playbackparams = new PlaybackParameters(momentbend, momentbend);
                            audioPlayer.setPlaybackParameters(playbackparams);
                        }
                    });

                    backAnimator.start();
                    backAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float momentbend = (float) animation.getAnimatedValue();
                            playbackparams = new PlaybackParameters(momentbend, momentbend);
                            audioPlayer.setPlaybackParameters(playbackparams);
                        }
                    });
                    return true;
                } else {
                    float velocityPercentX = Math.abs(velocityX / 10000);          // the percent is a value in the range of (0, 1]
                    if (velocityPercentX < 0.5) {
                        loopStartPosition = loopStartPosition + 25;
                    } else {
                        loopStartPosition = loopStartPosition + (long)(250 * velocityPercentX);
                    }
			        return true;
                }
			}
			return false;
		}
	}

	// Handles back button, shows exit popup

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
				.setMessage("Are you sure you want to exit?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent ob = new Intent(DJPlayer.this, MainMenu.class);
						startActivity(ob);
						DJPlayer.this.finish();
					}
				}).setNegativeButton("No", null).show();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (isPlaying) {
			isPlaying = false;
			loadSongfileThread = null;
			audioPlayer.stop();
			audioPlayer.release();
		} else {
			audioPlayer.release();
		}
	}

	@Override
	public void onPause() {

		super.onPause();
		if (isPlaying) {
			isPlaying = false;
			loadSongfileThread = null;
			audioPlayer.stop();
			audioPlayer.release();
		} else {
			audioPlayer.release();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isPlaying) {
			initializePlayer();
		}
	}

}
