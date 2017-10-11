package radar.radar.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.FriendsRequestAdapter;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.R;
import radar.radar.SearchResultActivity;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by keyst on 8/10/2017.
 */

public class PendingRequestsFragment extends Fragment {
    private RecyclerView recyclerView2;
    private UsersService usersService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_requests_fragment, container, false);
        recyclerView2 = view.findViewById(R.id.requestRecyclerView);


        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(getActivity(), usersApi);

        // Display pending friend requests for particular user
        displayFriendsRequest();


        return view;
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
//                     Display on the recycler view
                    FriendsRequestAdapter adapter = new FriendsRequestAdapter(getActivity(), friendRequestsResponse.requestDetails);
                    recyclerView2.setAdapter(adapter);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity(), "Error generating requests.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "Internal error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}