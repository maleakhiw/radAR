package radar.radar.Models.Responses;

import java.util.Date;

/**
 * Data model response for location
 */
public class GetLocationResponse extends Status {
    public float lat;
    public float lon;
    public float heading;
    public float accuracy;
    public Date timeUpdated;

}
