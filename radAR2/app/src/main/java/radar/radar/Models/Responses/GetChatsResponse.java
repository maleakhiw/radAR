package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Created by kenneth on 20/9/17.
 */

public class GetChatsResponse extends Status {
    public ArrayList<Integer> groups;

    public GetChatsResponse(ArrayList<Integer> groups, boolean success) {
        this.groups = groups;
        this.success = success;
    }
}
