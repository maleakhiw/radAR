package radar.radar.Models.Responses;


import java.util.ArrayList;

public class Group {
    public String name;
    public int groupID;
    public ArrayList<Integer> members;
    public ArrayList<Integer> admins;
    // TODO add footprints
    // TODO add meeting point
    public boolean isTrackingGroup;
}
