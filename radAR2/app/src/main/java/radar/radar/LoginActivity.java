package radar.radar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Presenters.LoginPresenter;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Views.LoginView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity
                           implements LoginView {

    private EditText username;
    private EditText password;
    private Button btn_login;
    private TextView link_signup;
    private ProgressDialog mProgress;

    private AuthService authService;  // service for making requests to our API

    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup UI
        setupUI();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();

        // get a Retrofit instance (this is also called the Builder pattern)
        // This is used to create an api class
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

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

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onLoginButtonClicked();
            }
        });
    }

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
    public String getUsernameText() {
        return username.getText().toString();
    }

    @Override
    public String getPasswordText() {
        return password.getText().toString();
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
    public void startSignUpActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishActivity() {
        finish();
    }


    /** Use to connect UI with java */
    public void setupUI() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);
        link_signup = (TextView) findViewById(R.id.link_signup);

        mProgress = new ProgressDialog(LoginActivity.this);
    }
}
