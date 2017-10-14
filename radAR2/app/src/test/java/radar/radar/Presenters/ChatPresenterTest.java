package radar.radar.Presenters;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Services.AuthService;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

/**
 * Used to unit test the main Chat functionality in Chat Presenter
 */
public class ChatPresenterTest {
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
     * Used to test determine message creation when a chat is already exist,
     * make sure that the method load existing chat instead of creating new one
     * @throws Exception
     */
    @Test
    public void determineMessageCreation_Load() throws Exception {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Behaviour of the mocking object
        Mockito.when(chatView.getLoad()).thenReturn(true); // indicate to load
        Group chat = Mockito.mock(Group.class);
        chat.groupID = 1;
        Mockito.when(chatView.getChatFromIntent()).thenReturn(chat);

        // Create presenter to be spied
        ChatPresenter presenterBeforeSpied = new ChatPresenter(chatView, chatService);
        ChatPresenter presenter = Mockito.spy(presenterBeforeSpied);

        // Make sure that load does nothing
        Mockito.doNothing().when(presenter).loadMessages(1);

        // Call the method
        presenter.determineMessageCreation();

        // Verify that load messages is called
        Mockito.verify(presenter).loadMessages(1);
        Mockito.verify(chatView).embedSendMessage();
    }

    /**
     * Used to test determine message creation when a chat haven't existed.
     * It will generate new chat
     * @throws Exception
     */
    @Test
    public void determineMessageCreation_New() throws Exception {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Behaviour of the mocking object
        Mockito.when(chatView.getLoad()).thenReturn(false); // indicate not to load

        // Create presenter to be spied
        ChatPresenter presenterBeforeSpied = new ChatPresenter(chatView, chatService);
        ChatPresenter presenter = Mockito.spy(presenterBeforeSpied);

        // Make sure that load does nothing
        Mockito.doNothing().when(presenter).generateNewChat();

        // Call the method
        presenter.determineMessageCreation();

        // Verify that new chat is created
        Mockito.verify(presenter).generateNewChat();
        Mockito.verify(chatView).embedSendMessage();
    }

    /**
     * Used to check if message is really loaded when success
     * @throws Exception
     */
    @Test
    public void loadMessages_Success() throws Exception {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create observable
        MessagesResponse messagesResponse = Mockito.mock(MessagesResponse.class);
        messagesResponse.success = true;
        Observable<MessagesResponse> observable = Observable.just(messagesResponse);

        Mockito.when(chatService.getMessages(1, 2000)).thenReturn(observable);

        // Create presenter
        ChatPresenter presenter = new ChatPresenter(chatView, chatService);
        presenter.loadMessages(1);

        // Verify
        Mockito.verify(chatView).processRecyclerView(messagesResponse);
    }


    /**
     * Used to check if error message is displayed when status is false
     * @throws Exception
     */
    @Test
    public void loadMessages_StatusFalse() throws Exception {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create observable
        MessagesResponse messagesResponse = Mockito.mock(MessagesResponse.class);
        messagesResponse.success = false;
        Observable<MessagesResponse> observable = Observable.just(messagesResponse);

        Mockito.when(chatService.getMessages(1, 2000)).thenReturn(observable);

        // Create presenter
        ChatPresenter presenter = new ChatPresenter(chatView, chatService);
        presenter.loadMessages(1);

        // Verify
        Mockito.verify(chatView).showToast(anyString());
    }

    /**
     * Used to check if error message is displayed when error i.e. connection is occurred
     * @throws Exception
     */
    @Test
    public void loadMessages_Failure() throws Exception {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Create observable
        MessagesResponse messagesResponse = Mockito.mock(MessagesResponse.class);
        Observable<MessagesResponse> observable = Observable.just(messagesResponse)
                .map(messagesResponse1 -> {
                    throw new SocketTimeoutException("Fake timeout exception");
                });

        Mockito.when(chatService.getMessages(1, 2000)).thenReturn(observable);

        // Create presenter
        ChatPresenter presenter = new ChatPresenter(chatView, chatService);
        presenter.loadMessages(1);

        // Verify
        Mockito.verify(chatView).showToast(anyString());
    }

