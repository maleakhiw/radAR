package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.User;

/**
 * Created by kenneth on 20/9/17.
 */

public class MessagesResponse extends Status {
    public ArrayList<MessageResponse> messages;
    public HashMap<Integer, User> usersDetails;
}
