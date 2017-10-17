package com.gohool.alertdialog.alertdialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button alert;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add Click event listener
        alert = (Button) findViewById(R.id.alert);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the actual dialog
                // The alert dialog needs to be belong of something
                alertDialog = new AlertDialog.Builder(MainActivity.this);

                // Setup title
                alertDialog.setTitle(R.string.title);

                // Setup message
                alertDialog.setMessage(R.string.message);

                // Set cancellable
                alertDialog.setCancelable(false);

                // Set positive button
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });


                // Set Negative button
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                });

                // Create the actual dialog
                AlertDialog dialog = alertDialog.create();

                // Show the dialog
                dialog.show();
            }
        });
    }
}
