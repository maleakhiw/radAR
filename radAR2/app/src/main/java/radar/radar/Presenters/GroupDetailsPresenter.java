package radar.radar.Presenters;

import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Services.LocationService;
import radar.radar.Views.GroupDetailView;

/**
 * Group Details application logic
 */
public class GroupDetailsPresenter {
    private GroupDetailView view;
    private Group group;
    private LocationService locationService;

    /**
     * Constructor for GroupDetails
     * @param view instance of the view
     * @param group instance of the current group class
     * @param locationService instance of location service
     */
    public GroupDetailsPresenter(GroupDetailView view, Group group, LocationService locationService) {
        this.view = view;
        this.group = group;
        this.locationService = locationService;

        if (group.meetingPoint != null) {
            updateMeetingPoint(group.meetingPoint);
        }
    }

    /**
     * Update the meeting point to another place
     * @param meetingPoint object indicating place to meet
     */
    public void updateMeetingPoint(MeetingPoint meetingPoint) {
        System.out.println("updateMeetingPoint");
        view.dropPinAt(meetingPoint.lat, meetingPoint.lon, meetingPoint.name);
        view.moveCameraTo(meetingPoint.lat, meetingPoint.lon);
        view.setMeetingPointName(meetingPoint.name);
        view.setMeetingPointLatLon(meetingPoint.lat, meetingPoint.lon);
        view.updateDistanceToMeetingPoint();

        view.setMeetingPoint(meetingPoint);
    }
}
