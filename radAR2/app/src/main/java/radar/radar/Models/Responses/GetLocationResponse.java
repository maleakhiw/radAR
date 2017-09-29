package radar.radar.Models.Responses;


import java.util.Date;

public class GetLocationResponse extends Status {
    float lat;
    float lon;
    float heading;
    float accuracy;
    Date timeUpdated;

}
