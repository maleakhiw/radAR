package radar.radar.Models.Responses;


import java.util.ArrayList;

import radar.radar.Models.Group;
import radar.radar.Models.User;

public class GroupsResponse extends Status {
    public Group group;
    public GroupsResponse(Group group) {
        this.group = group;
    }
}
