package radar.radar.Presenters;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Models.Responses.FriendRequest;
import radar.radar.Models.Responses.FriendRequestsResponse;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Services.UsersService;
import radar.radar.Views.PendingRequestsView;
import radar.radar.Views.SearchUserView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Class that are used to unit test PendingRequestsPresenter.
 * This class will test application logic of pending friend requests.
 */
public class PendingRequestsPresenterTest {
    /**
     * Method to setup class that are used when unit testing retrofit rxjava
     */
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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Unit test displayFriendsRequest() to make sure that when there is an error, the application
     * will display error message to user screen and notify that there are problems
     */
    @Test
    public void displayFriendsRequest_Failure() throws Exception {
        // Setup necessary mock
        PendingRequestsView pendingRequestsView = Mockito.mock(PendingRequestsView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Control the behaviour of user service to return error
        Observable<FriendRequestsResponse> errorThrowingObservable = Observable.just(new AuthResponse(false,
                null, null, 0))
                .map(fakeResponse -> {
                    throw new SocketTimeoutException("Fake internet timeout error.");
                });

        // Throw an error
        Mockito.when(usersService.getFriendRequests()).thenReturn(errorThrowingObservable);

        // Test the method
        PendingRequestsPresenter presenter = new PendingRequestsPresenter(pendingRequestsView, usersService);
        presenter.displayFriendsRequest();

        // Make sure displaying error message
        Mockito.verify(pendingRequestsView).showToast(anyString());
    }

    /**
     * Unit test for to make sure that when success displayFriendsRequest() will display all of the pending
     * friend request for users and bind them to the recycler view
     */
    @Test
    public void displayFriendRequest_Success() throws Exception {
        // Setup necessary mock
        PendingRequestsView pendingRequestsView = Mockito.mock(PendingRequestsView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Create observable that will return success
        FriendRequest friendRequest = Mockito.mock(FriendRequest.class);
        ArrayList<FriendRequest> request = new ArrayList<>();
        request.add(friendRequest);
        FriendRequestsResponse response = new FriendRequestsResponse(request);
        response.success = true; // initiate to true request
        Observable<FriendRequestsResponse> observable = Observable.just(response);

        // Throw success
        Mockito.when(usersService.getFriendRequests()).thenReturn(observable);

        // Test the method
        PendingRequestsPresenter presenter = new PendingRequestsPresenter(pendingRequestsView, usersService);
        presenter.displayFriendsRequest();

        // Make sure binding to recycler view and display view
        Mockito.verify(pendingRequestsView).bindAdapterToRecyclerView(response);
    }

    /**
     * Unit test for to make sure that when status false, will display error message
     */
    @Test
    public void displayFriendRequest_StatusFalse() throws Exception {
        // Setup necessary mock
        PendingRequestsView pendingRequestsView = Mockito.mock(PendingRequestsView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Create observable that will return success
        FriendRequest friendRequest = Mockito.mock(FriendRequest.class);
        ArrayList<FriendRequest> request = new ArrayList<>();
        request.add(friendRequest);
        FriendRequestsResponse response = new FriendRequestsResponse(request);
        response.success = false; // false request
        Observable<FriendRequestsResponse> observable = Observable.just(response);

        // Throw success
        Mockito.when(usersService.getFriendRequests()).thenReturn(observable);

        // Test the method
        PendingRequestsPresenter presenter = new PendingRequestsPresenter(pendingRequestsView, usersService);
        presenter.displayFriendsRequest();

        // Make sure display error message
        Mockito.verify(pendingRequestsView).showToast(anyString());
    }

}