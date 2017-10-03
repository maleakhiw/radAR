package radar.radar;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.FriendsRequestAdapter;
import radar.radar.Adapters.SearchAdapter;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Models.User;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultActivity extends AppCompatActivity {
    private UsersService usersService;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;

    private EditText query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result2);

        // Enable back action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Instantiate recyclerview and query edit view
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView2 = findViewById(R.id.requestRecyclerView);

        query = findViewById(R.id.search_bar);

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(usersApi, this);

        // When edit text is entered do search
        query.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // if entered do search
                    doSearch(query.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // Display pending friend requests for particular user
        displayFriendsRequest();

    }


    /** Method that are used for the back */
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    /** This search method will search the appropriate user using the user's query */
    public void doSearch(String query) {
        // By default search type is name
        usersService.searchForUsers(query, "name").subscribe(new Observer<UsersSearchResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UsersSearchResult usersSearchResult) {
                // After we get the search result, it will be array list of Users
                // We need to somehow display this user result to a recycler view
                if (usersSearchResult.results.size() != 0) {
                    SearchAdapter searchAdapter = new SearchAdapter(SearchResultActivity.this, usersSearchResult.results);
                    recyclerView.setAdapter(searchAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
                    searchAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "No user with this name found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "Error occurred.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void displayFriendsRequest() {
        // just display all of the friend request for a given user
        usersService.getFriendRequests().subscribe(new Observer<FriendRequestsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendRequestsResponse friendRequestsResponse) {
                // Check status of the response
                if (friendRequestsResponse.success) {
                    // Display on the recycler view
                    FriendsRequestAdapter adapter = new FriendsRequestAdapter(SearchResultActivity.this, friendRequestsResponse.requestDetails);
                    recyclerView2.setAdapter(adapter);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error generating requests.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
