package radar.radar.Views;

import android.content.Context;

import java.util.ArrayList;

import radar.radar.Adapters.MessageListAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MessageResponse;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.MessagesResponse;

/**
 * Interface for ChatActivity
 */
public interface ChatView {
    void setLoad(Boolean load);

    Boolean getLoad();

    Group getChatFromIntent();

    void setGroupID(int groupID);

    int getGroupID();

    void setMessages(ArrayList<MessageResponse> messages);

    ArrayList<MessageResponse> getMessages();

    MessageListAdapter getMessageListAdapter();

    User getUser();

    String getUsername();

    int getUserID();

    void setUser(User user);

    void showToast(String message);

    void processRecyclerView(MessagesResponse messagesResponse);

    int getCurrentUserID();

    void embedSendMessage();

    Context getChatContext();
}
