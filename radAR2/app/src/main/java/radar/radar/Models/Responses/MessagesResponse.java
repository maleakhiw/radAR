package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.MessageResponse;
import radar.radar.Models.Domain.User;

/**
 * Data model for message response
 */
public class MessagesResponse extends Status {
    public ArrayList<MessageResponse> messages;
    public HashMap<Integer, User> usersDetails;
}
