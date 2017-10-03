package radar.radar.Models.Responses;


import java.util.ArrayList;

import radar.radar.Models.MeetingPoint;

public class Group {
    public String name;
    public int groupID;
    public ArrayList<Integer> members;
    public ArrayList<Integer> admins;
    // TODO add footprints
    public MeetingPoint meetingPoint;
    public boolean isTrackingGroup;
}
