package com.oxygen.radar.Presenters;

import android.content.Context;

import com.oxygen.radar.Models.Requests.SignUpRequest;
import com.oxygen.radar.Models.Responses.AuthResponse;
import com.oxygen.radar.Models.Responses.FriendRequestsResponse;
import com.oxygen.radar.Models.Responses.FriendsResponse;
import com.oxygen.radar.Models.Responses.Status;
import com.oxygen.radar.Models.Responses.User;
import com.oxygen.radar.Models.Responses.UsersSearchResult;
import com.oxygen.radar.Services.AuthApi;
import com.oxygen.radar.Services.AuthService;
import com.oxygen.radar.Services.FriendsApi;
import com.oxygen.radar.Services.FriendsService;
import com.oxygen.radar.Views.LoginView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kenneth on 18/9/17.
 */

public class LoginPresenter {
    LoginView loginView;
    AuthService authService;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;

        // build an Retrofit instance for the Service TODO use Dagger
        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://35.185.35.117/api/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build();
        this.authService = new AuthService(retrofit.create(AuthApi.class), (Context) loginView);

        signUp("Kenneth", "Aloysius", "rusli.kenneth@gmail.com", "krusli", "testPassword");
        signUp("Kenneth", "Aloysius", "rusli.kenneth@gmail.com", "krusli", "testPassword");
        signUp("Kenneth", "Aloysius", "rusli.kenneth@gmail.com", "krusli", "testPassword");
//        login("krusli", "testPassword");
//        login("maleakhiw", "password");

        FriendsService friendsService = new FriendsService(retrofit.create(FriendsApi.class), (Context) loginView);
//        friendsService.searchForUsers("kiki", "name")
//                .subscribe(new Observer<UsersSearchResult>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(UsersSearchResult usersSearchResult) {
//                        System.out.println(usersSearchResult);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
        friendsService.respondToFriendRequest(1, "accept").subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                loginView.showTextToUser("Successfully added friend");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

//        // TODO move to FriendsPresenter
//        FriendsService friendsService = new FriendsService(retrofit.create(FriendsApi.class), (Context) loginView);
//        friendsService.getFriends().subscribe(new Observer<FriendsResponse>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(FriendsResponse friendsResponse) {
//                System.out.println(friendsResponse.success);
//                System.out.println(friendsResponse.friends.size());
//                for (User user: friendsResponse.friends) {
//                    System.out.println(user.firstName);
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }

    public void signUp(String firstName, String lastName, String email, String username, String password) {
        SignUpRequest body = new SignUpRequest(firstName, lastName, email, username, null, password, "fakeDeviceID");
        Observable<AuthResponse> signUpResponseObservable = authService.signUp(body);
        signUpResponseObservable.subscribe(new Observer<AuthResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AuthResponse authResponse) {
                for (int i=0; i< authResponse.errors.size(); i++) {
                    System.out.println(authResponse.errors.get(i).reason);
                }
                System.out.println(authResponse.success);
                System.out.println(authResponse.token);
                System.out.println(authResponse.userID);
            }

            @Override
            public void onError(Throwable e) {
                // TODO call view to notify user of connection error
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void login(String username, String password) {
        System.out.println("login");
        Observable<AuthResponse> loginResponseObservable = authService.login(username, password);
        loginResponseObservable.subscribe(new Observer<AuthResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("Subscribed");
            }

            @Override
            public void onNext(AuthResponse authResponse) {
                for (int i=0; i< authResponse.errors.size(); i++) {
                    System.out.println(authResponse.errors.get(i).reason);
                }
                System.out.println(authResponse.success);
                System.out.println(authResponse.token);
                System.out.println(authResponse.userID);


            }

            @Override
            public void onError(Throwable e) {
                // TODO call view to notify user of connection error
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
