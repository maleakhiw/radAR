package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.User;
import radar.radar.Presenters.UserDetailPresenter;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.UserDetailView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDetailActivity extends AppCompatActivity implements UserDetailView {
    private TextView fullname;
    private TextView username;
    private TextView userDetailsProfile;
    private TextView userDetailsEmail;
    private TextView userDetailsPhoneNumber;
    private FloatingActionButton messageFab;
    private ImageView add;

    private User user;
    private UsersService usersService;

    private UserDetailPresenter userDetailPresenter;

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
        userDetailsEmail.setText("Hidden email address");
        userDetailsPhoneNumber.setText("No phone number given");
        userDetailsProfile.setText(user.profileDesc);

        // initialize presenter
        userDetailPresenter = new UserDetailPresenter(this, usersService);


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
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add friend
                userDetailPresenter.generateFriendRequest(user.userID);
            }
        });

    }

    /** Will be used to show message on the form of toast on the presenter class */
    @Override
    public void showToastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /** Setup ui by connecting layout item with java */
    public void setupUI() {
        messageFab = findViewById(R.id.fab_message);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        userDetailsProfile = findViewById(R.id.user_details_profile);
        userDetailsEmail = findViewById(R.id.user_details_email);
        userDetailsPhoneNumber = findViewById(R.id.user_details_phone_number);
        add = findViewById(R.id.userAddFriend);
    }

}
