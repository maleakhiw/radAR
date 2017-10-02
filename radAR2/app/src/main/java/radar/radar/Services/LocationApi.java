package radar.radar.Services;


import io.reactivex.Observable;
import radar.radar.Models.Requests.UpdateLocationRequest;
import radar.radar.Models.Responses.GetLocationResponse;
import radar.radar.Models.Responses.GroupLocationsInfo;
import radar.radar.Models.Responses.UpdateLocationResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LocationApi {
    @POST("accounts/{userID}/location")
    Observable<UpdateLocationResponse> updateLocation(@Path(value = "userID", encoded=true) int userID, @Header("token") String token, @Body UpdateLocationRequest body);

    @GET("users/{queryUserID}/location")
    Observable<GetLocationResponse> getLocation(@Path(value = "queryUserID", encoded=true) int queryUserID, @Query("userID") int yourUserID, @Header("token") String token);

    @GET("accounts/{userID}/groups/{groupID}/locations")
    Observable<GroupLocationsInfo> getGroupLocations(@Path(value="userID", encoded=true) int userID, @Path(value="groupID", encoded=true) int groupID, @Header("token") String token);

}
