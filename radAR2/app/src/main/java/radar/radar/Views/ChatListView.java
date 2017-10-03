package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Group;

/**
 * Created by keyst on 3/10/2017.
 */

public interface ChatListView {
    void setGroups(ArrayList<Group> groups);

    void setChatIDs(ArrayList<Integer> chatIDs);

    ArrayList<Integer> getChatIDs();

    ArrayList<Group> getGroups();

    void showToastMessage(String message);

    void setArrayListInAdapter(ArrayList<Group> groups);

    void notifyAdapterChange();
}
