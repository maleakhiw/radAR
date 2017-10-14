package radar.radar.Presenters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;

/**
 * Unit testing class to check application logic of ChatList Presenter
 */
public class ChatListPresenterTest {
    @BeforeClass
    public static void setupClass() {
        // set all schedulers to trampoline scheduler - to run on the "main thread"
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                __ -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());

    }

    /**
     * When successfully getting chat ids, the method should call displayChatList()
     * @throws Exception
     */
    @Test
    public void getChatIDs_Success() throws Exception {
        // Mock necessary object
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create spied presenter
        ChatListPresenter presenterSpied = new ChatListPresenter(chatListView, chatService);
        ChatListPresenter presenter = Mockito.spy(presenterSpied);

        // Create Observable that will be return
        int group1 = 1;
        ArrayList<Integer> groups = new ArrayList<>();
        groups.add(group1);
        GetChatsResponse getChatsResponse = new GetChatsResponse(groups, true);
        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse);

        // Define the behaviour of getChats
        Mockito.when(chatService.getChats()).thenReturn(observable);

        // Call the method that will be tested
        presenter.getChatIDs();

        // Verify the method is called
        Mockito.verify(chatListView).setChatIDs(groups);
        Mockito.verify(presenter).displayChatList();
    }

    /**
     * When status false, notify error message to user
     * @throws Exception
     */
    @Test
    public void getChatIDs_StatusFalse() throws Exception {
        // Mock necessary object
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create spied presenter
        ChatListPresenter presenterSpied = new ChatListPresenter(chatListView, chatService);
        ChatListPresenter presenter = Mockito.spy(presenterSpied);

        // Create Observable that will be return
        int group1 = 1;
        ArrayList<Integer> groups = new ArrayList<>();
        groups.add(group1);
        GetChatsResponse getChatsResponse = new GetChatsResponse(groups, false);
        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse);

        // Define the behaviour of getChats
        Mockito.when(chatService.getChats()).thenReturn(observable);

        // Call the method that will be tested
        presenter.getChatIDs();

        // Verify to display error message to user
        Mockito.verify(chatListView).showToastMessage(anyString());
    }

    /**
     * When there is error, notify error message to user
     * @throws Exception
     */
    @Test
    public void getChatIDs_Failure() throws Exception {
        // Mock necessary object
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create spied presenter
        ChatListPresenter presenterSpied = new ChatListPresenter(chatListView, chatService);
        ChatListPresenter presenter = Mockito.spy(presenterSpied);

        // Create observable that will return error message
        int group1 = 1;
        ArrayList<Integer> groups = new ArrayList<>();
        groups.add(group1);
        GetChatsResponse getChatsResponse = new GetChatsResponse(groups, false);
        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse)
                .map(chatResponse1 -> {
                    throw new SocketTimeoutException("Fake timeout exception");
                });

        // Define the behaviour of getChats
        Mockito.when(chatService.getChats()).thenReturn(observable);

        // Call the method that will be tested
        presenter.getChatIDs();

        // Verify to display error message to user
        Mockito.verify(chatListView).showToastMessage(anyString());
    }

}