package radar.radar.Models.Requests;

/**
 * Data model for location
 */
public class UpdateLocationRequest {
    double lat;
    double lon;
    double accuracy;
    double heading;

    /**
     * Constructor
     */
    public UpdateLocationRequest(double lat, double lon, double accuracy, double heading) {
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.heading = heading;
    }
}
