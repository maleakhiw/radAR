package radar.radar.Views;

/**
 * Created by keyst on 26/09/2017.
 */

public interface LoginView {

    void setProgressBarMessage(String message);

    void showProgressBar();

    void dismissProgressBar();

    String getUsernameText();

    String getPasswordText();

    void showToastLong(String message);

    void startHomeScreenActivity();

    void startSignUpActivity();

    void finishActivity();
}
