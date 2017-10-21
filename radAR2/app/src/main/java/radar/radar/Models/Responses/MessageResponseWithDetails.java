package radar.radar.Models.Responses;

import radar.radar.Models.Domain.MessageResponse;
import radar.radar.Models.Domain.User;

/**
 * Data model for response message with additional detail
 */
public class MessageResponseWithDetails extends MessageResponse {
    public User userDetails;

    /**
     * Constructor
     */
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
