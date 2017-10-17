package radar.radar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import radar.radar.Presenters.LoginPresenter;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Views.LoginView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * View part of the MVP model for Login activity/ screen
 */
public class LoginActivity extends AppCompatActivity implements LoginView {
    /** Variable capturing elements of the user interface in the xml file */
    private EditText username;
    private EditText password;
    private Button btn_login;
    private TextView link_signup;
    private ProgressDialog mProgress;

    /** Service and presenter variable */
    private AuthService authService;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup UI
        setupUI();

        // get a Retrofit instance (this is also called the Builder pattern)
        // This is used to create an api class
        Retrofit retrofit = RetrofitFactory.getRetrofit().build();

        // make a new AuthApi using our current Retrofit *instance*
        AuthApi authApi = retrofit.create(AuthApi.class);

        // get an instance of the service (lets you make API requests, with abstractions and
        // indirections to simplify server API requests)
        authService = new AuthService(authApi, this);

        // initialise the presenter with the dependencies
        presenter = new LoginPresenter(this, authService);

        // Setup on click listener for link signup
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSignUpClicked();
            }
        });

        // Setup on click listener for login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onLoginButtonClicked();
            }
        });
    }

    /**
     * Method to set Progress bar (loading animation)
     * @param message the message to display when loading the screen
     */
    @Override
    public void setProgressBarMessage(String message) {
        mProgress.setMessage(message);
    }

    /**
     * Method to display the Progress bar (loading animation)
     */
    @Override
    public void showProgressBar() {
        mProgress.show();
    }

    /**
     * Method to dismiss the Progress bar (loading animation)
     */
    @Override
    public void dismissProgressBar() {
        mProgress.dismiss();
    }

    /**
     * Method to get username from text input/ edit text that user have inputted
     * @return String returning the username that the user have entered on the login screen
     */
    @Override
    public String getUsernameText() {
        return username.getText().toString();
    }

    /**
     * Method to get password from text input/ edit text that user have inputted
     * @return String returning the password that the user have entered on the login screen
     */
    @Override
    public String getPasswordText() {
        return password.getText().toString();
    }

    /**
     * Method to display a toast and set a message to it.
     * @param message the message that will be display using toast
     */
    @Override
    public void showToastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to jump from this activity to homescreen after user click login
     */
    @Override
    public void startHomeScreenActivity() {
        Intent intent = new Intent(this,
                HomeScreenActivity.class);
        startActivity(intent);
    }

    /**
     * Method to jump from this activity to signup if user click sign up link
     */
    @Override
    public void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Finish the LoginActivity
     */
    @Override
    public void finishActivity() {
        finish();
    }

    /**
     * Method to connect user interface components on the screen/xml to java
     */
    public void setupUI() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        link_signup = findViewById(R.id.link_signup);

        mProgress = new ProgressDialog(LoginActivity.this);
    }
}
