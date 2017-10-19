package radar.radar.Models.Responses;

import java.util.ArrayList;

/**
 * Data model representing status of a request
 */
public class Status {
    public boolean success;
    public ArrayList<StatusError> errors;

    /**
     * Constructor for Status
     * @param success
     */
    public Status(boolean success) {
        this.success = success;
    }

    public Status() {

    }
}
