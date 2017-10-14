package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Data model that will be sent when user request a pending friend request for them
 */
public class FriendRequestsResponse extends Status {
    public ArrayList<FriendRequest> requestDetails;
}
