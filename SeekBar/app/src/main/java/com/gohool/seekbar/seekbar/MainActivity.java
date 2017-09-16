package com.gohool.seekbar.seekbar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private TextView result;
    private TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect class variable to UI
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        result = (TextView) findViewById(R.id.result);
        question = (TextView) findViewById(R.id.painlevel);

        // Initialize default pain level
        result.setVisibility(View.VISIBLE);
        result.setText("Your pain level is 0/10");

        // Attach event listener and pass into argument the event listener which is from seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Show the progress
                result.setVisibility(View.VISIBLE);

                // Display text with different text color with different pain level)
                result.setText("Your pain level is " + progress + "/" + seekBar.getMax());

                result.setTextColor(Color.GRAY);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("SB", "OnStartTrackingTouch!");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("SB", "OnStopTrackingTouch!");

                if (seekBar.getProgress() >= 7) {
                    result.setTextColor(Color.RED);
                }
            }
        });
    }
}
