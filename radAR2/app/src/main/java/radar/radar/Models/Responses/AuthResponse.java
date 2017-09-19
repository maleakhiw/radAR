package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Created by kenneth on 17/9/17.
 */

public class AuthResponse {
    public boolean success;
    public ArrayList<StatusError> errors;
    public String token;
    public int userID;
}
