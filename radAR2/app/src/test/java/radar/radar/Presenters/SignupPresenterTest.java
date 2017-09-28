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

    // Test to check whether validation works
    @Test
    public void validateForm_Success() throws Exception {
        // Set up the SignupView mock
        SignupView signupView = Mockito.mock(SignupView.class);
        Mockito.when(signupView.getUsernameText()).thenReturn("maleakhiw");
        Mockito.when(signupView.getEmailText()).thenReturn("keystorm7@gmail.com");
        Mockito.when(signupView.getPassword()).thenReturn("hunter2");

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

    }

    @Test
    public void validateForm_Failed() throws Exception {
        // Set up the SignupView mock
        SignupView signupView = Mockito.mock(SignupView.class);
        Mockito.when(signupView.getUsernameText()).thenReturn("");
        Mockito.when(signupView.getEmailText()).thenReturn("blablabla@gmail.com");
        Mockito.when(signupView.getPassword()).thenReturn("1231231231");

        AuthService authService = Mockito.mock(AuthService.class);

        SignupPresenter presenter = new SignupPresenter(signupView, authService);

        boolean isFormValid = presenter.validateForm();
        assert(isFormValid);

        Mockito.verify(signupView).getPassword();
        Mockito.verify(signupView).getUsernameText();
        Mockito.verify(signupView).getEmailText();
    }


    @Test
    public void processSignup_Success() throws Exception {
        // Set up the signupview mock and signup presenter
        SignupView signupView = Mockito.mock(SignupView.class);
        AuthService authService = Mockito.mock(AuthService.class);

        SignupPresenter presenterToBeSpiedOn = new SignupPresenter(signupView, authService);
        SignupPresenter presenter = Mockito.spy(presenterToBeSpiedOn);

        // NOTE https://stackoverflow.com/a/11620196 - Mockito calls presenter.validateForm()
        // which fails due to a NullPointerException.

        // Mockito documentation also suggests using doReturn().when() syntax instead of
        // when().thenReturn() - safer for spies
        Mockito.doReturn(true).when(presenter).validateForm();

        // return an actual response! This is a success, not a failure
        Observable<AuthResponse> authResponseObservable = Observable.just(new AuthResponse(true, new ArrayList<>(), "fakeToken", 42));
        Mockito.when(authService.signUp(any(SignUpRequest.class))).thenReturn(authResponseObservable);
        presenter.processSignup();

        // Check whether it is properly called
        Mockito.verify(signupView).dismissProgressBar();
        Mockito.verify(signupView).startHomeScreenActivity();
        Mockito.verify(signupView).finishActivity();
    }

    @Test
    public void processSignup_Failed() throws Exception {
        // Set up the signupview mock and signup presenter
        SignupView signupView = Mockito.mock(SignupView.class);
        AuthService authService = Mockito.mock(AuthService.class);

        SignupPresenter presenterToBeSpiedOn = new SignupPresenter(signupView, authService);
        SignupPresenter presenter = Mockito.spy(presenterToBeSpiedOn);

        Mockito.doReturn(true).when(presenter).validateForm();

        // pretend a SocketTimeoutException occurred
        Observable<AuthResponse> authResponseObservable = Observable.just(new AuthResponse(true, new ArrayList<>(), "fakeToken", 42))
                .map(authResponse -> {
                    throw new SocketTimeoutException("fake socket timeout exception");
                });
        Mockito.when(authService.signUp(any(SignUpRequest.class))).thenReturn(authResponseObservable);
        presenter.processSignup();

        // Check whether it is properly called
        Mockito.verify(signupView).showToastLong(anyString());
        Mockito.verify(signupView).dismissProgressBar();

    }

}