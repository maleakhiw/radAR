package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.MeetingPoint;
import radar.radar.Models.User;
import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 2/10/17.
 */

public class GroupLocationsInfo extends Status {
    public MeetingPoint meetingPoint;
    public ArrayList<UserLocation> locations;

    public GroupLocationsInfo(MeetingPoint meetingPoint, ArrayList<UserLocation> locations, HashMap<Integer, User> userDetails) {
        this.meetingPoint = meetingPoint;
        this.locations = locations;
        this.userDetails = userDetails;
    }

    public HashMap<Integer, User> userDetails;  // TODO not all populated
}