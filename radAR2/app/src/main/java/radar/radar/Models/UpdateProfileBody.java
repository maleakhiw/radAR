package radar.radar.Models;

/**
 * Created by kenneth on 18/10/17.
 */

public class UpdateProfileBody {
    String name;
    String profilePicture;

    public UpdateProfileBody(String name, String profilePicture) {
        this.name = name;
        this.profilePicture = profilePicture;
    }
}
