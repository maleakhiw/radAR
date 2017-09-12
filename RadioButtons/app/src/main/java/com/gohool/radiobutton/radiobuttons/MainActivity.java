package com.gohool.radiobutton.radiobuttons;

import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to UI
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        // Add event listener to radio group
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                radioButton = (RadioButton) findViewById(checkedId);

                switch (radioButton.getId()) {
                    case R.id.yes: {
                        Log.d("ID", "YES");
                        break;
                    }
                    case R.id.no: {
                        Log.d("ID", "NO");
                        break;
                    }
                    case R.id.maybe: {
                        Log.d("ID", "MAYBE");
                        break;
                    }
                }

            }
        });

    }
}
