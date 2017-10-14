package radar.radar.Models.Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kenneth on 20/9/17.
 */

public class Group implements Serializable {
    public String name;
    public int groupID;
    public ArrayList<Integer> members;
    public ArrayList<Integer> admins;
    public ArrayList<Object> footprints;
    public MeetingPoint meetingPoint;
    public boolean isTrackingGroup;
    public HashMap<Integer, User> usersDetails;

    public Group(String name, boolean isTrackingGroup) {
        this.name = name;
        this.isTrackingGroup = isTrackingGroup;
    }
}
