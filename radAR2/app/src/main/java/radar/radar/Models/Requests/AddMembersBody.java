package radar.radar.Models.Requests;

import java.util.ArrayList;

/**
 * Created by kenneth on 21/10/17.
 */

public class AddMembersBody {
    ArrayList<Integer> participantUserIDs;

    public AddMembersBody(ArrayList<Integer> participantUserIDs) {
        this.participantUserIDs = participantUserIDs;
    }
}
