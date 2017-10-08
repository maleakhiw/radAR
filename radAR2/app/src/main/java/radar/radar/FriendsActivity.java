package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class FriendsActivity extends AppCompatActivity implements FriendsView {
    NavigationActivityHelper helper;
    FriendsPresenter presenter;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    private UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Setup drawer and navigation helper
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, this);

        recyclerView = findViewById(R.id.friends_recyclerView);
        fab = findViewById(R.id.fab);
        
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(this, usersApi);

        presenter = new FriendsPresenter(this, usersService);
        presenter.loadFriends();

        setTitle("Friends");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.respondToFABClick();
            }
        });

    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void launchHomeScreenActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void launchSearchFriendsActivity() {
        Intent intent = new Intent(this, SearchResultActivity.class);
        startActivity(intent);
    }


    @Override
    public void bindAdapterToRecyclerView(ArrayList<User> friends) {
        FriendsAdapter friendsAdapter = new FriendsAdapter(this, friends);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   // layout manager to position items
        friendsAdapter.notifyDataSetChanged();

    }

}
