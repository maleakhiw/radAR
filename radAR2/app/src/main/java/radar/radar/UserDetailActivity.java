package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.User;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDetailActivity extends AppCompatActivity {
    private TextView fullname;
    private TextView username;
    private TextView userDetailsProfile;
    private TextView userDetailsEmail;
    private TextView userDetailsPhoneNumber;
    private FloatingActionButton messageFab;
    private FloatingActionButton addFab;

    private User user;
    private UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        setupUI();

        // Setup Retrofit Instances
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Setup user api that will be used to generate a service so that we can add friends
        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(usersApi, this);

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

        // On click listener for add friends
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add friend
                generateFriendRequest(user.userID);
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
        addFab = findViewById(R.id.fab_add);
    }

    /** This method is used to create friend request */
    public void generateFriendRequest(int id) {
        usersService.addFriend(id).subscribe(new Observer<AddFriendResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AddFriendResponse addFriendResponse) {
                if (addFriendResponse.success) {
                    // If add friend successful, show alert dialogue to user to show that user has been added
                    Toast.makeText(getApplicationContext(), "User have been added successfully. Please wait for confirmation.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Fail to add. Please wait for confirmation", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                // Throw message if add friend fails
                Toast.makeText(getApplicationContext(), "Error adding friend. Please retry.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
