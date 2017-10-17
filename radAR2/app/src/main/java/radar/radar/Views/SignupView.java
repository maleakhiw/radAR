package radar.radar.Views;

/**
 * Interface for SignUpActivity that are used to support MVP model.
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

    void showToastShort(String message);

    void startHomeScreenActivity();

    void startLoginActivity();

    void finishActivity();
}