    /**
     * Used to check if the method really generate new chat when successful
     */
    @Test
    public void generateNewChat_Success() {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Define behaviour
        Mockito.when(chatView.getUsername()).thenReturn("maleakhiw");
        Mockito.when(chatView.getUserID()).thenReturn(1);
        Mockito.when(chatView.getCurrentUserID()).thenReturn(2);

        // Create presenter object
        ChatPresenter chatPresenter = new ChatPresenter(chatView, chatService);
        ChatPresenter presenter = Mockito.spy(chatPresenter);

        // Create observable and necessary dependency
        NewChatRequest newChatRequest = Mockito.mock(NewChatRequest.class);
        Mockito.when(presenter.generateNewChatRequest(1,2, "maleakhiw")).thenReturn(newChatRequest);
        NewChatResponse newChatResponse = Mockito.mock(NewChatResponse.class);
        Group group = Mockito.mock(Group.class);
        newChatResponse.group = group;
        group.groupID = 10;
        newChatResponse.success = true;
        Observable<NewChatResponse> observable = Observable.just(newChatResponse);

        // Return that observable
        Mockito.when(chatService.newChat(newChatRequest)).thenReturn(observable);

        // Call the method
        presenter.generateNewChat();

        // Verify
        Mockito.verify(chatView).setGroupID(10);
    }

    /**
     * Used to check if the method return error if status false
     */
    @Test
    public void generateNewChat_StatusFalse() {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Define behaviour
        Mockito.when(chatView.getUsername()).thenReturn("maleakhiw");
        Mockito.when(chatView.getUserID()).thenReturn(1);
        Mockito.when(chatView.getCurrentUserID()).thenReturn(2);

        // Create presenter object
        ChatPresenter chatPresenter = new ChatPresenter(chatView, chatService);
        ChatPresenter presenter = Mockito.spy(chatPresenter);

        // Create observable and necessary dependency
        NewChatRequest newChatRequest = Mockito.mock(NewChatRequest.class);
        Mockito.when(presenter.generateNewChatRequest(1,2, "maleakhiw")).thenReturn(newChatRequest);
        NewChatResponse newChatResponse = Mockito.mock(NewChatResponse.class);
        Group group = Mockito.mock(Group.class);
        newChatResponse.group = group;
        group.groupID = 10;
        newChatResponse.success = false;
        Observable<NewChatResponse> observable = Observable.just(newChatResponse);

        // Return that observable
        Mockito.when(chatService.newChat(newChatRequest)).thenReturn(observable);

        // Call the method
        presenter.generateNewChat();

        // Verify
        Mockito.verify(chatView).showToast(anyString());
    }

    /**
     * Used to check if the method return error if there is on error
     */
    @Test
    public void generateNewChat_Failure() {
        // Mock necessary object
        ChatView chatView = Mockito.mock(ChatView.class);
        ChatService chatService = Mockito.mock(ChatService.class);

        // Define behaviour
        Mockito.when(chatView.getUsername()).thenReturn("maleakhiw");
        Mockito.when(chatView.getUserID()).thenReturn(1);
        Mockito.when(chatView.getCurrentUserID()).thenReturn(2);

        // Create presenter object
        ChatPresenter chatPresenter = new ChatPresenter(chatView, chatService);
        ChatPresenter presenter = Mockito.spy(chatPresenter);

        // Create observable and necessary dependency
        NewChatRequest newChatRequest = Mockito.mock(NewChatRequest.class);
        Mockito.when(presenter.generateNewChatRequest(1,2, "maleakhiw")).thenReturn(newChatRequest);
        NewChatResponse newChatResponse = Mockito.mock(NewChatResponse.class);
        Group group = Mockito.mock(Group.class);
        newChatResponse.group = group;
        group.groupID = 10;
        newChatResponse.success = false;
        Observable<NewChatResponse> observable = Observable.just(newChatResponse)
                .map(newChatResponse1 -> {
                    throw new TimeoutException("error");
                });

        // Return that observable
        Mockito.when(chatService.newChat(newChatRequest)).thenReturn(observable);

        // Call the method
        presenter.generateNewChat();

        // Verify that error message is given if there is error
        Mockito.verify(chatView).showToast(anyString());
    }

}