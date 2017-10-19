package radar.radar.Models;

/**
 * Created by kenneth on 18/10/17.
 */

public class UpdateGroupBody {
    String name;
    String profilePicture;

    public UpdateGroupBody(String name, String profilePicture) {
        this.name = name;
        this.profilePicture = profilePicture;
    }
}
