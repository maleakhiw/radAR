package radar.radar.Models.Requests;


public class UpdateLocationRequest {
    float lat;
    float lon;
    float accuracy;
    float heading;


    public UpdateLocationRequest(float lat, float lon, float accuracy, float heading) {
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.heading = heading;
    }
}
