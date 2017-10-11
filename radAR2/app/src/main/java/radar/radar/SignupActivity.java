package radar.radar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import radar.radar.Presenters.SignupPresenter;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Views.SignupView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * View part of the MVP model for signup activity/ screen
 */
public class SignupActivity extends AppCompatActivity implements SignupView {
    /** Variable for UI element */
    private EditText email;
    private EditText username;
    private EditText password;
    private Button btn_signup;
    private TextView link_login;
    private ProgressDialog mProgress; // for loading animation
    private EditText firstName;
    private EditText lastName;

    /** Variable for service and presenter */
    private AuthService authService;
    private SignupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Setup UI
        setupUI();

        // get a Retrofit instance (this is also called the Builder pattern)
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("https://radar.fadhilanshar.com/api/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .build();

        // make a new AuthApi using our current Retrofit *instance*
        AuthApi authApi = retrofit.create(AuthApi.class);

        // get an instance of the service. Ideally we want to use a Factory or use DI
        // (dependency injection) to a class as a dependency so we can mock the service instead of
        // locking us to use the real service
        authService = new AuthService(authApi, this);

        // Initialized presenter
        presenter = new SignupPresenter(this, authService);

        // Create on click listener for link login
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When clicked go to login page
                startLoginActivity();
            }
        });

        // When the user sign up, we want to pass everything to the server and database
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               presenter.processSignup();
            }
        });
    }

    /**
     * Setting up the User Interface by connecting UI element with Java
     */
    public void setupUI() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_signup = findViewById(R.id.btn_signup);
        link_login = findViewById(R.id.link_login);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        mProgress = new ProgressDialog(SignupActivity.this);
    }

    /**
     * Method to get username from text input/ edit text that user have inputted
     * @return String returning the username that the user have entered on the signup screen
     */
    @Override
    public String getUsernameText() {
        return username.getText().toString();
    }

    /**
     * Method to get email from text input/ edit text that user have inputted
     * @return String returning the email that the user have entered on the signup screen
     */
    @Override
    public String getEmailText() {
        return email.getText().toString();
    }

    /**
     * Method to get password from text input/ edit text that user have inputted
     * @return String returning the password that the user have entered on the signup screen
     */
    @Override
    public String getPassword() {
        return password.getText().toString();
    }

    /**
     * Method to get first name from text input/ edit text that user have inputted
     * @return String returning the first name that the user have entered on the signup screen
     */
    @Override
    public String getFirstName() { return firstName.getText().toString(); }

    /**
     * Method to get last name from text input/ edit text that user have inputted
     * @return String returning the last name that the user have entered on the signup screen
     */
    @Override
    public String getLastName() { return lastName.getText().toString(); }

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
     * Method to display a toast and set a message to it.
     * @param message the message that will be display using toast
     */
    @Override
    public void showToastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
     * Method to jump from this activity to login if user click login link
     */
    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Method to terminate this activity
     */
    @Override
    public void finishActivity() {
        finish();
    }

}
