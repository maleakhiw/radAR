package radar.radar.Models.Requests;


import java.util.ArrayList;

import radar.radar.Models.Domain.MeetingPoint;

public class NewGroupBody {
    String name;
    ArrayList<Integer> participantUserIDs;
    MeetingPoint meetingPoint;

    public NewGroupBody(String name, ArrayList<Integer> participantUserIDs) {
        this.name = name;
        this.participantUserIDs = participantUserIDs;
    }

    public NewGroupBody(String name, ArrayList<Integer> participantUserIDs, MeetingPoint meetingPoint) {
        this.name = name;
        this.participantUserIDs = participantUserIDs;
        this.meetingPoint = meetingPoint;
    }
}
