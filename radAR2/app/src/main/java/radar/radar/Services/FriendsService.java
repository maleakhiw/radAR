package radar.radar.Services;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.AddFriendRequest;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UsersSearchResult;


/**
 * Created by kenneth on 18/9/17.
 */

public class FriendsService {
    FriendsApi friendsApi;
    Context context;
    int userID;
    String token;

    public FriendsService(FriendsApi friendsApi, Context context) {
        this.context = context;
        this.friendsApi = friendsApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    public Observable<FriendsResponse> getFriends() {
        // get userID and token from AuthService
        // TODO check if userID 0, if 0 return an Observable which only emits an error (which should boot the user to the Login screen in the Presenter)
        Observable<FriendsResponse> observable = friendsApi.getFriends(userID, token)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<AddFriendResponse> addFriend(int invitedUserID) {
        Observable<AddFriendResponse> observable = friendsApi.addFriend(userID, token, new AddFriendRequest(invitedUserID))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    public Observable<FriendRequestsResponse> getFriendRequests() {
        return friendsApi.getFriendRequests(userID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Status> respondToFriendRequest(int requestID, String action) {
        return friendsApi.respondToFriendRequest(userID, token, requestID, new RespondToRequestRequest(action))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UsersSearchResult> searchForUsers(String query, String searchType) {
        return friendsApi.searchForUsers(userID, token, query, searchType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}