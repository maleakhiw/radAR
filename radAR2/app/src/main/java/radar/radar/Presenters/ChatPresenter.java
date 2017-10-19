package radar.radar.Presenters;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatView;

/**
 * Application logic for chat (Presenter part of the MVP model)
 */
public class ChatPresenter {
    private ChatView chatView;
    private ChatService chatService;
    Disposable loadMessagesDisposable;
    private Integer lastGroupID;

    /**
     * Constructor for ChatPresenter
     * @param chatView ChatActivityooo
     * @param chatService instance of ChatService object
     */
    public ChatPresenter(ChatView chatView, ChatService chatService) {
        this.chatView = chatView;
        this.chatService = chatService;
    }

    /**
     * Method that are used to determine whether we load messages or create new chat
     */
    public void determineMessageCreation() {
        // If there exist the message, just load the message
        if (chatView.getLoad()) {
            Group chat = chatView.getChatFromIntent();
            chatView.setGroupID(chat.groupID);
            loadMessages(chat.groupID);
            lastGroupID = chat.groupID;
            chatView.embedSendMessage();
        } else {
            generateNewChat();
            chatView.embedSendMessage();
        }
    }


    Observable<MessagesResponse> responseObservable;

    /**
     * Used to load messages
     * @param chatID id of the chat to load the messages
     */
    public void loadMessages(int chatID) {
        responseObservable = chatService.getMessages(chatID, 2000);
        responseObservable.subscribe(new Observer<MessagesResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadMessagesDisposable = d;
            }

            @Override
            public void onNext(MessagesResponse messagesResponse) {
                // If successful display on recycler view
                System.out.println("got message response");
                if (messagesResponse.success) {
                    chatView.processRecyclerView(messagesResponse);
                }
                else {
                    chatView.showToast("Failure to load messages.");
                }
            }

            @Override
            public void onError(Throwable e) {
                chatView.showToast("Internal Error. Failed to load messages.");

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * Create new chat request object
     * @param id1 user id 1
     * @param id2 user id 2
     * @param name name of the group/ chat
     * @return new chat request object
     */
    @Deprecated
    public NewChatRequest generateNewChatRequest(int id1, int id2, String name) {
        ArrayList<Integer> participant = new ArrayList<>();
        participant.add(id1);
        participant.add(id2);

        NewChatRequest object = new NewChatRequest(name, participant);
        return (object);
    }

    public void onStop() {
        if (loadMessagesDisposable != null) {
            System.out.println("disposing load messages disposable");
            loadMessagesDisposable.dispose();
            System.out.println(loadMessagesDisposable.isDisposed());
//            responseObservable = null;
            chatService.stopPollingMessages();
        }
    }

    public void onStart() {
        if (lastGroupID != null) {
            loadMessages(lastGroupID);
        }
    }



    /**
     * Used to generate a new chat for a particular user
     * DEPRECATED - use the new API route for this purpose.
     */
    @Deprecated
    public void generateNewChat() {
        // Create an object for new chat request which includes the participant of the chat
        // and also the name of the chat
        String name = chatView.getUsername(); // name of the chat is the username
        NewChatRequest newChatRequest = generateNewChatRequest(chatView.getUserID(), chatView.getCurrentUserID(), name);

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
                    chatView.setActivityTitle(newChatResponse.group.name);
                }
                else {
                    chatView.showToast("Failed to create new chat");
                }
            }

            @Override
            public void onError(Throwable e) {
                chatView.showToast("Internal error. Failed to generate new chat.");
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
