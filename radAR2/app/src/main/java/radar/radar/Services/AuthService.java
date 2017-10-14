package radar.radar.Services;

import android.content.Context;
import android.content.SharedPreferences;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Service for authentication that served as layer of abstraction for retrofit. The method here
 * will call AuthApi.java
 */
public class AuthService {
    AuthApi authApi;
    SharedPreferences prefs;

    /**
     * Retrieves the authentication token from SharedPreferences. Returns null if unset.
     * @param context Android Context
     * @return authentication token
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        return prefs.getString("radar_token", null);
    }

    /**
     * Retrieves the userID from SharedPreferences. Returns 0 if unset.
     * @param context Android Context
     * @return userID
     */
    public static int getUserID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        return prefs.getInt("radar_userID", 0);
    }

    /**
     * Retrieves the user first name from SharedPreferences. Returns "" if unset.
     * @param context Android Context
     * @return userID
     */
    public static String getFirstName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        return prefs.getString("firstName", "");
    }

    /**
     * Retrieves the user first name from SharedPreferences. Returns "" if unset.
     * @param context Android Context
     * @return userID
     */
    public static String getLastName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        return prefs.getString("lastName", "");
    }

    /**
     * Retrieves the user email from SharedPreferences. Returns "" if unset.
     * @param context Android Context
     * @return userID
     */
    public static String getEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        return prefs.getString("email",  "");
    }

    /**
     * Constructor class for AuthService.java
     * @param context Android Context
     * @param authApi Instance of the authentication api created by retrofit
     * @return userID
     */
    public AuthService(AuthApi authApi, Context context) {
        this.prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        this.authApi = authApi;
    }

    /**
     * Creates a new radAR account. Also stores the userID and token (required for future requests)
     * to SharedPreferences.
     * @param body parameters (form fields) for a new account
     * @return Observable to be subscribed to for the API response
     */
    public Observable<AuthResponse> signUp(SignUpRequest body) {
        Observable<AuthResponse> observable = authApi.signUp(body)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread());

        Observable<AuthResponse> newObservable = Observable.create(new ObservableOnSubscribe<AuthResponse>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResponse> emitter) throws Exception {
                observable.subscribe(new Observer<AuthResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AuthResponse authResponse) {
                        if (authResponse.success) {
                            prefs.edit().putString("radar_token", authResponse.token)
                                    .putInt("radar_userID", authResponse.userID)
                                    .putString("firstName", authResponse.userInfo.firstName)
                                    .putString("lastName", authResponse.userInfo.lastName)
                                    .putString("email", authResponse.userInfo.email)
                                    .apply();
                        }

                        emitter.onNext(authResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        emitter.onComplete();

                    }
                });
            }
        });

        return newObservable;
    }

    /**
     * Logs into an existing radAR account.
     * Also stores the userID and token (required for future requests) to SharedPreferences.
     * @param username Username for the account
     * @param password Password for the account
     * @return Observable to be subscribed to for the API response
     */
    public Observable<AuthResponse> login(String username, String password) {
        Observable<AuthResponse> observable = authApi.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<AuthResponse> newObservable = Observable.create(new ObservableOnSubscribe<AuthResponse>() {
            @Override
            public void subscribe(ObservableEmitter<AuthResponse> emitter) throws Exception {
                observable.subscribe(new Observer<AuthResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AuthResponse authResponse) {
                        if (authResponse.success) {
                            prefs.edit().putString("radar_token", authResponse.token)
                                    .putInt("radar_userID", authResponse.userID)
                                    .putString("firstName", authResponse.userInfo.firstName)
                                    .putString("lastName", authResponse.userInfo.lastName)
                                    .putString("email", authResponse.userInfo.email)
                                    .apply();
                        }
                        emitter.onNext(authResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        emitter.onComplete();

                    }
                });
            }
        });

        return newObservable;
    }

    /**
     * Sign out from the application, removing the shared preferences store in the device
     * @param context context of the application
     */
    public static void signOut(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        prefs.edit().remove("radar_token").remove("radar_userID").apply();
    }

}
