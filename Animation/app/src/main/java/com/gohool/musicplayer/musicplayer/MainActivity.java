package com.gohool.musicplayer.musicplayer;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private AnimationDrawable batAnimation;
    private ImageView bat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bat = (ImageView) findViewById(R.id.batId);
        //bat.setBackgroundResource(R.drawable.bat_anim);
        //batAnimation = (AnimationDrawable) bat.getBackground();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //batAnimation.start();

        Handler mHandler = new Handler(); // post delayed control how long the application run
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_animation);
                bat.startAnimation(startAnimation);
                //batAnimation.stop();
            }
        }, 5000); // new traffic for animation
        return super.onTouchEvent(event);
    }
}
