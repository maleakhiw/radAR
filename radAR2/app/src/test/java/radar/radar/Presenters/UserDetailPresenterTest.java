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
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Models.Responses.StatusError;
import radar.radar.Services.UsersService;
import radar.radar.Views.UserDetailView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;

/**
 * Unit Testing class for testing application logic of UserDetail
 */
public class UserDetailPresenterTest {
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
     * Unit testing for successful generate friend request. When it is success, we should see
     * message saying that we have successfully added the user
     */
    @Test
    public void generateFriendRequest_Success() throws Exception {
        // Mock userdetailview and usersservice
        UserDetailView userDetailView = Mockito.mock(UserDetailView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Create instance of presenter that will be checked
        UserDetailPresenter presenter = new UserDetailPresenter(userDetailView, usersService);
        AddFriendResponse addFriendResponse = new AddFriendResponse();
        addFriendResponse.success = true;

        Mockito.when(usersService.addFriend(1)).thenReturn(
                Observable.just(addFriendResponse));

        // Test
        presenter.generateFriendRequest(1);

        // Assert the it produce successful message
        Mockito.verify(userDetailView).showToastShort(anyString());
    }

    /**
     * Unit testing for failure generate friend request. because status is false
     */
    @Test
    public void generateFriendRequest_StatusFalse() throws Exception {
        // Mock userdetailview and usersservice
        UserDetailView userDetailView = Mockito.mock(UserDetailView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Create instance of presenter that will be checked
        UserDetailPresenter presenter = new UserDetailPresenter(userDetailView, usersService);
        AddFriendResponse addFriendResponse = new AddFriendResponse();
        addFriendResponse.success = false;

        Mockito.when(usersService.addFriend(1)).thenReturn(
                Observable.just(addFriendResponse));

        // Test
        presenter.generateFriendRequest(1);

        // Assert that it produces error message
        Mockito.verify(userDetailView).showToastShort(anyString());
    }

    /**
     * Unit testing for failure generate friend request. We need to tell user that adding
     * friends is failed.
     */
    @Test
    public void generateFriendRequest_Failure() throws Exception {
        // Mock userdetailview and usersservice
        UserDetailView userDetailView = Mockito.mock(UserDetailView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Create instance of presenter that will be checked
        UserDetailPresenter presenter = new UserDetailPresenter(userDetailView, usersService);

        // Error observable
        Observable<AddFriendResponse> errorThrowingObservable = Observable.just(new AuthResponse(false,
                null, null, 0))
                .map(fakeResponse -> {
                    throw new SocketTimeoutException("Fake internet timeout error.");
                });

        Mockito.when(usersService.addFriend(1)).thenReturn(
                (errorThrowingObservable));

        // Test by calling the method
        presenter.generateFriendRequest(1);

        // Assert that it return error
        Mockito.verify(userDetailView).showToastShort(anyString());
    }

}