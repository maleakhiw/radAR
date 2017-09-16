package myproject.com.myproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {
    private Button firstActivity;
    private static final int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Connect to UI
        firstActivity = (Button) findViewById(R.id.first_activity_button);
        firstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click on the button and go to second activity
                // Intent need to go from first activity to second activity
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                intent.putExtra("Message", "Hello from first activity");
                intent.putExtra("SecondMessage", "Goodbye!");
                intent.putExtra("ThirdMessage", 123);

//                startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    /** Catch what is sent from second activity, everytime go back to activity */
    // Expecting something from the first activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("returnData");

                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
