package radar.radar.Models.Requests;

/**
 * Data model for signup request
 */
public class SignUpRequest {
    String firstName;
    String lastName;
    String email;
    String username;
    String profileDesc;
    String password;
    String deviceID;

    /**
     * Constructor
     */
    public SignUpRequest(String firstName, String lastName, String email, String username, String profileDesc, String password, String deviceID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.profileDesc = profileDesc;
        this.password = password;
        this.deviceID = deviceID;
    }
}
