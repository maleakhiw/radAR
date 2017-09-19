package radar.radar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import radar.radar.Adapters.FriendsAdapter;
import radar.radar.Models.Responses.User;
import radar.radar.Presenters.FriendsPresenter;
import radar.radar.Views.FriendsView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity implements FriendsView {

    FriendsPresenter presenter;
    RecyclerView recyclerView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerView = findViewById(R.id.friends_recyclerView);
        fab = findViewById(R.id.fab);

        presenter = new FriendsPresenter(this, new Retrofit.Builder()
                                                                .baseUrl("http://35.185.35.117/api/")
                                                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                                                .addConverterFactory(GsonConverterFactory.create())
                                                                .build());


    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG);
    }

    @Override
    public void bindAdapterToRecyclerView(ArrayList<User> friends) {
        FriendsAdapter friendsAdapter = new FriendsAdapter(this, friends);
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   // layout manager to position items
        friendsAdapter.notifyDataSetChanged();

    }

    @Override
    public void setFABOnClickListener(View.OnClickListener onClickListener) {
        fab.setOnClickListener(onClickListener);
    }

    @Override
    public void startActivityFromIntent(Intent intent) {
        startActivity(intent);
    }

}
