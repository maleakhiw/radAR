package radar.radar.Models.Responses;

import java.util.ArrayList;

import radar.radar.Models.User;

/**
 * Created by kenneth on 17/9/17.
 */

public class AuthResponse {
    public boolean success;
    public ArrayList<StatusError> errors;
    public String token;
    public int userID;
    public User userInfo;

    public AuthResponse(boolean success, ArrayList<StatusError> errors, String token, int userID) {
        this.success = success;
        this.errors = errors;
        this.token = token;
        this.userID = userID;
    }
}
