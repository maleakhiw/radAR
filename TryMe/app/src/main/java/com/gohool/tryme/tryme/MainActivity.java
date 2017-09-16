package com.gohool.tryme.tryme;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button tryMeButton;
    private View windowView;
    private int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set possible colors
        colors = new int[] {Color.BLACK, Color.BLUE, Color.CYAN, Color.GREEN, Color.DKGRAY,
        Color.RED, Color.LTGRAY, Color.WHITE, Color.YELLOW};

        // Connect object with UI widget
        tryMeButton = (Button) findViewById(R.id.tryMeButton);
        windowView = findViewById(R.id.windowViewId);

        // Create event listener for button
        tryMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set background after button clicked
                windowView.setBackgroundColor(chooseColor());
            }
        });
    }

    /** Create method to choose random color */
    private int chooseColor() {
        Random random = new Random();
        int randomNum = random.nextInt(colors.length);

        Log.d("Random", Integer.toString(randomNum)); // Displayed on Android monitor and log it into file
        return colors[randomNum];
    }
}
