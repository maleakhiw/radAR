package radar.radar.Presenters;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;

/**
 * Presenter class of ChatListActivity.
 * Contains application logic related to displaying chat list
 */
public class ChatListPresenter {
    ChatListView chatListView;
    ChatService chatService;

    /**
     * Constructor
     * @param chatListView ChatListActivity
     * @param  chatService service that are instantiated on ChatListActivity
     */
    public ChatListPresenter(ChatListView chatListView, ChatService chatService) {
        this.chatListView = chatListView;
        this.chatService = chatService;
    }

    /**
     * Get chat ids that are related to a particular user
     */
    public void getChats() {
        // Getting the chat id that are related to a particular user
        chatService.getChats().subscribe(new Observer<GetChatsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GetChatsResponse getChatsResponse) {
                // If we successfully get chat ids
                if (getChatsResponse.success) {
                    chatListView.setChatIDs(getChatsResponse.groups);
                    displayChatList();
                }
                else {
                    chatListView.showToastMessage("Unsuccessful getting chatIDs.");
                }
            }

            @Override
            public void onError(Throwable e) {
                chatListView.showToastMessage("Internal error. Failed to get chat ids.");
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * Used to display chat list (getting the chat information)
     */
    public void displayChatList() {
        // Using the id that we have get display the chat
        // Iterate through all ids
        for (int i=0; i<chatListView.getChatIDsSize(); i++) {
            chatService.getChatInfo(chatListView.getChatId(i)).subscribe(new Observer<GetChatInfoResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(GetChatInfoResponse getChatInfoResponse) {
                    // If the response successful display on the recycler view
                    if (getChatInfoResponse.success) {
                        chatListView.processDisplayChatList(getChatInfoResponse);
                        chatListView.stopRefreshIndicator();
                    }
                    else {
                        chatListView.showToastMessage("Failed to display chat information.");
                    }

                }

                @Override
                public void onError(Throwable e) {
                    chatListView.showToastMessage("Internal Error. Failed to display chat list.");

                }

                @Override
                public void onComplete() {
                }
            });
        }
    }

}
