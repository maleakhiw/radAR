package radar.radar.Models;

/**
 * Created by kenneth on 18/10/17.
 */

public class UpdateProfileBody {
    String firstName;
    String lastName;
    String email;
    String profileDesc;
    String profilePicture;

    public UpdateProfileBody(String name, String email, String profileDesc, String profilePicture) {
        String[] splitStr = name.split("\\s+");
        firstName = splitStr[0];

        lastName = "";

        for (int i=1; i<splitStr.length; i++) {
            lastName += splitStr[i];
            if (i != splitStr.length - 1) {
                lastName += " ";
            }
        }

        this.email = email;
        this.profileDesc = profileDesc;

        this.profilePicture = profilePicture;
    }
}
