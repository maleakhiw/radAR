package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MessageResponse;

/**
 * Data model for getting chat response. Shows the chat ids related to particular user
 */
public class GetChatsResponse extends Status {
    public ArrayList<Group> groups;
    public HashMap<Integer, MessageResponse> groupsDetails; // server-side sorting, by creation and last message received

    public GetChatsResponse(ArrayList<Group> groups, boolean success) {
        this.groups = groups;
        this.success = success;
    }
}
