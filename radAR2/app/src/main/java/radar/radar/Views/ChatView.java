package radar.radar.Views;

import android.content.Context;

import java.util.ArrayList;

import radar.radar.Adapters.MessageListAdapter;
import radar.radar.Models.Chat;
import radar.radar.Models.Responses.MessageResponse;
import radar.radar.Models.User;

/**
 * Created by keyst on 3/10/2017.
 */

public interface ChatView {
    void setLoad(Boolean load);

    Boolean getLoad();

    Chat getChatFromIntent();

    void setGroupID(int groupID);

    int getGroupID();

    void setMessages(ArrayList<MessageResponse> messages);

    ArrayList<MessageResponse> getMessages();

    MessageListAdapter getMessageListAdapter();

    User getUser();

    void setUser(User user);

    void showToast(String message);

    void embedSendMessage();

    Context getChatContext();
}
