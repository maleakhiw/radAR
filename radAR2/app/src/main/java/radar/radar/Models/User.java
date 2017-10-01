package radar.radar.Models;

import java.io.Serializable;

public class User implements Serializable{
    public int userID;
    public String username;
    public String firstName;
    public String lastName;
    public String profilePicture;
    public String profileDesc;
    public String email;

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
