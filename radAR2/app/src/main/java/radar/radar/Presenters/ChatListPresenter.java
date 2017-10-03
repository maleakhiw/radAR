package radar.radar.Presenters;

import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.ChatListActivity;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;

/**
 * Created by keyst on 3/10/2017.
 */

public class ChatListPresenter {
    ChatListView chatListView;
    ChatService chatService;

    /** Constructor */
    public ChatListPresenter(ChatListView chatListView, ChatService chatService) {
        this.chatListView = chatListView;
        this.chatService = chatService;
    }

    public void getChatIDs() {
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
                    chatListView.showToastMessage("Unsuccessful getting chatIDs");
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

    /** Method that are used to display chat list */
    public void displayChatList() {
        // Using the id that we have get display the chat
        // Iterate through all ids
        for (int i=0; i < chatIDs.size(); i++) {
            chatService.getChatInfo(chatIDs.get(i)).subscribe(new Observer<GetChatInfoResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(GetChatInfoResponse getChatInfoResponse) {
                    // If the response successful display on the recycler view
                    if (getChatInfoResponse.success) {
                        // Add to groups
                        groups.add(getChatInfoResponse.group);
                        chatAdapter.setChatList(groups);
                        chatAdapter.notifyDataSetChanged();
                    }
                    else {
                        chatListView.showToastMessage("Failed to display chat information.");
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

}
