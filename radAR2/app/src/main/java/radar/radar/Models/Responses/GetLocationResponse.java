package radar.radar.Models.Responses;


import java.util.Date;

public class GetLocationResponse extends Status {
    public double lat;
    public double lon;
    public double heading;
    public double accuracy;
    public Date timeUpdated;

}
