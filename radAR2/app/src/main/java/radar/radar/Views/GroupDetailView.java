package radar.radar.Views;

import radar.radar.Models.Domain.MeetingPoint;

/**
 * Interface for groups to support MVP model
 */
public interface GroupDetailView {
    void setMeetingPoint(MeetingPoint meetingPoint);

    void moveCameraTo(double lat, double lon);

    void dropPinAt(double lat, double lon, String name);

    void setMeetingPointLatLon(double lat, double lon);

    void setMeetingPointName(String name);

    void updateDistanceToMeetingPoint();

    void requestLocationPermissions(int requestCode);
}
