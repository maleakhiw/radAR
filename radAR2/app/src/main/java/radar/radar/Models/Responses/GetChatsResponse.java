package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Data model for getting chat response. Shows the chat ids related to particular user
 */
public class GetChatsResponse extends Status {
    public ArrayList<Integer> groups;

    public GetChatsResponse(ArrayList<Integer> groups, boolean success) {
        this.groups = groups;
        this.success = success;
    }
}
