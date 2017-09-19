package radar.radar;

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
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private EditText email;
    private EditText username;
    private EditText password;
    private Button btn_signup;
    private TextView link_login;

    private AuthService authService;  // service for making requests to our API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Setup UI
        setupUI();

        // get a Retrofit instance (this is also called the Builder pattern)
        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .build();

        // make a new AuthApi using our current Retrofit *instance*
        AuthApi authApi = retrofit.create(AuthApi.class);

        // get an instance of the service. Ideally we want to use a Factory or use DI (dependency injection) to a class as a dependency
        // so we can mock the service instead of locking us to use the real service
        authService = new AuthService(authApi, this);

        // Create on click listener for link login
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When clicked go to login page
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // When the user sign up, we want to pass everything to the server and database
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    SignUpRequest signUpRequest = new SignUpRequest("",
                            "", email.getText().toString(), username.getText().toString(),
                            "", password.getText().toString(), "fakeDeviceID");

                    authService.signUp(signUpRequest).subscribe(new Observer<AuthResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AuthResponse authResponse) {
                            // Jump to home
                            Intent intent = new Intent(SignupActivity.this, HomeScreenActivity.class);

                            startActivity(intent);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter all fields.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /** Setting up the User Interface */
    public void setupUI() {
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        link_login = (TextView) findViewById(R.id.link_login);
    }

    /** Validation check to make sure that there is no empty things on the form */
    public boolean validateForm() {
        String username, email, password;
        // Check to make sure that everything is filled

        username = this.username.getText().toString().trim();
        email = this.email.getText().toString().trim();
        password = this.password.getText().toString().trim();

        return !(username.isEmpty() || email.isEmpty() || password.isEmpty());
    }
}
