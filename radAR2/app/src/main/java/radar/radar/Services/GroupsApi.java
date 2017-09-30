package radar.radar.Services;


import io.reactivex.Observable;
import radar.radar.Models.Requests.NewGroupBody;
import radar.radar.Models.Responses.GroupsResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupsApi {
    @POST("accounts/{userID}/groups")
    public Observable<GroupsResponse> newGroup(@Path(value="userID", encoded=true) int userID, @Body NewGroupBody body, @Header("token") String token);

    @GET("accounts/{userID}/groups/{groupID}")
    public Observable<GroupsResponse> getGroup(@Path(value="userID", encoded=true) int userID, @Path(value="groupID", encoded=true) int groupID, @Header("token") String token);
}
