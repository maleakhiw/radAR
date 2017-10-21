package radar.radar.Models.Responses;

import radar.radar.Models.Domain.Group;

/**
 * Data model for group response
 */
public class GroupsResponse extends Status {
    public Group group;
    public GroupsResponse(Group group) {
        this.group = group;
    }
}
