package com.gohool.meterstoinches.metertoinches;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button convert_button;
    private ImageView scaler_image;
    private EditText input_meter;
    private TextView result;

    // Constant
    public static final double METER_TO_INCHES = 39.37;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the widget into Java object
        convert_button = (Button) findViewById(R.id.convert_button);
        scaler_image = (ImageView) findViewById(R.id.scale_image);
        input_meter = (EditText) findViewById(R.id.input_meter);
        result = (TextView) findViewById(R.id.result);

        // Set result to hidden at first
        result.setVisibility(View.INVISIBLE);

        // Event Listener for button
        convert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text
                double entered, result_inches;
                // Lazy Evaluation
                if (input_meter.getText().toString().equals("")) {
                    result.setVisibility(View.VISIBLE);
                    result.setText(R.string.error_message);
                    result.setTextColor(Color.RED);
                }
                else {
                    entered = Double.parseDouble((input_meter.getText().toString()));
                    result_inches = convertMeterToInches(entered);

                    // Display on text view
                    result.setVisibility(View.VISIBLE);
                    result.setTextColor(Color.DKGRAY);
                    result.setText(String.format("%.2f", result_inches) + " inches");
                }
            }
        });
    }

    /** Create method to convert from meters to inches */
    public double convertMeterToInches(double meters) {
        return (METER_TO_INCHES * meters);
    }
}
