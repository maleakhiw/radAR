package radar.radar.Models.Domain;

import java.util.ArrayList;

/**
 * Created by kenneth on 6/9/17.
 */

class StatusError {
    String reason;
    int errorCode;
}

public class Status {
    boolean success;
    ArrayList<StatusError> errors;
}
