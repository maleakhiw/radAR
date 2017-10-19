package radar.radar.Services;


import java.util.List;

import io.reactivex.Observable;
import radar.radar.Models.Requests.AddFriendRequest;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.OnlineStatusesResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Models.SearchUserResponse;
import radar.radar.Models.UpdateProfileBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that are used by Retrofit to do HTTP request regarding user (i.e. friends, user)
 */
public interface UsersApi {
    // Adding friends
    @POST("accounts/{userID}/friends")
    Observable<AddFriendResponse> addFriend(@Path(value = "userID", encoded = true) int userID, @Header("token") String token, @Body AddFriendRequest body);

    // Get pending friend requests
    @GET("accounts/{userID}/friendRequests")
    Observable<FriendRequestsResponse> getFriendRequests(@Path(value = "userID", encoded = true) int userID, @Header("token") String token);

    // Display friend list
    @GET("accounts/{userID}/friends")
    Observable<FriendsResponse> getFriends(@Path(value = "userID", encoded = true) int userID, @Header("token") String token);

    // Respond to friend request
    @POST("accounts/{userID}/friendRequests/{requestID}")
    Observable<Status> respondToFriendRequest(@Path(value = "userID", encoded = true) int userID, @Header("token") String token, @Path(value = "requestID", encoded = true) int requestID, @Body RespondToRequestRequest body);

    @GET("users/{queryUserID}")
    Observable<SearchUserResponse> getUserProfile(@Path(value = "queryUserID", encoded = true) int queryUserID, @Query("userID") int userID, @Header("token") String token);

    // Search another user
    @GET("users")
    Observable<UsersSearchResult> searchForUsers(@Query("userID") int userID, @Header("token") String token, @Query("query") String query, @Query("searchType") String searchType);

    // Get online statuses
    @GET("accounts/{userID}/usersOnlineStatuses")
    Observable<OnlineStatusesResponse> getOnlineStatuses(@Path(value = "userID", encoded = true) int userID, @Query("userIDsToCheck[]") List<Integer> userIDsToCheck);

    @PUT("accounts/{userID}")
    Observable<Status> updateProfile(@Path(value="userID", encoded=true) int userID, @Header("token") String token, @Body UpdateProfileBody body);
}
