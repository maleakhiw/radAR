package radar.radar.Presenters;

import android.content.Context;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Services.UsersService;
import radar.radar.Views.UserDetailView;

/**
 * Presenter for UserDetailActivity
 * This class contains application logic of UserDetailActivity
 */
public class UserDetailPresenter {
    UserDetailView userDetailView;
    UsersService usersService;

    /**
     * Constructor for UserDetailPresenter
     * @param userDetailView view for which the presenter is called
     * @param usersService service that are instantiated on UserDetailActivity
     */
    public UserDetailPresenter(UserDetailView userDetailView, UsersService usersService) {
        this.userDetailView = userDetailView;
        this.usersService = usersService;
    }

    /**
     * Generating friend requests
     * @param id id of the user we want to request friend with
     */
    public void generateFriendRequest(int id) {
        usersService.addFriend(id).subscribe(new Observer<AddFriendResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AddFriendResponse addFriendResponse) {
                if (addFriendResponse.success) {
                    userDetailView.showToastShort("Friend request sent");
//                    userDetailView.hideAddFriend();
                }
                else {
                    userDetailView.showToastShort("Friend request already sent");
//                    userDetailView.showToastShort("User have been added previously. Please wait for confirmation.");
                }
            }

            @Override
            public void onError(Throwable e) {
                // Throw message if add friend fails
                userDetailView.showToastShort("Internal error. Failed to add friends.");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
