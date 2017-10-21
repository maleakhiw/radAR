package radar.radar.Models.Domain;

import java.util.Date;

import radar.radar.Models.Responses.GetLocationResponse;

/**
 * Data model representing user location
 */
public class UserLocation extends GetLocationResponse {
    int userID;

    /**
     * Constructor
     */
    public UserLocation(int userID, GetLocationResponse response) {
        this.lat = response.lat;
        this.lon = response.lon;
        this.accuracy = response.accuracy;
        this.heading = response.heading;
        this.timeUpdated = response.timeUpdated;
        this.userID = userID;
    }

    public UserLocation(int userID, float lat, float lon, float accuracy, float heading, Date timeUpdated) {
        this.userID = userID;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.heading = heading;
        this.timeUpdated = timeUpdated;
    }

    /**
     * Getter and setter
     */
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }
}
