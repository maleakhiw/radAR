package com.gohool.sharepreferences.sharedpreferences;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText enterText;
    private Button submit;
    private TextView result;

    private SharedPreferences sharedPreferences;
    private static final String PREFERENCES = "shared_preferences_file"; // store in xml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use shared preference class to save everything we get from the entertext
                sharedPreferences = getSharedPreferences(PREFERENCES, 0); // lvl 0 accessibility

                // Begin to add into file
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("message", enterText.getText().toString());

                // Save into editor
                editor.commit();
            }
        });

        // Get data back
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, 0);
        if (prefs.contains("message")) {
            String message = prefs.getString("message", "not found");
            result.setText("Message: " + message);
        }
    }

    private void setupUI() {
        enterText = (EditText) findViewById(R.id.enterName);
        submit = (Button) findViewById(R.id.submit);
        result = (TextView) findViewById(R.id.result);
    }

}
