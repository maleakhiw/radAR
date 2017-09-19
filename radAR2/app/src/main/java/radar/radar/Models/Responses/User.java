package radar.radar.Models.Responses;

import java.io.Serializable;

/**
 * Created by kenneth on 18/9/17.
 */

public class User implements Serializable{
    public int userID;
    public String username;
    public String firstName;
    public String lastName;
    public String profilePicture;
    public String profileDesc;

    public User(int userID, String username, String firstName, String lastName, String profileDesc) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = "";
        this.profileDesc = profileDesc;
    }

}
