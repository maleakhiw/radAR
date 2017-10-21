package radar.radar.Models.Domain;

import java.util.Date;

import radar.radar.Models.Responses.GetLocationResponse;



public class UserLocation extends GetLocationResponse {

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getHeading() {
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public Date getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public UserLocation(int userID, GetLocationResponse response) {
        this.lat = response.lat;
        this.lon = response.lon;
        this.accuracy = response.accuracy;
        this.heading = response.heading;
        this.timeUpdated = response.timeUpdated;
        this.userID = userID;
    }

    int userID;

    public UserLocation(int userID, float lat, float lon, float accuracy, float heading, Date timeUpdated) {
        this.userID = userID;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.heading = heading;
        this.timeUpdated = timeUpdated;
    }
}
