package radar.radar.Views;

/**
 * Interface for SignupActivity that are used to support MVP model.
 */
public interface SignupView {
    // Some setter and getter for the private variable
    String getUsernameText();

    String getEmailText();

    String getPassword();

    String getFirstName();

    String getLastName();

    void setProgressBarMessage(String message);

    void showProgressBar();

    void dismissProgressBar();

    void showToastLong(String message);

    void startHomeScreenActivity();

    void startLoginActivity();

    void finishActivity();
}
