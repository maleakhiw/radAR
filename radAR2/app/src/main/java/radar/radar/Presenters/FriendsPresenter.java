package radar.radar.Presenters;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.HomeScreenActivity;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.User;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.FriendsView;

public class FriendsPresenter {
    FriendsView friendsView;
    UsersApi usersApi;
    UsersService usersService;

    public FriendsPresenter(FriendsView friendsView, UsersApi usersApi) {
        // TODO REMOVE ANDROID DEPENDENCIES

        this.friendsView = friendsView;
        this.usersApi = usersApi;
        this.usersService = new UsersService(usersApi, (Context) friendsView);

        loadFriends();

        friendsView.setFABOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent((Context) friendsView, HomeScreenActivity.class);
                friendsView.startActivityFromIntent(intent);
            }
        });
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
                    friendsView.showToast("Error occurred");
                }
            }

            @Override
            public void onError(Throwable e) {
                friendsView.showToast("Error occurred");
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
