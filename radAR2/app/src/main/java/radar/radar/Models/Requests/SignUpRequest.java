package radar.radar.Models.Requests;


public class SignUpRequest {
    String firstName;
    String lastName;
    String email;
    String username;
    String profileDesc;
    String password;
    String deviceID;

    public SignUpRequest(String firstName, String lastName, String email, String username, String profileDesc, String password, String deviceID) {
        // TODO null checks, throw exception
        // TODO device ID for push notifications

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.profileDesc = profileDesc;
        this.password = password;
        this.deviceID = deviceID;
    }
}
