package domain.testapplication.models;

/**
 * Created by keyst on 8/09/2017.
 */

public class Friend {
    int userID;
    String firstName;
    String lastName;
    String profileDesc;
    String profilePicture;

    public int getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileDesc() {
        return profileDesc;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
