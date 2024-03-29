package radar.radar.Presenters;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Services.UsersService;
import radar.radar.Views.FriendsView;

/**
 * Class representing application logic/ presenter for FriendsActivity
 */
public class FriendsPresenter {
    FriendsView friendsView;
    UsersService usersService;

    /**
     * Constructor for FriendsPresenter
     * @param friendsView the view from which the activity being run (FriendsActivity)
     * @param usersService service that have been initialised in FriendsActivity
     */
    public FriendsPresenter(FriendsView friendsView, UsersService usersService) {
        this.friendsView = friendsView;
        this.usersService = usersService;
    }

    /**
     * Responding to add friend button click and go to search user activity
     */
    public void respondToFABClick() {
        friendsView.launchSearchFriendsActivity();
    }

    boolean isUpdating = false;
    public void startUpdates() {
        isUpdating = true;
        Observable.interval(5, TimeUnit.SECONDS)
                .takeWhile(l -> isUpdating)
                .subscribe(tick -> loadFriends());
    }

    public void stopUpdates() {
        isUpdating = false;
    }



    /**
     * Load friends that the user have on the recycler view
     */
    public void loadFriends() {
        usersService.getFriends().subscribe(new Observer<FriendsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendsResponse friendsResponse) {
                if (friendsResponse.success) {
                    friendsView.updateAdapterDataset(friendsResponse.friends);
                } else {
                    friendsView.showToast("Failed to load friends.");
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
                friendsView.showToast("Internal Error. Failed to load friends.");
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
