package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.User;
import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 2/10/17.
 */

public class MembersLocationResponse extends Status {
    public ArrayList<UserLocation> locations;
    public HashMap<Integer, User> userDetails;  // TODO not all populated
}
