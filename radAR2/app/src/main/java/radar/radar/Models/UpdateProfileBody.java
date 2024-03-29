package radar.radar.Models;

/**
 * Data model for updating profile body
 */
public class UpdateProfileBody {
    public String firstName;
    public String lastName;
    public String email;
    public String profileDesc;
    public String profilePicture;

    /**
     * Constructor
     */
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
