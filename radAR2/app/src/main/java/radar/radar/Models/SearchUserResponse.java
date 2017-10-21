package radar.radar.Models;

import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.Status;

/**
 * Data model response when searching user
 */
public class SearchUserResponse extends Status {
    public User details;
}
