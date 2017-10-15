package radar.radar.Models.Domain;

import android.location.Location;

import radar.radar.Models.Responses.GroupLocationsInfo;

/**
 * Created by kenneth on 28/9/17.
 */

public class LocationAndDeviceData {
    public float azimuth;
    public float pitch;
    public Location location;
    public GroupLocationsInfo groupLocationDetails;

    public LocationAndDeviceData(float azimuth, float pitch, Location location, GroupLocationsInfo groupLocationDetails) {
        this.pitch = pitch;
        this.azimuth = azimuth;
        this.location = location;
        this.groupLocationDetails = groupLocationDetails;
    }

    @Override
    public String toString() {
        return ((Float) azimuth).toString() + ", " + ((Float) pitch).toString() + ", " + location.toString();
    }
}
