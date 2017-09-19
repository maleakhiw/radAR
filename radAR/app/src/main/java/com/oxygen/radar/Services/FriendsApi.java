package com.oxygen.radar.Services;

import com.oxygen.radar.Models.Requests.AddFriendRequest;
import com.oxygen.radar.Models.Responses.AddFriendResponse;
import com.oxygen.radar.Models.Responses.FriendRequestsResponse;
import com.oxygen.radar.Models.Responses.FriendsResponse;
import com.oxygen.radar.Models.Responses.Status;
import com.oxygen.radar.Models.Responses.UsersSearchResult;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by kenneth on 18/9/17.
 */

public interface FriendsApi {
    @POST("accounts/{userID}/friends")
    Observable<AddFriendResponse> addFriend(@Path(value="userID", encoded=true) int userID, @Header("token") String token, @Body AddFriendRequest body);

    @GET("accounts/{userID}/friendRequests")
    Observable<FriendRequestsResponse> getFriendRequests(@Path(value="userID", encoded=true) int userID, @Header("token") String token);

    // friends list
    @GET("accounts/{userID}/friends")
    Observable<FriendsResponse> getFriends(@Path(value="userID", encoded=true) int userID, @Header("token") String token);

    // respond to friend request
    @DELETE("accounts/{userID}/friendRequests/{requestID}")
    Observable<Status> respondToFriendRequest(@Path(value = "userID", encoded = true) int userID, @Header("token") String token, @Path(value = "requestID", encoded = true) int requestID, @Body RespondToRequestRequest body);

    // search
    @GET("users")
    Observable<UsersSearchResult> searchForUsers(@Query("userID") int userID, @Header("token") String token, @Query("query") String query, @Query("searchType") String searchType);
}
