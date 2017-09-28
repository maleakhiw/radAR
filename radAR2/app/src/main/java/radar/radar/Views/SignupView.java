package radar.radar.Views;

/**
 * Created by keyst on 27/09/2017.
 */

public interface SignupView {
    // Some setter and getter for the private variable
    String getUsernameText();

    String getEmailText();

    String getPassword();

    void setProgressBarMessage(String message);

    void showProgressBar();

    void dismissProgressBar();

    void showToastLong(String message);

    void startHomeScreenActivity();

    void startLoginActivity();

    void finishActivity();
}
