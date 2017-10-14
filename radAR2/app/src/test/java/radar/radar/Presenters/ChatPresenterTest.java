package radar.radar.Presenters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Domain.Group;
import radar.radar.Services.ChatService;
import radar.radar.Views.ChatView;

import static org.junit.Assert.*;

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

    @Test
    public void loadMessages() throws Exception {
    }

    @Test
    public void generateNewChatRequest() throws Exception {
    }

    @Test
    public void generateNewChat() throws Exception {
    }

}