package radar.radar.Models.Requests;

/**
 * Data model for root mean squares
 */
public class RMSGetGroupInfoRequest {
    int userID = 0;
    String username = null;
    int queryUserID;
    String token;
    int groupID;

    /**
     * Constructor
     */
    public RMSGetGroupInfoRequest(int userID, int groupID, int queryUserID, String token) {
        this.userID = userID;
        this.groupID = groupID;
        this.queryUserID = queryUserID;
        this.token = token;
    }

    public RMSGetGroupInfoRequest(String username, int groupID, int queryUserID, String token) {
        this.username = username;
        this.groupID = groupID;
        this.queryUserID = queryUserID;
        this.token = token;
    }
}
