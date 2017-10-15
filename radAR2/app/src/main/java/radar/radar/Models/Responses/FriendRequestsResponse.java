package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Data model that will be sent when user request a pending friend request for them
 */
public class FriendRequestsResponse extends Status {
    public ArrayList<FriendRequest> requestDetails;

    /**
     * Constructor for FriendRequestsResponse
     * @param requestDetails list of friend requests
     */
    public FriendRequestsResponse(ArrayList<FriendRequest> requestDetails) {
        this.requestDetails = requestDetails;
    }
}
