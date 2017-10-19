package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.User;

/**
 * Data model as a response when getting online status of users
 */
public class OnlineStatusesResponse extends Status {
    ArrayList<User> userInfos;
    HashMap<Integer, Boolean> onlineStatus;
}
