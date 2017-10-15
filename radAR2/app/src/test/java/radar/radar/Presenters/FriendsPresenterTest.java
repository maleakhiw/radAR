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
import radar.radar.Models.Responses.StatusError;
import radar.radar.Models.Domain.User;
import radar.radar.Services.UsersService;
import radar.radar.Views.FriendsView;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

public class FriendsPresenterTest {
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
     * Unit test to make sure that when the add friend fab is clicked it will send to appropriate
     * activity
     * @throws Exception
     */
    @Test
    public void respondToFABClick() throws Exception {
        // Mock necessary object
        FriendsView friendsView = Mockito.mock(FriendsView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        FriendsPresenter presenter = new FriendsPresenter(friendsView, usersService);
        presenter.respondToFABClick();

        // I want to make sure that feedback is shown to the user in the form
        // of a new Activity being launched.
        Mockito.verify(friendsView).launchSearchFriendsActivity();
    }


    /**
     * Unit test to make sure when user have error in calling the service method will display appropriate
     * message.
     * @throws Exception
     */
    @Test
    public void loadFriends_Failure() throws Exception {
        // Mock necessary object
        UsersService usersService = Mockito.mock(UsersService.class);
        FriendsView friendsView = Mockito.mock(FriendsView.class);

        // define an Observable that simply throws an error.
        FriendsResponse friendsResponse = new FriendsResponse(new ArrayList<>());
        Observable<FriendsResponse> observable = Observable.just(friendsResponse)
                                                .map(friendsResponse1 -> {
                                                    throw new SocketTimeoutException("Fake timeout exception");
                                                });


        Mockito.when(usersService.getFriends()).thenReturn(observable);

        // Test the system
        FriendsPresenter friendsPresenter = new FriendsPresenter(friendsView, usersService);
        friendsPresenter.loadFriends();

        // Make sure that user will get notified that an error has occurred
        Mockito.verify(friendsView).showToast(anyString());

    }

    /**
     * Unit test making sure that loadFriends() will load friends of a particular user\
     * and bind that to appropriate adapter.
     * @throws Exception
     */
    @Test
    public void loadFriends_Success() throws Exception {
        // Mock necessary objects
        UsersService usersService = Mockito.mock(UsersService.class);
        FriendsView friendsView = Mockito.mock(FriendsView.class);

        // Define behaviours
        FriendsResponse friendsResponse = new FriendsResponse(new ArrayList<>());
        friendsResponse.success = true;
        friendsResponse.errors = new ArrayList<>();

        // List of friends for friendsResponse
        ArrayList<User> friendsForFriendsResponse = new ArrayList<>();
        friendsForFriendsResponse.add(new User(1, "user1", "Fake User", "1", "I'm a fake user", "keystorm@rocketmail.com"));
        friendsForFriendsResponse.add(new User(2, "user2", "Fake User", "2", "I'm a fake user too", "dragonica@gmail.com"));
        friendsResponse.friends = friendsForFriendsResponse;

        // Model the behaviour of the users service by pretending that it will give successful request
        Mockito.when(usersService.getFriends()).thenReturn(
                Observable.just(friendsResponse)
        );

        // Test the appropriate method
        FriendsPresenter friendsPresenter = new FriendsPresenter(friendsView, usersService);
        friendsPresenter.loadFriends();

        // Make sure that it display the data and bind that to recycler view
        Mockito.verify(friendsView).bindAdapterToRecyclerView(friendsForFriendsResponse);
    }

    /**
     * Unit test making making sure toast is displayed when status of the request is false
     * @throws Exception
     */
    @Test
    public void loadFriends_StatusFalse() throws Exception {
        // Mock necessary objects
        UsersService usersService = Mockito.mock(UsersService.class);
        FriendsView friendsView = Mockito.mock(FriendsView.class);

        // Define behaviours
        FriendsResponse friendsResponse = new FriendsResponse(new ArrayList<>());
        friendsResponse.success = false;
        friendsResponse.errors = new ArrayList<>();

        // List of friends for friendsResponse
        ArrayList<User> friendsForFriendsResponse = new ArrayList<>();
        friendsForFriendsResponse.add(new User(1, "user1", "Fake User", "1", "I'm a fake user", "keystorm@rocketmail.com"));
        friendsForFriendsResponse.add(new User(2, "user2", "Fake User", "2", "I'm a fake user too", "dragonica@gmail.com"));
        friendsResponse.friends = friendsForFriendsResponse;

        // Model the behaviour of the users service by pretending that it will give successful request
        Mockito.when(usersService.getFriends()).thenReturn(
                Observable.just(friendsResponse)
        );

        // Test the appropriate method
        FriendsPresenter friendsPresenter = new FriendsPresenter(friendsView, usersService);
        friendsPresenter.loadFriends();

        // Make sure that it display toast error message
        Mockito.verify(friendsView).showToast(anyString());
    }

}