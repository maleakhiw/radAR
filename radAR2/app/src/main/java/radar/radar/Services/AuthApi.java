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
 * Created by kenneth on 17/9/17.
 */

public interface AuthApi {
    @POST("auth")
    Observable<AuthResponse> signUp(@Body SignUpRequest body);

    @GET("auth/{username}")
    Observable<AuthResponse> login(@Path(value = "username", encoded = true) String username, @Query("password") String password);
}
