package radar.radar.Services;

import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Authentication API class for Retrofit that are used to handle authentication kind of activity
 * such as login and signup
 */
public interface AuthApi {
    @POST("auth")
    Observable<AuthResponse> signUp(@Body SignUpRequest body);

    @GET("auth/{username}")
    Observable<AuthResponse> login(@Path(value = "username", encoded = true) String username,
                                   @Query("password") String password);
}
