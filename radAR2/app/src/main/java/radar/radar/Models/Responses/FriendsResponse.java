package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Created by kenneth on 18/9/17.
 */

public class FriendsResponse extends Status {
    public FriendsResponse(ArrayList<User> friends) {
        this.friends = friends;
    }

    public ArrayList<User> friends;
}
