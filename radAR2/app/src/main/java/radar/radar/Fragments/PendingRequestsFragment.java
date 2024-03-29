package radar.radar.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import radar.radar.Adapters.FriendsRequestAdapter;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Presenters.PendingRequestsPresenter;
import radar.radar.R;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.PendingRequestsView;
import retrofit2.Retrofit;

import static radar.radar.RetrofitFactory.*;

/**
 * Fragment that are used to display pending friend request that a user have
 */
public class PendingRequestsFragment extends Fragment implements PendingRequestsView {
    /** UI variable */
    private RecyclerView recyclerView2;

    /** Variable for service and presenter */
    private PendingRequestsPresenter presenter;
    private UsersService usersService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_requests_fragment, container, false);
        recyclerView2 = view.findViewById(R.id.requestRecyclerView);


        // Create retrofit instance
        Retrofit retrofit = getRetrofitBuilder().build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(getActivity(), usersApi);

        // Initiate presenter
        presenter = new PendingRequestsPresenter(this, usersService);
        presenter.displayFriendsRequest(); // call display pending request method

        return view;
    }

    /**
     * Displaying message in the form of toast to user
     * @param message message to be sent to user screen in toast
     */
    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.startUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stopUpdates();
    }

    /**
     * Displaying pending requests
     * @param friendRequestsResponse representing the response when displaying pending request
     */
    @Override
    public void bindAdapterToRecyclerView(FriendRequestsResponse friendRequestsResponse) {
        Activity activity = getActivity();

        if (activity != null) {
            FriendsRequestAdapter adapter = new FriendsRequestAdapter(getActivity(), friendRequestsResponse.requestDetails);
            recyclerView2.setAdapter(adapter);
            recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView2.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL));
            adapter.notifyDataSetChanged();
        } else {
            Log.w("PendingRequests", "Activity is null");
        }

    }
}