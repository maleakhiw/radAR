package myproject.com.myproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    private TextView textView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Bundle extras = getIntent().getExtras();

        textView = (TextView) findViewById(R.id.textView);
        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
                returnIntent.putExtra("returnData", "From SecondActivity");

                // internal code to return back intent
                setResult(RESULT_OK, returnIntent);
                finish(); //clean the slate
            }
        });

        // Check if extra intent is empty or null
        if (extras != null) {
            String message = extras.getString("Message");
            int message2 = extras.getInt("ThirdMessage");
            textView.setText(Integer.toString(message2));
        }
    }
}
