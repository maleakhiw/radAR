package com.gohool.tipcalculator.tipcalculator;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gohool.tipcalculator.tipcalculator.presenters.MainPresenter;
import com.gohool.tipcalculator.tipcalculator.presenters.MainPresenterImpl;
import com.gohool.tipcalculator.tipcalculator.views.MainView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainView {
    private EditText bill;
    private SeekBar tipPercentage;
    private Button calculate;
    private TextView result;
    private TextView percentage;
    private AlertDialog.Builder alertDialog;
    private AlertDialog dialog;

    private MainPresenter mainPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect with UI
        bill = (EditText) findViewById(R.id.editText);
        tipPercentage = (SeekBar) findViewById(R.id.seekBar);
        calculate = (Button) findViewById(R.id.calculate);
        result = (TextView) findViewById(R.id.result);
        percentage = (TextView) findViewById(R.id.seekbarText);

        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();

        mainPresenter = new MainPresenterImpl(this);

    }

    @Override
    public void newAlertDialog() {
        // Create an alert for user if they haven't input anything
        // Show the actual dialog
        // The alert dialog needs to be belong of something
        alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setup title
        alertDialog.setTitle("Error");

        // Setup message
        alertDialog.setMessage("Please input an amount in the proper input form.");

        // Set cancellable
        alertDialog.setCancelable(false);

        // Set positive button
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Create the actual dialog
        dialog = alertDialog.create();
    }

    @Override
    public void setSeekbarText(int resourceID) {
        percentage.setText(resourceID);
    }

    @Override
    public void setSeekbarText(String text) {
        percentage.setText(text);
    }

    @Override
    public void setSeekbarVisibility(int visibility) {
        percentage.setVisibility(visibility);
    }

    @Override
    public void setCalculateBtnOnClickListener(View.OnClickListener onClickListener) {
        calculate.setOnClickListener(onClickListener);
    }

    @Override
    public void setTipPercentageOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        tipPercentage.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    @Override
    public void onClick(View v) {
        // Lazy evaluation to handle if the user haven't input anything
        if (bill.getText().toString().equals("")) {
            dialog.show();
        } else {

            // When the calculate button is clicked it will display the tip that needs to be paid
            double payPercentage = (double) tipPercentage.getProgress() / 100;
            double result_payment = payPercentage * Double.parseDouble(bill.getText().toString());

            // display
            System.out.println(result_payment);
            result.setVisibility(View.VISIBLE);
            result.setText("The tip that you need to pay is " + String.format("%.2f", result_payment));
        }
    }
}
