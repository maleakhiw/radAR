package radar.radar.Presenters;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.HomeScreenActivity;
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.R;
import radar.radar.Services.AuthService;
import radar.radar.SignupActivity;
import radar.radar.Views.SignupView;

/**
 * Created by keyst on 27/09/2017.
 */

public class SignupPresenter {
    SignupView signupView;
    AuthService authService;

    public SignupPresenter(SignupView signupView, AuthService authService) {
        this.signupView = signupView;
        this.authService = authService;
    }

    /** Validation check to make sure that there is no empty things on the form */
    public boolean validateForm() {
        String username, email, password, firstName, lastName;
        // Check to make sure that everything is filled

        username = signupView.getUsernameText().trim();
        email = signupView.getEmailText().trim();
        password = signupView.getPassword().trim();
        firstName = signupView.getFirstName().trim();
        lastName = signupView.getLastName().trim();


        return !(username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty()
                || lastName.isEmpty());
    }

    /** Method to signup processes */
    public void processSignup() {
        signupView.setProgressBarMessage("Signing Up...");
        signupView.showProgressBar();
        if (validateForm()) {
            SignUpRequest signUpRequest = new SignUpRequest(signupView.getFirstName(), signupView.getLastName(),
                    signupView.getEmailText(), signupView.getUsernameText(),
                    "Hello, I am using Radar!",
                    signupView.getPassword(), "DeviceID");

            authService.signUp(signUpRequest).subscribe(new Observer<AuthResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(AuthResponse authResponse) {
                    // Jump to home
                    signupView.dismissProgressBar();
                    if (authResponse.success) {
                        signupView.startHomeScreenActivity();
                        signupView.finishActivity();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println(e);
                    signupView.showToastLong("Sign Up failed.");
                    signupView.dismissProgressBar();
                }

                @Override
                public void onComplete() {

                }
            });
        }
        else {
            signupView.showToastLong("Please enter all fields.");
            signupView.dismissProgressBar();
        }
    }
}