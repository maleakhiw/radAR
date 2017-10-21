package radar.radar.Models.Domain;

import android.location.Location;

import radar.radar.Models.Responses.GroupLocationsInfo;

public class LocationAndDeviceData {
    public double azimuth;
    public double pitch;
    public Location location;
    public GroupLocationsInfo groupLocationDetails;

    public LocationAndDeviceData(double azimuth, double pitch, Location location, GroupLocationsInfo groupLocationDetails) {
        this.pitch = pitch;
        this.azimuth = azimuth;
        this.location = location;
        this.groupLocationDetails = groupLocationDetails;
    }


    @Override
    public String toString() {
        return ((Double) azimuth).toString() + ", " + ((Double) pitch).toString() + ", " + location.toString();
    }
}
