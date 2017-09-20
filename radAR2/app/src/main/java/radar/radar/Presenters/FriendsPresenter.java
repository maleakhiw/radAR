package radar.radar.Presenters;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.HomeScreenActivity;
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Services.FriendsApi;
import radar.radar.Services.FriendsService;
import radar.radar.Views.FriendsView;
import retrofit2.Retrofit;

/**
 * Created by kenneth on 19/9/17.
 */

public class FriendsPresenter {
    FriendsView friendsView;
    FriendsApi friendsApi;
    FriendsService friendsService;

    public FriendsPresenter(FriendsView friendsView, FriendsApi friendsApi) {
        this.friendsView = friendsView;
        this.friendsApi = friendsApi;
        this.friendsService = new FriendsService(friendsApi, (Context) friendsView);

//        AuthApi authApi = retrofit.create(AuthApi.class);
//        AuthService authService = new AuthService(authApi, (Context) friendsView);

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
        friendsService.getFriends().subscribe(new Observer<FriendsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FriendsResponse friendsResponse) {
                System.out.println(friendsResponse);

                if (friendsResponse.success) {
                    friendsView.bindAdapterToRecyclerView(friendsResponse.friends);
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
