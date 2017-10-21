package radar.radar.Presenters;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
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

public class ChatListPresenter {
    ChatListView chatListView;
    ChatService chatService;

    boolean isUpdating = false;

    /**
     * Constructor
     * @param chatListView ChatListActivity
     * @param  chatService service that are instantiated on ChatListActivity
     */
    public ChatListPresenter(ChatListView chatListView, ChatService chatService) {
        this.chatListView = chatListView;
        this.chatService = chatService;
    }

    public void startUpdates() {
        isUpdating = true;
        Observable.interval(5, TimeUnit.SECONDS)
                .takeWhile(aLong -> isUpdating)
                .subscribe(tick -> {
                    System.out.println(tick);
                    loadData(false);
                });
    }

    public void stopUpdates() {
        System.out.println("stopUpdates");
        isUpdating = false;
    }

    public void loadData(boolean showRefresh) {
        System.out.println("loadData");
        if (showRefresh) {
            chatListView.startRefreshIndicator();
        }
        // Getting the chat id that are related to a particular user
       chatService.getChats().subscribe(new Observer<GetChatsResponse>() {
           @Override
           public void onSubscribe(Disposable d) {

           }

           @Override
           public void onNext(GetChatsResponse getChatsResponse) {
               if (getChatsResponse.success) {
                   chatListView.setGroups(getChatsResponse.groups);
               }
               else {
                   chatListView.showToastMessage("Failure to load chat list.");
               }
               if (showRefresh) {
                   chatListView.stopRefreshIndicator();
               }
           }

           @Override
           public void onError(Throwable e) {
               System.out.println(e);
               chatListView.showToastMessage("Internal Error. Failure to load chat list.");
               if (showRefresh) {
                   chatListView.stopRefreshIndicator();
               }
           }

           @Override
           public void onComplete() {

           }
       });
    }

    public void deleteGroup(int groupID) {
        chatService.leaveGroup(groupID).subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                if (status.success) {
                    chatListView.showToastMessage("Group deleted");
                    chatListView.removeGroup(groupID);
                }
                else {
                    chatListView.showToastMessage("Failure to delete group.");
                }
                loadData(true);
            }

            @Override
            public void onError(Throwable e) {
                chatListView.showToastMessage("Internal Error. Failure to delete group.");
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void onStop() {
    }

}
