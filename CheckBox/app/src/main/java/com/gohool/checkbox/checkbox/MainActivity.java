package com.gohool.checkbox.checkbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private CheckBox mom;
    private CheckBox dad;
    private CheckBox sister;
    private TextView result;
    private Button show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect to UI
        mom = (CheckBox) findViewById(R.id.mom);
        dad = (CheckBox) findViewById(R.id.dad);
        sister = (CheckBox) findViewById(R.id.sister);

        result = (TextView) findViewById(R.id.result);
        show = (Button) findViewById(R.id.button);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mom.getText().toString() + " status is " + mom.isChecked() + "\n");
                stringBuilder.append(dad.getText().toString() + " status is " + dad.isChecked() + "\n");
                stringBuilder.append(sister.getText().toString() + " status is " + sister.isChecked());

                System.out.println(stringBuilder);
                result.setVisibility(View.VISIBLE);
                result.setText(stringBuilder);
            }
        });
    }
}
