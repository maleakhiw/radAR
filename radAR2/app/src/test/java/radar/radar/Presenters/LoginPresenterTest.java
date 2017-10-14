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
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Views.LoginView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Unit testing for LoginPresenter class
 * This class is used to test the application logic of login
 */
public class LoginPresenterTest {
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
     * This method is used to test and get the behaviour when there is a timeout on the server
     * If there is a timeout we want to check whether the method on the presenter log the error
     * and does not log in the user
     * @throws Exception
     */
    @Test
    public void onLoginButtonClicked_Failure() throws Exception {
        // set up the LoginView mock
        LoginView loginView = Mockito.mock(LoginView.class);

        // set up the AuthService mock
        AuthService authService = Mockito.mock(AuthService.class);

        /* simulate a failure (can be a connection error, etc.) */
        // set up an observable that throws an error
        Observable<AuthResponse> errorThrowingObservable = Observable.just(new AuthResponse(false,
                null, null, 0))
                .map(fakeResponse -> {
           throw new SocketTimeoutException("Fake internet timeout error.");
        });

        // return the errorThrowingObservable when authService.login is called
        // to simulate an error condition
        Mockito.when(authService.login(anyString(), anyString()))
                .thenReturn(errorThrowingObservable);

        // set up a new presenter, not with the real dependencies but the mocks
        LoginPresenter presenter = new LoginPresenter(loginView, authService);

        // we're all set up, time to simulate a call to onLoginButtonClicked()
        presenter.onLoginButtonClicked();

        // thanks to the scheduler setup above, all the code runs synchronously
        // let's make sure feedback is displayed to the user and the progressBar is dismissed
        Mockito.verify(loginView).showToastLong(anyString());
        Mockito.verify(loginView).dismissProgressBar();
    }

    /**
     * This method is used to check whether when login button is clicked, it will call
     * the method respondToLoginResponse to handle the login process. It will used to check
     * successful condition of login.
     * @throws Exception
     */
    @Test
    public void onLoginButtonClicked_Success() throws Exception {
        // set up the LoginView mock
        LoginView loginView = Mockito.mock(LoginView.class);

        // set up the AuthService mock
        AuthService authService = Mockito.mock(AuthService.class);

        // return an actual response! This is a success, not a failure
        Observable<AuthResponse> authResponseObservable = Observable.just(new
                AuthResponse(true, new ArrayList<>(), "fakeToken", 42));

        Mockito.when(authService.login(anyString(), anyString())).thenReturn(authResponseObservable);

        // set up a new presenter, not with the real dependencies but the mocks
        LoginPresenter presenterToBeSpiedOn = new LoginPresenter(loginView, authService);
        LoginPresenter presenter = Mockito.spy(presenterToBeSpiedOn);
        // if the function is actually not a void method, you might be able to use Mockito.when to override the spy method

        // we're all set up, time to simulate a call to onLoginButtonClicked()
        presenter.onLoginButtonClicked();

        // it will be a success, so the method respondToLoginResponse(loginResponse) will be called.
        // let's check with our spied presenter to see if it has been called
        Mockito.verify(presenter).respondToLoginResponse(any(AuthResponse.class));
    }

    /**
     * This method is used to unit test responding to login request successfully
     * It will go to HomeScreen activity
     * @throws Exception
     */
    @Test
    public void respondToLoginResponse_Success() throws Exception {
        // set up the LoginView mock
        LoginView loginView = Mockito.mock(LoginView.class);

        // set up the AuthService mock
        AuthService authService = Mockito.mock(AuthService.class);

        // set up a new presenter, not with the real dependencies but the mocks
        LoginPresenter presenterToBeSpied = new LoginPresenter(loginView, authService);
        LoginPresenter presenter = Mockito.spy(presenterToBeSpied);
        presenter.respondToLoginResponse(new AuthResponse(true, new ArrayList<>(),
                "3.141592653589793ispi", 42));

        Mockito.verify(loginView).startHomeScreenActivity();
        Mockito.verify(loginView).finishActivity();
    }

    /**
     * This method is used to unit test responding to login request when failure
     * It will show toast that the credentials are wrong
     * @throws Exception
     */
    @Test
    public void respondToLoginResponse_Failure() throws Exception {
        // set up the LoginView mock
        LoginView loginView = Mockito.mock(LoginView.class);

        // set up the AuthService mock
        AuthService authService = Mockito.mock(AuthService.class);

        // set up a new presenter, not with the real dependencies but the mocks
        LoginPresenter presenterToBeSpied = new LoginPresenter(loginView, authService);
        LoginPresenter presenter = Mockito.spy(presenterToBeSpied);
        presenter.respondToLoginResponse(new AuthResponse(false, new ArrayList<>(),
                null, 0));

        // make sure feedback is shown to the user
        Mockito.verify(loginView).showToastLong(anyString());
    }

}