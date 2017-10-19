package radar.radar.Models.Responses;

import radar.radar.Models.Domain.Group;

/**
 * Getting information from a particular group
 */
public class GetChatInfoResponse extends Status {
    public Group group;

    public GetChatInfoResponse(Group group) {
        this.group = group;
    }
}
