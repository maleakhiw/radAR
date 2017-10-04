package radar.radar.Presenters;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.HomeScreenActivity;
import radar.radar.LoginActivity;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Views.LoginView;

/**
 * Created by keyst on 26/09/2017.
 */

public class LoginPresenter {
    LoginView loginView;
    AuthService authService;

    public LoginPresenter(LoginView loginView, AuthService authService) {
        this.loginView = loginView;
        this.authService = authService;
    }

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
                loginView.showToastLong("Unexpected error");
                loginView.dismissProgressBar();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void onSignUpClicked() {
        loginView.startSignUpActivity();
        loginView.finishActivity();
    }

    public void respondToLoginResponse(AuthResponse authResponse) {
        loginView.dismissProgressBar();

        if (authResponse.success) {
            // Go to another activity
            loginView.startHomeScreenActivity();
            loginView.finishActivity();
        }
        else {
            loginView.showToastLong("Login failed. Please check your username or password.");
        }
    }


}
