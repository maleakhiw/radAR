package radar.radar.Presenters;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Services.AuthService;
import radar.radar.Views.LoginView;

/**
 * Presenter class for Login functionality to support MVP model
 */
public class LoginPresenter {
    LoginView loginView;
    AuthService authService;

    /**
     * Constructor method for the presenter
     * @param loginView LoginActivity context
     * @param authService services that have been initialised on LoginActivity.java
     */
    public LoginPresenter(LoginView loginView, AuthService authService) {
        this.loginView = loginView;
        this.authService = authService;
    }

    /**
     * Specifying the logic after login button is clicked
     * Follow the MVP model to make the view as dumb as possible while all logic is on the presenter
     * so that it can be easily tested and deployed.
     */
    public void onLoginButtonClicked() {
        // Start loading the process
        loginView.setProgressBarMessage("Logging in...");
        loginView.showProgressBar();

        // Login using a username and password
        authService.login(loginView.getUsernameText(), loginView.getPasswordText())
        .subscribe(new Observer<AuthResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AuthResponse authResponse) {
                respondToLoginResponse(authResponse);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
                loginView.showToastShort("Internal Error. Failed to login.");
                loginView.dismissProgressBar();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * Method to jump to signup activity if user click signup link
     */
    public void onSignUpClicked() {
        loginView.startSignUpActivity();
        loginView.finishActivity();
    }

    /**
     * Core method that are used to check if the login is successful or error
     * @param authResponse object that will be send after getting response from server
     */
    public void respondToLoginResponse(AuthResponse authResponse) {
        loginView.dismissProgressBar();

        if (authResponse.success) {
            // Go to another activity
            loginView.startHomeScreenActivity();
            loginView.finishActivity();
        }
        else {
            loginView.showToastShort("Login failed.Please check your username or password.");
        }
    }


}
