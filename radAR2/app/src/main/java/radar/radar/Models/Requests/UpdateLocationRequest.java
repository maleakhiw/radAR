package radar.radar.Models.Requests;


public class UpdateLocationRequest {
    double lat;
    double lon;
    double accuracy;
    double heading;


    public UpdateLocationRequest(double lat, double lon, double accuracy, double heading) {
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.heading = heading;
    }
}
