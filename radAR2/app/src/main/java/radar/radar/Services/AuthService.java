package radar.radar.Services;

import android.content.Context;
import android.content.SharedPreferences;

import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kenneth on 17/9/17.
 */

public class AuthService {
//    Retrofit retrofit;
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

    public AuthService(AuthApi authApi, Context context) {
        this.prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
//        this.retrofit = retrofit;
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
        observable.subscribe(new Observer<AuthResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AuthResponse authResponse) {
                // save to shared prefs
                prefs.edit().putString("radar_token", authResponse.token)
                        .putInt("radar_userID", authResponse.userID).commit();


            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });
        return observable;

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
        observable.subscribe(new Observer<AuthResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AuthResponse authResponse) {
                // save to shared prefs
                System.out.println(authResponse.token);
                prefs.edit().putString("radar_token", authResponse.token)
                        .putInt("radar_userID", authResponse.userID).commit();
                System.out.println(prefs.getString("radar_token", null));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        return observable;
    }

    public static void signOut(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);
        prefs.edit().remove("radar_token").remove("radar_userID").commit();
    }

}
