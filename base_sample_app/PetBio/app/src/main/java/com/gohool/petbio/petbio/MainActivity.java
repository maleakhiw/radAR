package com.gohool.petbio.petbio;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView cat;
    private ImageView dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect with UI
        cat = (ImageView) findViewById(R.id.cat);
        dog = (ImageView) findViewById(R.id.dog);

        cat.setOnClickListener(MainActivity.this);
        dog.setOnClickListener(MainActivity.this);
    }

    // Prevent redundant code
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cat:
                // Create an intent to link to the second activity
                Intent catIntent = new Intent(MainActivity.this, BioActivity.class);
                catIntent.putExtra("name", "Kitty");
                catIntent.putExtra("bio", "Great cat. Loves people and meow a lot!");
                catIntent.putExtra("type", "cat");

                // send intent
                startActivity(catIntent);
                break;
            case R.id.dog:
                // Second screen about the dog
                Intent dogIntent = new Intent(MainActivity.this, BioActivity.class);
                dogIntent.putExtra("name", "Doggy");
                dogIntent.putExtra("bio", "Great dog. Loves people and barks, eat a lot!");
                dogIntent.putExtra("type", "dog");

                // send intent
                startActivity(dogIntent);
                break;
        }
    }
}
