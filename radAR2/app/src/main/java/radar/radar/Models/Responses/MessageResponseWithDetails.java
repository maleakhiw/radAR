package radar.radar.Models.Responses;

import radar.radar.Models.Domain.MessageResponse;
import radar.radar.Models.Domain.User;

public class MessageResponseWithDetails extends MessageResponse {
    public User userDetails;
    public MessageResponseWithDetails(MessageResponse messageResponse, User user) {
        from = messageResponse.from;
        groupID = messageResponse.groupID;
        time = messageResponse.time;
        contentType = messageResponse.contentType;
        text = messageResponse.text;
        contentResourceID = messageResponse.contentResourceID;
        userDetails = user;
    }
}