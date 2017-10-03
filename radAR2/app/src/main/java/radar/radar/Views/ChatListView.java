package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Chat;

/**
 * Created by keyst on 3/10/2017.
 */

public interface ChatListView {
    void setGroups(ArrayList<Chat> groups);

    void setChatIDs(ArrayList<Integer> chatIDs);

    void showToastMessage(String message);
}
