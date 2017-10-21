package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import radar.radar.Adapters.FriendsAdapter;
import radar.radar.Models.Domain.User;
import radar.radar.Presenters.FriendsPresenter;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.FriendsView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class representing activity to display friend list and the default activity for friends
 * functionality.
 */
public class FriendsActivity extends AppCompatActivity implements FriendsView {
    /** Navigation variable */
    private NavigationActivityHelper helper;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    /** Presenter and service */
    private UsersService usersService;
    private FriendsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Setup UI
        setupUI();

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("https://radar.fadhilanshar.com/api/")
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(this, usersApi);

        presenter = new FriendsPresenter(this, usersService);
        presenter.loadFriends();
        presenter.startUpdates();

        // Setup onclick listener on the fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.respondToFABClick();
            }
        });

    }

    /**
     * Displaying message in front of toast to user
     * @param message message to be displayed on the screen
     */
    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method that are used to go from this activity to home screen immediately
     */
    @Override
    public void launchHomeScreenActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method that are used to go from this activity to search friends/ another user
     */
    @Override
    public void launchSearchFriendsActivity() {
        Intent intent = new Intent(this, FriendRequestActivity.class);
        startActivity(intent);
    }

    /**
     * Binding the adapter to the recycler view and also display the data on the recycler view
     * @param friends array list that consisting of users to be displayed on the list
     */
    @Override
    public void updateAdapterDataset(ArrayList<User> friends) {
        FriendsAdapter friendsAdapter = new FriendsAdapter(this, friends);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   // layout manager to position items
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        friendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.loadFriends();
            presenter.startUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.stopUpdates();
        }
    }

    /**
     * Connecting UI element on the xml files with java
     */
    public void setupUI() {
        // Setup drawer and navigation helper
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        ImageView image = navigationView.getHeaderView(0).findViewById(R.id.profile_picture);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, image, this);

        recyclerView = findViewById(R.id.friends_recyclerView);
        fab = findViewById(R.id.fab);

        setTitle("Friends"); // set the title of the activity
    }

}
