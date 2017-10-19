package radar.radar.Models.Responses;

public class StatusError {
    public String reason;
    public int errorCode;

    public StatusError(String reason, int errorCode) {
        this.reason = reason;
        this.errorCode = errorCode;
    }
}