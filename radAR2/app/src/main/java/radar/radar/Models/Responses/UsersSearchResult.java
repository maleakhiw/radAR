package radar.radar.Models.Responses;

import java.util.ArrayList;

import radar.radar.Models.Domain.User;

/**
 * Data model for the response that display search result for users
 */
public class UsersSearchResult extends Status {
    public ArrayList<User> results;

    /**
     * Constructor for UsersSearchResult
     * @param results list of users
     */
    public UsersSearchResult(ArrayList<User> results) {
        this.results = results;
    }
}
