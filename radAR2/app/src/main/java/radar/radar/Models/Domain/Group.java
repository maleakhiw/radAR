package radar.radar.Models.Domain;

import android.os.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data model for Group (used in tracking group or chat)
 */
public class Group implements Serializable {
    public String name;
    public String profilePicture;
    public int groupID;
    public ArrayList<Integer> members;
    public ArrayList<Integer> admins;
    public ArrayList<Object> footprints;
    public MeetingPoint meetingPoint;
    public boolean isTrackingGroup;
    public HashMap<Integer, User> usersDetails;
    public MessageResponse lastMessage;

    /**
     * Constructor for group
     */
    public Group(String name, boolean isTrackingGroup) {
        this.name = name;
        this.isTrackingGroup = isTrackingGroup;
    }

    public Group(String name, int groupID, ArrayList<Integer> members, ArrayList<Integer> admins, ArrayList<Object> footprints, MeetingPoint meetingPoint, boolean isTrackingGroup, HashMap<Integer, User> usersDetails) {
        this.name = name;
        this.groupID = groupID;
        this.members = members;
        this.admins = admins;
        this.footprints = footprints;
        this.meetingPoint = meetingPoint;
        this.isTrackingGroup = isTrackingGroup;
        this.usersDetails = usersDetails;
    }
}
