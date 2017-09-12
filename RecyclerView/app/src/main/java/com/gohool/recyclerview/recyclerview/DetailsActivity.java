package com.gohool.recyclerview.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailsActivity extends AppCompatActivity {
    private TextView name, description;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Connect with UI
        name = (TextView) findViewById(R.id.details);
        description = (TextView) findViewById(R.id.description);

        // Get information from extras
        extras = getIntent().getExtras();

        if (extras != null) {
            name.setText(extras.getString("name"));
            description.setText(extras.getString("description"));
        }
    }
}
