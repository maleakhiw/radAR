package radar.radar.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenneth on 20/9/17.
 */

public class Chat implements Serializable {
    public String name;
    public int groupID;
    public ArrayList<Integer> members;
    public ArrayList<Integer> admins;
    public ArrayList<Object> footprints;
    public Object meetingPoint;
    public boolean isTrackingGroup;
}
