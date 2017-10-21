package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Domain.UserLocation;

/**
 * Data model response when getting group locations information
 */
public class GroupLocationsInfo extends Status {
    public MeetingPoint meetingPoint;
    public ArrayList<UserLocation> locations;
    public HashMap<Integer, User> userDetails;

    /**
     * Constructor
     */
    public GroupLocationsInfo(MeetingPoint meetingPoint, ArrayList<UserLocation> locations, HashMap<Integer, User> userDetails) {
        this.meetingPoint = meetingPoint;
        this.locations = locations;
        this.userDetails = userDetails;
    }
}
