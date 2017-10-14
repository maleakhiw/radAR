package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.GetChatInfoResponse;

/**
 * Interface for ChatListActivity that supports MVP model.
 */
public interface ChatListView {
    void setGroups(ArrayList<Group> groups);

    void setChatIDs(ArrayList<Integer> chatIDs);

    ArrayList<Integer> getChatIDs();

    ArrayList<Group> getGroups();

    void showToastMessage(String message);

    int getChatIDsSize();

    int getChatId(int index);

    void processDisplayChatList(GetChatInfoResponse getChatInfoResponse);
}
