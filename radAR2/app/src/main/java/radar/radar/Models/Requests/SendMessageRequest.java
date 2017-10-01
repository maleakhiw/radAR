package radar.radar.Models.Requests;

import radar.radar.Models.Status;

/**
 * Created by keyst on 1/10/2017.
 */

public class SendMessageRequest extends Status {
    public int from;
    public int groupID;
    public int time;
    public String contentType;
    public String text;
    public String contentResourceID;
}
