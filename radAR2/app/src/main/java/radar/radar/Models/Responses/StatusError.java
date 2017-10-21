package radar.radar.Models.Responses;

/**
 * Another data model for status representing error on response
 */
public class StatusError {
    public String reason;
    public int errorCode;

    /**
     * Constructor
     */
    public StatusError(String reason, int errorCode) {
        this.reason = reason;
        this.errorCode = errorCode;
    }
}