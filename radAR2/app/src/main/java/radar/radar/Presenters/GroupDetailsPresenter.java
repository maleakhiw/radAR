package radar.radar.Presenters;

import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Services.LocationService;
import radar.radar.Views.GroupDetailView;


public class GroupDetailsPresenter {

    private GroupDetailView view;
    private Group group;
    private LocationService locationService;

    public GroupDetailsPresenter(GroupDetailView view, Group group, LocationService locationService) {
        this.view = view;
        this.group = group;
        this.locationService = locationService;

        if (group.meetingPoint != null) {
            updateMeetingPoint(group.meetingPoint);
        }
    }

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
