package radar.radar.Presenters;

import android.widget.EditText;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Services.AuthService;
import radar.radar.Views.SignupView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by keyst on 27/09/2017.
 */
public class SignupPresenterTest {

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
     * Method that are used to check whether the validation form act like what we want in to be
     * In this case we want the validation form to validate successfully if every field
     * is entered correctly
     * @throws Exception
     */
    @Test
    public void validateForm_Success() throws Exception {
        // Set up the SignupView mock
        SignupView signupView = Mockito.mock(SignupView.class);

        // Setup behaviour of the view
        Mockito.when(signupView.getUsernameText()).thenReturn("maleakhiw");
        Mockito.when(signupView.getEmailText()).thenReturn("keystorm7@gmail.com");
        Mockito.when(signupView.getPassword()).thenReturn("hunter2");
        Mockito.when(signupView.getFirstName()).thenReturn("Maleakhi");
        Mockito.when(signupView.getLastName()).thenReturn("Wijaya");

        // Set up the AuthService mock
        AuthService authService = Mockito.mock(AuthService.class);

        // Set up the presenter
        SignupPresenter presenter = new SignupPresenter(signupView, authService);

        boolean isFormValid = presenter.validateForm();
        // make sure that a valid form input causes a True value to be returned
        assert(isFormValid);

        // make sure it's actually getting data from the SignupView, not just from thin air
        Mockito.verify(signupView).getUsernameText();
        Mockito.verify(signupView).getEmailText();
        Mockito.verify(signupView).getPassword();
        Mockito.verify(signupView).getFirstName();
        Mockito.verify(signupView).getLastName();

    }

    /**
     * Method that are used to check whether the validation form act like what we want in to be
     * In this case when user does not entered correctly such as leaving some things empty
     * it will generate false
     * @throws Exception
     */
    @Test
    public void validateForm_Failure() throws Exception {
        // Set up the SignupView mock
        SignupView signupView = Mockito.mock(SignupView.class);
        Mockito.when(signupView.getUsernameText()).thenReturn("");
        Mockito.when(signupView.getEmailText()).thenReturn("blablabla@gmail.com");
        Mockito.when(signupView.getPassword()).thenReturn("1231231231");
        Mockito.when(signupView.getFirstName()).thenReturn("Maleakhi");
        Mockito.when(signupView.getLastName()).thenReturn("Wijaya");

        AuthService authService = Mockito.mock(AuthService.class);

        SignupPresenter presenter = new SignupPresenter(signupView, authService);

        boolean isFormValid = presenter.validateForm();
        // Assert that it will return false because it is not valid
        assert(isFormValid == false);

        Mockito.verify(signupView).getPassword();
        Mockito.verify(signupView).getUsernameText();
        Mockito.verify(signupView).getEmailText();
        Mockito.verify(signupView).getFirstName();
        Mockito.verify(signupView).getLastName();
    }


    /**
     * Method that are used to test whether processSignup works as intended (processing signup request
     * to server) In the case of success, it will bring the user to home screen and finish the
     * SignUpActivity.
     * @throws Exception
     */
    @Test
    public void processSignup_Success() throws Exception {
        // Set up the signupview mock and signup presenter
        SignupView signupView = Mockito.mock(SignupView.class);
        AuthService authService = Mockito.mock(AuthService.class);

        // Need to create Mockito.spy because we will call validateForm that we will manipulate
        // its behaviour
        SignupPresenter presenterToBeSpiedOn = new SignupPresenter(signupView, authService);
        SignupPresenter presenter = Mockito.spy(presenterToBeSpiedOn);

        // Mockito documentation also suggests using doReturn().when() syntax instead of
        // when().thenReturn() - safer for spies
        Mockito.doReturn(true).when(presenter).validateForm();

        // return an actual response! This is a success, not a failure
        Observable<AuthResponse> authResponseObservable = Observable.just(new
                AuthResponse(true, new ArrayList<>(), "fakeToken", 42));
        Mockito.when(authService.signUp(any(SignUpRequest.class))).thenReturn(authResponseObservable);
        presenter.processSignup();

        // Check whether it is properly called
        Mockito.verify(signupView).dismissProgressBar();
        Mockito.verify(signupView).startHomeScreenActivity();
        Mockito.verify(signupView).finishActivity();
    }

    /**
     * Method that are used to test whether processSignup works as intended (processing signup request
     * to server) In the case of failure, it will log the error, display a toast to user.
     * @throws Exception
     */
    @Test
    public void processSignup_Failure() throws Exception {
        // Set up the signupview mock and signup presenter
        SignupView signupView = Mockito.mock(SignupView.class);
        AuthService authService = Mockito.mock(AuthService.class);

        SignupPresenter presenterToBeSpiedOn = new SignupPresenter(signupView, authService);
        SignupPresenter presenter = Mockito.spy(presenterToBeSpiedOn);

        Mockito.doReturn(true).when(presenter).validateForm();

        // pretend a SocketTimeoutException occurred
        Observable<AuthResponse> authResponseObservable = Observable.just(new
                AuthResponse(true, new ArrayList<>(), "fakeToken", 42))
                .map(authResponse -> {
                    throw new SocketTimeoutException("fake socket timeout exception");
                });
        Mockito.when(authService.signUp(any(SignUpRequest.class))).thenReturn(authResponseObservable);
        
        // Call the method that will be tested
        presenter.processSignup();

        // Check whether it is properly called
        Mockito.verify(signupView).showToastLong("Internal Error. Sign Up failed.");
        Mockito.verify(signupView).dismissProgressBar();
    }

}