package com.gohool.petbio.petbio;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class BioActivity extends AppCompatActivity {
    private ImageView petBioImage;
    private TextView petName;
    private TextView petBio;
    private Bundle extras;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        petBioImage = (ImageView) findViewById(R.id.petImageId);
        petName = (TextView) findViewById(R.id.nameId);
        petBio = (TextView) findViewById(R.id.bioId);
        back = (Button) findViewById(R.id.back_button);

        // Get the intent data that are sent from first activity
        extras = getIntent().getExtras();

        if (extras != null) {
            String name = extras.getString("name");
            String bio = extras.getString("bio");
            String type = extras.getString("type");
            setUp(name, bio, type);
        }

        // Event listener for back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(BioActivity.this, MainActivity.class);
                startActivity(backIntent);
            }
        });


    }

    // Create method to setup intent
    public void setUp(String name, String bio, String type) {
        // setup name
        petName.setText(name);
        petBio.setText(bio);
        if (type.equals("cat")) {
            petBioImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_lg_cat));
        }
        else {
            petBioImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_lg_dog));
        }
    }
}
