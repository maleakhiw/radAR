package radar.radar.Models.Requests;

import java.util.ArrayList;

/**
 * Data model for new chat request
 */
public class NewChatRequest {
    public String name; // name for the chat
    public ArrayList<Integer> participantUserIDs;

    /**
     * Constructor for new chat
     * @param participantUserIDs array list of participants
     * @param name name of the chat
     */
    public NewChatRequest(ArrayList<Integer> participantUserIDs, String name) {
        this.participantUserIDs = participantUserIDs;
        this.name = name;
    }
}
