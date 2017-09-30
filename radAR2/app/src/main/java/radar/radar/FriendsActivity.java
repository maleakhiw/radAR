package radar.radar;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.FriendsAdapter;
import radar.radar.Adapters.SearchAdapter;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Models.User;
import radar.radar.Presenters.FriendsPresenter;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.FriendsView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity implements FriendsView {

    FriendsPresenter presenter;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    private UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = findViewById(R.id.friends_recyclerView);
        fab = findViewById(R.id.fab);
        
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(usersApi, this);

        presenter = new FriendsPresenter(this, usersService);

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
        finish();
    }


    @Override
    public void bindAdapterToRecyclerView(ArrayList<User> friends) {
        FriendsAdapter friendsAdapter = new FriendsAdapter(this, friends);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   // layout manager to position items
        friendsAdapter.notifyDataSetChanged();

    }

}
