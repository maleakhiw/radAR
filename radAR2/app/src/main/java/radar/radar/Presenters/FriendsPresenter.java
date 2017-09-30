package radar.radar.Presenters;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Services.UsersService;
import radar.radar.Views.FriendsView;

public class FriendsPresenter {
    FriendsView friendsView;
    UsersService usersService;

    public FriendsPresenter(FriendsView friendsView, UsersService usersService) {
        this.friendsView = friendsView;
        this.usersService = usersService;
    }

    public void respondToFABClick() {
        friendsView.launchSearchFriendsActivity();
    }

    public void loadFriends() {
        usersService.getFriends().subscribe(new Observer<FriendsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendsResponse friendsResponse) {
                if (friendsResponse.success) {
                    friendsView.bindAdapterToRecyclerView(friendsResponse.friends);
                    System.out.println(friendsResponse.friends);
                } else {
                    friendsView.showToast("Failed to load friends");
                }
            }

            @Override
            public void onError(Throwable e) {
                friendsView.showToast("Unauthorized token. Please login again");
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
