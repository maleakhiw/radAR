package com.gohool.togglebutton.togglebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private ToggleButton toggleButton;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect with UI
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        result = (TextView) findViewById(R.id.result);

        // At first result is invisible
        result.setVisibility(View.INVISIBLE);

        // Event Listener
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    result.setVisibility(View.VISIBLE);
                }
                else {
                    // Toggle is disabled
                    result.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}
