package radar.radar.Presenters;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;

/**
 * Presenter class of ChatListActivity.
 * Contains application logic related to displaying chat list
 */

// TODO @maleakhiw class has been refactored - rewrite unit tests

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

//    Disposable getChatListDisposable;

    public void getGroups() {
        chatListView.startRefreshIndicator();
        // Getting the chat id that are related to a particular user
       chatService.getChats().subscribe(new Observer<GetChatsResponse>() {
           @Override
           public void onSubscribe(Disposable d) {

           }

           @Override
           public void onNext(GetChatsResponse getChatsResponse) {
               System.out.println("got response");
               if (getChatsResponse.success) {
                   chatListView.setGroups(getChatsResponse.groups);
               }
               chatListView.stopRefreshIndicator();
           }

           @Override
           public void onError(Throwable e) {
               System.out.println(e);
               chatListView.stopRefreshIndicator();
           }

           @Override
           public void onComplete() {

           }
       });
    }

    public void deleteGroup(int groupID) {
        chatService.deleteGroup(groupID).subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                if (status.success) {
                    chatListView.showToastMessage("Group deleted");
                    chatListView.removeGroup(groupID);
                }
                getGroups();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void onStop() {
//        if (getChatListDisposable != null) {
//            getChatListDisposable.dispose();
//        }
    }
}
