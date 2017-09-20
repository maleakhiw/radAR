package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import radar.radar.Models.User;

public class UserDetailActivity extends AppCompatActivity {
    private TextView fullname;
    private TextView username;
    private TextView userDetailsProfile;
    private TextView userDetailsEmail;
    private TextView userDetailsPhoneNumber;
    private FloatingActionButton messageFab;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        setupUI();

        // Get the information
        user = (User) getIntent().getSerializableExtra("user");
        fullname.setText(user.firstName + " " + user.lastName);
        username.setText(user.username);
        userDetailsEmail.setText("test@example.com");
        userDetailsPhoneNumber.setText("0410254343");
        userDetailsProfile.setText(user.profileDesc);

        // On click listener for fab
        messageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to home page
                Intent intent = new Intent(UserDetailActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });

    }

    // Setup UI with java
    public void setupUI() {
        messageFab = (FloatingActionButton) findViewById(R.id.fab_message);
        fullname = (TextView) findViewById(R.id.fullname);
        username = (TextView) findViewById(R.id.username);
        userDetailsProfile = (TextView) findViewById(R.id.user_details_profile);
        userDetailsEmail = (TextView) findViewById(R.id.user_details_email);
        userDetailsPhoneNumber = (TextView) findViewById(R.id.user_details_phone_number);
    }
}
