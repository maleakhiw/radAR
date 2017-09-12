package domain.testapplication.models;

import java.util.ArrayList;

/**
 * Created by keyst on 8/09/2017.
 */

public class GetFriendsResponse {
    public boolean success;
    public ArrayList<ErrorObj> errors;
    public ArrayList<Friend> friends;
}
