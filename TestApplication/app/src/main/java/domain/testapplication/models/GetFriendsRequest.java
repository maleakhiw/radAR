package domain.testapplication.models;

/**
 * Created by keyst on 8/09/2017.
 */

public class GetFriendsRequest {
    int userID;
    String token;

    public GetFriendsRequest(int userID, String token) {
        this.userID = userID;
        this.token = token;
    }
}
