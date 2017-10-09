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

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Presenters.SignupPresenter;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Views.SignupView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity implements SignupView {
    private EditText email;
    private EditText username;
    private EditText password;
    private Button btn_signup;
    private TextView link_login;
    private ProgressDialog mProgress; // for loading animation
    private EditText firstName;
    private EditText lastName;

    private AuthService authService;  // service for making requests to our API

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
        SignupPresenter presenter = new SignupPresenter(this, authService);

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

    /** Setting up the User Interface */
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

    // Some setter and getter for the private variable
    @Override
    public String getUsernameText() {
        return username.getText().toString();
    }

    @Override
    public String getEmailText() {
        return email.getText().toString();
    }

    @Override
    public String getPassword() {
        return password.getText().toString();
    }

    @Override
    public String getFirstName() { return firstName.getText().toString(); }

    @Override
    public String getLastName() { return lastName.getText().toString(); }

    @Override
    public void setProgressBarMessage(String message) {
        mProgress.setMessage(message);
    }

    @Override
    public void showProgressBar() {
        mProgress.show();
    }

    @Override
    public void dismissProgressBar() {
        mProgress.dismiss();
    }

    @Override
    public void showToastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startHomeScreenActivity() {
        Intent intent = new Intent(this,
                HomeScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishActivity() {
        finish();
    }

}
