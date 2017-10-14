package radar.radar.Views;

/**
 * Interface for LoginActivity that are used to support MVP design
 * Note: for description of method please refer to LoginActivity.java
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
