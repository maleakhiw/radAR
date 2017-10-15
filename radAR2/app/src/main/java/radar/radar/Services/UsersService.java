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
 * Service for users that served as layer of abstraction for retrofit. The methods here
 * will call UsersApi.java
 */
public class UsersService {
    UsersApi usersApi;
    Context context;
    int userID;
    String token;

    /**
     * Constructor for UsersService
     * @param context context from where the activity called the service
     * @param usersApi api regarding users that have been initiated on the activity
     */
    public UsersService(Context context, UsersApi usersApi) {
        this.context = context;
        this.usersApi = usersApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    /**
     * Representing possible value of processing request
     */
    public static enum REQUEST_ACTION {
        ACCEPT, DECLINE
    };

    /**
     * Returns a list of friends for the logged-in user.
     * userID and token required for requests retrieved from AuthService.
     * @return response from API server
     */
    public Observable<FriendsResponse> getFriends() {
        // get userID and token from AuthService
        // TODO check if userID 0, if 0 return an Observable which only emits an error (which should boot the user to the Login screen in the Presenter)
        Observable<FriendsResponse> observable = usersApi.getFriends(userID, token)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    /**
     * Sends an invitation (friend request) to a user on RadAR.
     * @param invitedUserID user to send friend request to
     * @return response from API server - with a requestID
     */
    public Observable<AddFriendResponse> addFriend(int invitedUserID) {
        Observable<AddFriendResponse> observable = usersApi.addFriend(userID, token,
                                                        new AddFriendRequest(invitedUserID))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    /**
     * Retrieves any pending friend requests.
     * @return pending friend requests
     */
    public Observable<FriendRequestsResponse> getFriendRequests() {
        return usersApi.getFriendRequests(userID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Accept or decline a friend request
     * @param requestID ID of request to respond to
     * @param requestAction accept or decline the friend request
     * @return success or failure + reasons
     */
    public Observable<Status> respondToFriendRequest(int requestID, REQUEST_ACTION requestAction) {
        String action;
        if (requestAction == REQUEST_ACTION.ACCEPT) {
            action = "accept";
        } else {
            action = "decline";
        }

        return usersApi.respondToFriendRequest(userID, token, requestID, new RespondToRequestRequest(action))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Search for users using keyword
     * @param query keyword that are used for search
     * @param searchType search type (i.e. email, username, full name)
     * @return
     */
    public Observable<UsersSearchResult> searchForUsers(String query, String searchType) {
        return usersApi.searchForUsers(userID, token, query, searchType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
