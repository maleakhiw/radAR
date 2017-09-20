package radar.radar.Models.Responses;

import java.util.ArrayList;

import radar.radar.Models.User;

public class FriendsResponse extends Status {
    public FriendsResponse(ArrayList<User> friends) {
        this.friends = friends;
    }

    public ArrayList<User> friends;
}
