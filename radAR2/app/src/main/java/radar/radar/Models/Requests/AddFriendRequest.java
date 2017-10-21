package radar.radar.Models.Requests;

/**
 * Data model for add friend request
 */
public class AddFriendRequest {
    int invitedUserID;

    public AddFriendRequest(int invitedUserID) {
        this.invitedUserID = invitedUserID;
    }
}
