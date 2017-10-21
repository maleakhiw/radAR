package radar.radar.Models.Domain;

import java.util.Date;

import radar.radar.Models.Responses.GetLocationResponse;

/**
 * Data model for destination
 */
public class DestinationLocation extends UserLocation {
    public String name;

    /**
     * Constructor
     */
    public DestinationLocation(int userID, GetLocationResponse response) {
        super(userID, response);
    }

    public DestinationLocation(int userID, float lat, float lon, float accuracy, float heading, Date timeUpdated, String name) {
        super(userID, lat, lon, accuracy, heading, timeUpdated);
        this.name = name;
    }
}
