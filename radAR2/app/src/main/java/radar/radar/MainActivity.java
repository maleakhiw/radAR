package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import radar.radar.Models.Requests.SignUpRequest;

public class MainActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText username;
    private EditText password;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup UI
        setupUI();

        // When the user sign up, we want to pass everything to the server and database
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpRequest signUpRequest = new SignUpRequest(firstName.getText().toString(),
                        lastName.getText().toString(), email.getText().toString(), username.getText().toString(),
                        null, password.getText().toString(), null);
            }
        });
    }

    /** Setting up the User Interface */
    public void setupUI() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.signup);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
    }
}
