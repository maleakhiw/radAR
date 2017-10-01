package radar.radar.Presenters;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Services.UsersService;
import radar.radar.Views.UserDetailView;

/**
 * Created by keyst on 30/09/2017.
 */

public class UserDetailPresenter {
    UserDetailView userDetailView;
    UsersService usersService;

    public UserDetailPresenter(UserDetailView userDetailView, UsersService usersService) {
        this.userDetailView = userDetailView;
        this.usersService = usersService;
    }

    /** This method is used to create friend request */
    public void generateFriendRequest(int id) {
        usersService.addFriend(id).subscribe(new Observer<AddFriendResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AddFriendResponse addFriendResponse) {
                if (addFriendResponse.success) {
                    // If add friend successful, show alert dialogue to user to show that user has been added
                    userDetailView.showToastLong("User have been added successfully.");
                }
                else {
                    userDetailView.showToastLong("User have been added previously. Please wait for confirmation.");
                }
            }

            @Override
            public void onError(Throwable e) {
                // Throw message if add friend fails
                userDetailView.showToastLong("Error adding friends.");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
