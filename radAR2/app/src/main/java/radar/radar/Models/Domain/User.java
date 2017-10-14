package radar.radar.Models.Domain;

import java.io.Serializable;

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

}
