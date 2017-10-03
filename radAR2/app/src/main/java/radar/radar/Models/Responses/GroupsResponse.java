package radar.radar.Models.Responses;


import radar.radar.Models.Group;

public class GroupsResponse extends Status {
    public Group group;

    public GroupsResponse(Group group) {
        this.group = group;
    }
}
