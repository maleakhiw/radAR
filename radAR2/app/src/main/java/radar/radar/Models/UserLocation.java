package radar.radar.Models;

import java.util.Date;

/**
 * Created by kenneth on 28/9/17.
 */

public class UserLocation {
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

    int userID;
    double lat;
    double lon;
    double accuracy;
    double heading;
    Date timeUpdated;

    public UserLocation(int userID, double lat, double lon, double accuracy, double heading, Date timeUpdated) {
        this.userID = userID;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.heading = heading;
        this.timeUpdated = timeUpdated;
    }
}
