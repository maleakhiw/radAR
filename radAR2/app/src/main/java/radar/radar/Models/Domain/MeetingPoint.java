package radar.radar.Models.Domain;

import java.util.Date;

/**
 * Created by kenneth on 3/10/17.
 */

public class MeetingPoint {
    public double lat;
    public double lon;
    public String name;
    public String description;
    public Date timeAdded;
    public int updatedBy;

    public MeetingPoint(double lat, double lon, String name, String description) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
    }
}
