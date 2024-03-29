package radar.radar.Models.Domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class the represents data model of user. Used to support MVP and retrofit.
 */
public class User implements Serializable {
    public int userID;
    public String username;
    public String firstName;
    public String lastName;
    public String profilePicture;
    public String profileDesc;
    public String email;
    public HashMap<Integer, Group> commonGroups;    // may be null

    public boolean isFriend;

    /** Constructor for User class */
    public User(int userID, String username, String firstName, String lastName, String profileDesc, String email) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = "";
        this.profileDesc = profileDesc;
        this.email = email;
    }

    public User(int userID, String username, String firstName, String lastName, String profilePicture, String profileDesc, String email) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.profileDesc = profileDesc;
        this.email = email;
    }

}
