package radar.radar.Services;


import io.reactivex.Observable;
import radar.radar.Models.Requests.PostLocation;
import radar.radar.Models.Responses.GetLocationResponse;
import radar.radar.Models.Responses.UpdateLocationResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LocationApi {
    @POST("accounts/{userID}/location")
    Observable<UpdateLocationResponse> updateLocation(@Path(value = "userID", encoded=true) int userID, @Header("token") String token, @Body PostLocation body);

    @GET("users/{queryUserID}/location")
    Observable<GetLocationResponse> getLocation(@Path(value = "queryUserID", encoded=true) int queryUserID, @Header("token") String token);

}
