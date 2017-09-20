package radar.radar.Models;

import java.io.Serializable;

public class User implements Serializable{
    public int userID;
    public String username;
    public String firstName;

    public User(int userID, String username, String firstName, String lastName, String profilePicture, String profileDesc) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.profileDesc = profileDesc;
    }

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
