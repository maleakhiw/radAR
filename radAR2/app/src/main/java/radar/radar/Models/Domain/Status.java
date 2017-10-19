package radar.radar.Models.Domain;

import java.util.ArrayList;

/**
 * Class representing data model for status
 */
class StatusError {
    String reason;
    int errorCode;
}

public class Status {
    boolean success;
    ArrayList<StatusError> errors;

    /**
     * Constructor for status
     * @param success success value true/false
     */
    public Status(boolean success) {
        this.success = success;
    }
}
