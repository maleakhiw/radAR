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
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button btn_login;
    private TextView link_signup;

    private AuthService authService;  // service for making requests to our API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Setup UI
        setupUI();

        // get a Retrofit instance (this is also called the Builder pattern)
        // This is used to create an api class
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

        // Setup on click listener for link signup
        link_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to the signup
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Login using a username and password
                authService.login(username.getText().toString(), password.getText().toString()).subscribe(new Observer<AuthResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AuthResponse authResponse) {
                        // Go to another activity
                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "Login failed. Please check your credentials.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });

    }

    /** Use to connect UI with java */
    public void setupUI() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);
        link_signup = (TextView) findViewById(R.id.link_signup);
    }
}
