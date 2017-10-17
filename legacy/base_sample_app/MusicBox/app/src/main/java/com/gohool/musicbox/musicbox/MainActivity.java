package com.gohool.musicbox.musicbox;

import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView start;
    private TextView end;
    private SeekBar seekBar;
    private Button previous;
    private Button play;
    private Button next;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup
        setupUI();

        // Register event listener to button
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);

        // setup media player
        mediaPlayer = new MediaPlayer();
        mediaPlayer = mediaPlayer.create(getApplicationContext(), R.raw.pink);

        // setup seekbar
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                start.setText(dateFormat.format(new Date(currentPosition)));
                end.setText(dateFormat.format(new Date(duration - currentPosition)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /** Set up user interface */
    private void setupUI() {
        artistImage = (ImageView) findViewById(R.id.artistProfile);
        start = (TextView) findViewById(R.id.start);
        end = (TextView) findViewById(R.id.end);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        previous = (Button) findViewById(R.id.previous);
        play = (Button) findViewById(R.id.play);
        next = (Button) findViewById(R.id.next);
    }

    /** Use the method so that we can just type this when attaching event listener to object */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.previous:
                backMusic();
                break;

            case R.id.play:
                // play or pause depending on the situation
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                }
                else {
                    startMusic();
                }
                break;

            case R.id.next:
                nextMusic();
                break;
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            // Change the pause symbols to play
            play.setBackgroundResource(android.R.drawable.ic_media_play);
        }

    }

    public void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            play.setBackgroundResource(android.R.drawable.ic_media_pause);
            updateThread();
        }
    }

    public void backMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    /** Create different thread */
    /** When starting application, android will create thread. The good news is that we can create
     * additional thread for every purposes so that it will alleviate traffic
     */
    public void updateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);

                                // update the text
                                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                                start.setText(dateFormat.format(mediaPlayer.getCurrentPosition()));
                                end.setText(dateFormat.format(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                            }
                        });
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
