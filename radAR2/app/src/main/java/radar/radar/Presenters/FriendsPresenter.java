package radar.radar.Presenters;

import android.content.Context;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
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

    public FriendsPresenter(FriendsView friendsView, Retrofit retrofit) {
        this.friendsView = friendsView;
        this.friendsApi = retrofit.create(FriendsApi.class);
        this.friendsService = new FriendsService(friendsApi, (Context) friendsView);

        AuthApi authApi = retrofit.create(AuthApi.class);
        AuthService authService = new AuthService(authApi, (Context) friendsView);
//        authService.login("manshar", "hunter2").subscribe(new Observer<AuthResponse>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(AuthResponse authResponse) {
//                System.out.println(authResponse.token);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println(e);
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//        System.out.println(AuthService.getToken((Context) friendsView));

        loadFriends();
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
                    // load ArrayList to adapter
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
