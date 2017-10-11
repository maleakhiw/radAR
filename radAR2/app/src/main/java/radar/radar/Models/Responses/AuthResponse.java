package radar.radar.Models.Responses;

import java.util.ArrayList;

import radar.radar.Models.Domain.User;

/**
 * Data model for AuthResponse that will be send back after user signup or login
 */
public class AuthResponse {
    public boolean success;
    public ArrayList<StatusError> errors;
    public String token;
    public int userID;
    public User userInfo;

    /** Constructor for AuthResponse */
    public AuthResponse(boolean success, ArrayList<StatusError> errors, String token, int userID) {
        this.success = success;
        this.errors = errors;
        this.token = token;
        this.userID = userID;
    }
}
