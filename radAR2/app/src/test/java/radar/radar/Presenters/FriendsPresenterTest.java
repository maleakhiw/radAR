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

    @Test
    public void respondToFABClick() throws Exception {
        FriendsView friendsView = Mockito.mock(FriendsView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        FriendsPresenter presenter = new FriendsPresenter(friendsView, usersService);
        presenter.respondToFABClick();

        // I want to make sure that feedback is shown to the user in the form
        // of a new Activity being launched.
        Mockito.verify(friendsView).launchSearchFriendsActivity();
    }

    @Test
    public void loadFriends_connectionError() throws Exception {
        UsersService usersService = Mockito.mock(UsersService.class);
        FriendsView friendsView = Mockito.mock(FriendsView.class);

        // define an Observable that simply throws an error.
        FriendsResponse friendsResponse = new FriendsResponse(new ArrayList<>());
        Observable<FriendsResponse> observable = Observable.just(friendsResponse)
                                                .map(friendsResponse1 -> {
                                                    throw new SocketTimeoutException("Fake timeout exception");
                                                });


        Mockito.when(usersService.getFriends()).thenReturn(observable);

        // system under test
        FriendsPresenter friendsPresenter = new FriendsPresenter(friendsView, usersService);
        friendsPresenter.loadFriends();

        // assertions
        Mockito.verify(friendsView).showToast(anyString());

    }

    @Test
    public void loadFriends_loginFailure() throws Exception {
        UsersService usersService = Mockito.mock(UsersService.class);
        FriendsView friendsView = Mockito.mock(FriendsView.class);

        // define behaviours
        FriendsResponse friendsResponse = new FriendsResponse(new ArrayList<>());
        friendsResponse.success = false;
        ArrayList<User> friendsForFriendsResponse = new ArrayList<>();
        friendsResponse.friends = friendsForFriendsResponse;
        friendsResponse.errors = new ArrayList<>();
        friendsResponse.errors.add(new StatusError("fakeReason", 42));

        Mockito.when(usersService.getFriends()).thenReturn(
                Observable.just(friendsResponse)    // return an object similar to one encountered
                // in a successful request
        );

        // system under test
        FriendsPresenter friendsPresenter = new FriendsPresenter(friendsView, usersService);
        friendsPresenter.loadFriends();

        // make sure the View is told to inform that the login failed
        Mockito.verify(friendsView).showToast(anyString());

    }

    @Test
    public void loadFriends_success() throws Exception {
        UsersService usersService = Mockito.mock(UsersService.class);
        FriendsView friendsView = Mockito.mock(FriendsView.class);

        // define behaviours
        FriendsResponse friendsResponse = new FriendsResponse(new ArrayList<>());
        friendsResponse.success = true;
        friendsResponse.errors = new ArrayList<>();

        // list of friends for friendsResponse
        ArrayList<User> friendsForFriendsResponse = new ArrayList<>();
        friendsForFriendsResponse.add(new User(1, "user1", "Fake User", "1", "I'm a fake user", "keystorm@rocketmail.com"));
        friendsForFriendsResponse.add(new User(2, "user2", "Fake User", "2", "I'm a fake user too", "dragonica@gmail.com"));

        friendsResponse.friends = friendsForFriendsResponse;

        Mockito.when(usersService.getFriends()).thenReturn(
                Observable.just(friendsResponse)    // return an object similar to one encountered
                                                    // in a successful request
        );

        // system under test
        FriendsPresenter friendsPresenter = new FriendsPresenter(friendsView, usersService);
        friendsPresenter.loadFriends();

        // assertions
        Mockito.verify(friendsView).bindAdapterToRecyclerView(friendsForFriendsResponse);

    }

}