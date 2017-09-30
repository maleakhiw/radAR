package radar.radar.Models.Requests;


import java.util.ArrayList;

public class NewGroupBody {
    String name;
    ArrayList<Integer> participantUserIDs;

    public NewGroupBody(String name, ArrayList<Integer> participantUserIDs) {
        this.name = name;
        this.participantUserIDs = participantUserIDs;
    }
}
