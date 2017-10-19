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
     * @param name name of the chat
     * @param participantUserIDs array list of participants
     */
    public NewChatRequest(String name, ArrayList<Integer> participantUserIDs) {
        this.participantUserIDs = participantUserIDs;
        this.name = name;
    }
}
