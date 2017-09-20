package radar.radar.Models.Requests;

import java.util.ArrayList;

/**
 * Created by kenneth on 20/9/17.
 */

public class NewChatRequest {   // TODO ideally what we POST should be what we get back
    public ArrayList<Integer> participantUserIDs;
    public String name; // name for the chat
}
