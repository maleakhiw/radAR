package radar.radar.Presenters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Responses.AddFriendResponse;
import radar.radar.Models.Responses.StatusError;
import radar.radar.Services.UsersService;
import radar.radar.Views.UserDetailView;

import static org.junit.Assert.*;

/**
 * Created by keyst on 30/09/2017.
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

    /** Unit testing for successful generate friend request */
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

        // Assert
        Mockito.verify(userDetailView).showToastLong("User have been added successfully.");
    }

    /** Unit testing for failure generate friend request */
    @Test
    public void generateFriendRequest_Failed() throws Exception {
        // Mock userdetailview and usersservice
        UserDetailView userDetailView = Mockito.mock(UserDetailView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Create instance of presenter that will be checked
        UserDetailPresenter presenter = new UserDetailPresenter(userDetailView, usersService);
        AddFriendResponse addFriendResponse = new AddFriendResponse();
        addFriendResponse.success = false;
        addFriendResponse.errors = new ArrayList<>();
        addFriendResponse.errors.add(new StatusError("fakeReason", 42));

        Mockito.when(usersService.addFriend(1)).thenReturn(
                Observable.just(addFriendResponse));

        // Test
        presenter.generateFriendRequest(1);

        // Assert
        Mockito.verify(userDetailView).showToastLong("User have been added previously. Please wait for confirmation.");
    }

}