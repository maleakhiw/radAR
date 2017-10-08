package radar.radar.Presenters;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.ChatActivity;
import radar.radar.Models.Group;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Services.AuthService;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatView;

/**
 * Created by keyst on 3/10/2017.
 */

public class ChatPresenter {
    private ChatView chatView;
    private ChatService chatService;

    /** Constructor */
    public ChatPresenter(ChatView chatView, ChatService chatService) {
        this.chatView = chatView;
        this.chatService = chatService;
    }

    /** Method that are used to determine whether we load messages or create new chat */
    public void determineMessageCreation() {
        // If there exist the message, just load the message
        if (chatView.getLoad()) {
            Group chat = chatView.getChatFromIntent();
            chatView.setGroupID(chat.groupID);
            loadMessages(chat.groupID);
            chatView.embedSendMessage();
        } else {
            generateNewChat();
            chatView.embedSendMessage();
        }
    }

    /** Used to get messages */
    public void loadMessages(int chatID) {
        chatService.getMessages(chatID).subscribe(new Observer<MessagesResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MessagesResponse messagesResponse) {
                // If successful display on recycler view
                chatView.setMessages(messagesResponse.messages);
                chatView.getMessageListAdapter().setMessageList(chatView.getMessages());
                chatView.getMessageListAdapter().notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /** Create new chat request object */
    public NewChatRequest generateNewChatRequest(int id1, int id2, String name) {
        ArrayList<Integer> participant = new ArrayList<>();
        participant.add(id1);
        participant.add(id2);

        return (new NewChatRequest(participant, name));
    }

    /** Used to generate a new chat for a particular user */
    public void generateNewChat() {
        // Create an object for new chat request which includes the participant of the chat
        // and also the name of the chat
        String name = chatView.getUser().username; // name of the chat is the username
        NewChatRequest newChatRequest = generateNewChatRequest(chatView.getUser().userID, AuthService.getUserID(chatView.getChatContext()), name);

        chatService.newChat(newChatRequest).subscribe(new Observer<NewChatResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NewChatResponse newChatResponse) {
                // if the response is successful, then we can proceed to create a chat
                if (newChatResponse.success) {
                    // new chat created
                    chatView.setGroupID(newChatResponse.group.groupID);
                }
                else {
                    chatView.showToast("Error to create new chat");
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        });
    }

}