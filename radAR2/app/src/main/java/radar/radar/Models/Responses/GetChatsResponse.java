package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.MessageResponse;

/**
 * Data model for getting chat response. Shows the chat ids related to particular user
 */
public class GetChatsResponse extends Status {
    public ArrayList<Integer> groups;
    public HashMap<Integer, MessageResponse> groupsDetails;

    public GetChatsResponse(ArrayList<Integer> groups, boolean success) {
        this.groups = groups;
        this.success = success;
    }

    public GetChatsResponse(ArrayList<Integer> groups, HashMap<Integer, MessageResponse> groupsDetails, boolean success) {
        this.groups = groups;
        this.groupsDetails = groupsDetails;
        this.success = success;
    }
}
