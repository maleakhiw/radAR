package radar.radar;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import radar.radar.Models.Domain.User;
import radar.radar.Presenters.UserDetailPresenter;
import radar.radar.Services.AuthService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.UserDetailView;
import retrofit2.Retrofit;

/**
 * Class that handle displaying user details and information (profile)
 */
public class UserDetailActivity extends AppCompatActivity implements UserDetailView {
    /** UI variable */
    private TextView fullName;
    private TextView username;
    private TextView userDetailsProfile;
    private TextView userDetailsEmail;
    private FloatingActionButton messageFab;
    private ImageView add;
    private User user;

    /** Service and Presenter variable */
    private UsersService usersService;
    private UserDetailPresenter userDetailPresenter;


    private boolean isSelf(Context context, int userID) {
        return AuthService.getUserID(context) == userID;
    }

    private void updateViewForSelf() {
        messageFab.setImageDrawable(getDrawable(R.drawable.ic_edit_white_24dp));
        add.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Setup the User Interface
        setupUI();

        // Enable back action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup Retrofit Instances
        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

        // Setup user api that will be used to generate a service so that we can add friends
        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(this, usersApi);

        // Get the information for user to display
        user = (User) getIntent().getSerializableExtra("user");
        fullName.setText(user.firstName + " " + user.lastName);
        username.setText(user.username);
        userDetailsEmail.setText(user.email);

        if (user.isFriend) {
            add.setVisibility(View.GONE);
        }

        // This is if user doesn't set any profile description
        if (user.profileDesc != null) {
            userDetailsProfile.setText(user.profileDesc);
        } else {
            userDetailsProfile.setText("No description set.");
        }

        if (isSelf(this, user.userID)) {
            updateViewForSelf();
            messageFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Go to home page
                    Intent intent = new Intent(UserDetailActivity.this, EditProfileActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

        } else {
            // On click listener for fab
            messageFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Go to home page
                    Intent intent = new Intent(UserDetailActivity.this, ChatActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });
        }

        // Initialise presenter
        userDetailPresenter = new UserDetailPresenter(this, usersService);




        // On click listener for add friends
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add friend
                userDetailPresenter.generateFriendRequest(user.userID);
            }
        });

    }

    /**
     * Method that are used for the back button
     */
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    /**
     * Will be used to show message on the form of toast on the presenter class
     */
    @Override
    public void showToastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideAddFriend() {
        if (add != null) {
            add.setVisibility(View.GONE);
        }
    }

    /**
     * Setup ui by connecting layout item with java
     */
    public void setupUI() {
        messageFab = findViewById(R.id.fab_message);
        fullName = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        userDetailsProfile = findViewById(R.id.user_details_profile);
        userDetailsEmail = findViewById(R.id.user_details_email);
        add = findViewById(R.id.userAddFriend);

        setTitle("Details");
    }

}
