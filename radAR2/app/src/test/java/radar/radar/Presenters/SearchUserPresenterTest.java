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
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Services.AuthService;
import radar.radar.Services.UsersService;
import radar.radar.Views.LoginView;
import radar.radar.Views.SearchUserView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;

/**
 * Unit testing for SearchUserPresenter class.
 * This class is used to test the application logic of all method in SearchUserPresenter.java
 */
public class SearchUserPresenterTest {
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
     * Unit test doSearch() to make sure that when there is an error, the application
     * will display error message to user screen and notify that there are problems
     */
    @Test
    public void doSearch_Failure() throws Exception {
        // Setup necessary mock
        SearchUserView searchUserView = Mockito.mock(SearchUserView.class);
        UsersService usersService = Mockito.mock(UsersService.class);

        // Control the behaviour of user service to return error
        Observable<UsersSearchResult> errorThrowingObservable = Observable.just(new AuthResponse(false,
                null, null, 0))
                .map(fakeResponse -> {
                    throw new SocketTimeoutException("Fake internet timeout error.");
                });

        Mockito.when(usersService.searchForUsers("maleakhi", "name")).thenReturn(errorThrowingObservable);

        // Test the method
        SearchUserPresenter presenter = new SearchUserPresenter(searchUserView, usersService);
        presenter.doSearch("maleakhi");

        // Make sure displaying error message
        Mockito.verify(searchUserView).showToast(anyString());
    }

}