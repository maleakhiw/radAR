package radar.radar.Models.Domain;

import java.io.Serializable;

/**
 * Created by keyst on 1/10/2017.
 */

public class MessageResponse implements Serializable {
    public int from;
    public int groupID;
    public String time;
    public String contentType;
    public String text;
    public Object contentResourceID;
}
