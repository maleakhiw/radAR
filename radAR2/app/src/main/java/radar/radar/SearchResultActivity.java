package radar.radar;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.SearchAdapter;
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

    private EditText query;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Instantiate recyclerview and query edit view
        recyclerView = findViewById(R.id.searchRecyclerView);
        query = findViewById(R.id.search_bar);
        search = findViewById(R.id.search);

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(usersApi, this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If clicked do search
                doSearch(query.getText().toString());
            }
        });

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
}
