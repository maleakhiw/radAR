package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.FriendsRequestAdapter;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendRequestActivity extends AppCompatActivity {
    private UsersService usersService;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        recyclerView = findViewById(R.id.requestRecyclerView);

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create user service and api
        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(usersApi, this);

        Log.d("FriendRequestActivity", "iam here");

        displayFriendsRequest();
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
                    FriendsRequestAdapter adapter = new FriendsRequestAdapter(FriendRequestActivity.this, friendRequestsResponse.requestDetails);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(FriendRequestActivity.this));
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error occurred.", Toast.LENGTH_LONG).show();
                    Log.d("Norequest", "no request found");
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
