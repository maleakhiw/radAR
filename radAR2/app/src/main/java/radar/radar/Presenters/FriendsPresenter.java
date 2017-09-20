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
        this.friendsView = friendsView;
        this.usersApi = usersApi;
        this.usersService = new UsersService(usersApi, (Context) friendsView);

//        AuthApi authApi = retrofit.create(AuthApi.class);
//        AuthService authService = new AuthService(authApi, (Context) friendsView);

//        loadFriends();
        // For now to check the friends I will add several friend to the arraylist
        ArrayList<User> friends = new ArrayList<>();
        User maleakhiw = new User(1, "maleakhiw", "Maleakhi", "Wijaya", "My name is Maleakhi Agung Wijaya");
        User krusli = new User(2, "krusli", "Kenneth", "Rusli", "My name is Kenneth");
        User manshar = new User(3, "manshar", "Fadhil", "Anshar", "am a horse");
        User ricky = new User(4, "rtanudjaja", "Ricky", "Tanudjaja", "am a human");
        friends.add(maleakhiw);
        friends.add(krusli);
        friends.add(manshar);
        friends.add(ricky);
        System.out.println(friends);
        friendsView.bindAdapterToRecyclerView(friends);

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
