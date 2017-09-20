package radar.radar.Models.Responses;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.User;

/**
 * Created by kenneth on 20/9/17.
 */

public class OnlineStatusesResponse extends Status {
    ArrayList<User> userInfos;
    HashMap<Integer, Boolean> onlineStatus;
}
