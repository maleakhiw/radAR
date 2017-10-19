package radar.radar.Models.Responses;

import radar.radar.Models.Domain.MessageResponse;

/**
 * Data model of response that will be got after sending message
 */
public class SendMessageResponse extends Status {
   public MessageResponse sentMessage;
}
