package radar.radar.Services;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.AddFriendRequest;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Models.SearchUserResponse;
import radar.radar.Models.UpdateGroupBody;
import radar.radar.Models.UpdateProfileBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    public Observable<SearchUserResponse> getProfile(int queryUserID) {
        return usersApi.getUserProfile(queryUserID, userID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets the profile picture for a user
     * @param queryUserID user to get the profile picture for
     * @param resourcesService Service, required to access profile picture
     * @param context Android Context, to load data from SharedPreferences and internal storage
     * @return
     */
    public Observable<File> getProfilePicture(int queryUserID, ResourcesService resourcesService, Context context) {
        return getProfile(queryUserID).switchMap(searchUserResponse -> {
            if (searchUserResponse.details != null && searchUserResponse.details.profilePicture != null) {
                SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
                prefs.edit().putString("profilePicture", searchUserResponse.details.profilePicture).apply();
                // check if the fileID is in cache
                return resourcesService.getResourceWithCache(searchUserResponse.details.profilePicture, context);
            } else {
                throw new Exception("User has no profile picture"); // to go to onError
            }
        });
    }

    /**
     * Returns a list of friends for the logged-in user.
     * userID and token required for requests retrieved from AuthService.
     * @return response from API server
     */
    public Observable<FriendsResponse> getFriends() {
        if (userID == 0) {
            return Observable.error(new Throwable("loggedOut"));
        }

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
     * @return search results
     */
    public Observable<UsersSearchResult> searchForUsers(String query, String searchType) {
        return usersApi.searchForUsers(userID, token, query, searchType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Updates the user profile for the signed in user.
     * @param body fields to be changed from the profile
     * @return success or failure + reasons
     */
    public Observable<Status> updateProfile(UpdateProfileBody body) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);

        return usersApi.updateProfile(userID, token, body)
                .subscribeOn(Schedulers.io())
                .map(result -> {
                    if (result.success) {
                        if (body.firstName != null) {
                            prefs.edit().putString("firstName", body.firstName)
                                    .putString("lastName", body.lastName).apply();
                        }
                        if (body.email != null) {
                            prefs.edit().putString("email", body.email).apply();
                        }
                        if (body.profileDesc != null) {
                            prefs.edit().putString("profileDesc", body.profileDesc).apply();
                        }
                        if (body.profilePicture != null) {
                            prefs.edit().putString("profilePicture", body.profilePicture).apply();
                        }
                    }

                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Retrieves the last known profile picture resource ID from SharedPreferences. Returns null if unset.
     * @param context Android Context
     * @return resource ID
     */
    public static String getProfilePictureResID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        return prefs.getString("profilePicture", null);
    }
}
