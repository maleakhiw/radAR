package radar.radar.Models.Requests;

import java.util.ArrayList;

/**
 * Data model for new group
 */
public class NewGroupBody {
    String name;
    ArrayList<Integer> participantUserIDs;

    /**
     * Constructor
     */
    public NewGroupBody(String name, ArrayList<Integer> participantUserIDs) {
        this.name = name;
        this.participantUserIDs = participantUserIDs;
    }
}
