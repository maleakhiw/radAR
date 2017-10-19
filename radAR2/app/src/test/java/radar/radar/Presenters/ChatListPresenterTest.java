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
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatListView;

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
     * Unit testing to check when data is loaded successfully, it will set group properly
     */
    @Test
    public void loadData_Success() {
        // Mock necessary object
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create groups
        ArrayList<Group> groups = Mockito.mock(ArrayList.class);

        // Create observable returning success
        GetChatsResponse getChatsResponse = Mockito.mock(GetChatsResponse.class);
        getChatsResponse.success = true;
        getChatsResponse.groups = groups;
        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse);

        // Define behaviour of getChats()
        Mockito.when(chatService.getChats()).thenReturn(observable);

        // Create the real presenter object to be tested
        ChatListPresenter presenter = new ChatListPresenter(chatListView, chatService);

        // Call the method
        presenter.loadData();

        // Verify if load data successfully called
        Mockito.verify(chatListView).setGroups(groups);
        Mockito.verify(chatListView).stopRefreshIndicator();
    }

    /**
     * Unit testing to check when error, to display toast to user
     */
    @Test
    public void loadData_StatusFalse() {
        // Mock necessary object
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create groups
        ArrayList<Group> groups = Mockito.mock(ArrayList.class);

        // Create observable returning success
        GetChatsResponse getChatsResponse = Mockito.mock(GetChatsResponse.class);
        getChatsResponse.success = false;
        getChatsResponse.groups = groups;
        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse);

        // Define behaviour of getChats()
        Mockito.when(chatService.getChats()).thenReturn(observable);

        // Create the real presenter object to be tested
        ChatListPresenter presenter = new ChatListPresenter(chatListView, chatService);

        // Call the method
        presenter.loadData();

        // Verify if load data status false, it will give message to user
        Mockito.verify(chatListView).showToastMessage(anyString());
        Mockito.verify(chatListView).stopRefreshIndicator();
    }

    /**
     * Unit testing to check when error, to display toast to user
     */
    @Test
    public void loadData_Failure() {
        // Mock necessary object
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create groups
        ArrayList<Group> groups = Mockito.mock(ArrayList.class);

        // Create observable returning success
        GetChatsResponse getChatsResponse = Mockito.mock(GetChatsResponse.class);
        getChatsResponse.success = false;
        getChatsResponse.groups = groups;
        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse)
                .map(getChatsResponse1 -> {
                    throw new SocketTimeoutException("Exception");
                });

        // Define behaviour of getChats()
        Mockito.when(chatService.getChats()).thenReturn(observable);

        // Create the real presenter object to be tested
        ChatListPresenter presenter = new ChatListPresenter(chatListView, chatService);

        // Call the method
        presenter.loadData();

        // Verify if load data status false, it will give message to user
        Mockito.verify(chatListView).showToastMessage(anyString());
        Mockito.verify(chatListView).stopRefreshIndicator();
    }

    /**
     * Unit testing to check when deleteGroup, it will behave properly by deleting the group
     * and showing message to user
     */
    @Test
    public void deleteGroup_Success() {
        ChatListView chatListView = Mockito.mock(ChatListView.class);
        ChatService chatService = Mockito.mock(ChatService.class);
        int groupID = 1;

        // Create presenter and spy it
        ChatListPresenter presenterToBeSpied = new ChatListPresenter(chatListView, chatService);
        ChatListPresenter presenter = Mockito.spy(presenterToBeSpied);

        // Create Successful observable
        Status status = new Status(true);
        Observable<Status> observable = Observable.just(status);

        // Return that successful observable
        Mockito.when(chatService.deleteGroup(groupID)).thenReturn(observable);

        // Change behaviour of load data
        Mockito.doNothing().when(presenter).loadData();

        // Call the presenter
        presenter.deleteGroup(groupID);

        // Verify
        Mockito.verify(chatListView).showToastMessage(anyString());
        Mockito.verify(chatListView).removeGroup(groupID);
    }
}