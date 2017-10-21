package radar.radar.Models.Android;

import radar.radar.Models.Domain.User;

/**
 * Data model for adding member to the group
 */
public class UserWithCheckbox extends User {
    public boolean isChecked = false;

    /**
     * Constructor
     */
    public UserWithCheckbox(int userID, String username, String firstName, String lastName, String profileDesc, String email) {
        super(userID, username, firstName, lastName, profileDesc, email);
    }

    public UserWithCheckbox(User user, boolean isChecked) {
        super(user.userID, user.username, user.firstName, user.lastName, user.profileDesc, user.email);
        this.isChecked = isChecked;
    }
}
