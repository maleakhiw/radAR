package radar.radar.Presenters;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.FriendsRequestAdapter;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Services.UsersService;
import radar.radar.Views.PendingRequestsView;

/**
 * Presenter for PendingRequestsFragment.
 * Contains application logic for pending requests
 */
public class PendingRequestsPresenter {
    private PendingRequestsView view;
    private UsersService usersService;

    /**
     * Constructor for this presenter class
     * @param view PendingRequestsFragment
     * @param service initialised service on PendingRequestsFragment
     */
    public PendingRequestsPresenter(PendingRequestsView view, UsersService service) {
        this.view = view;
        this.usersService = service;
    }

    /**
     * Displaying pending friend request
     */
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
                    view.bindAdapterToRecyclerView(friendRequestsResponse);
                }
                else {
                    view.showToast("Error generating pending requests.");
                }
            }

            @Override
            public void onError(Throwable e) {
                view.showToast("Internal error. Failed to display pending requests.");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
