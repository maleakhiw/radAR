package radar.radar.Models.Responses;

/**
 * Created by kenneth on 17/9/17.
 */

public class StatusError {
    public String reason;
    public int errorCode;

    public StatusError(String reason, int errorCode) {
        this.reason = reason;
        this.errorCode = errorCode;
    }
}