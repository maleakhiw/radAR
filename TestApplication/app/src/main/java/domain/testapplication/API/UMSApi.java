package domain.testapplication.API;

import domain.testapplication.models.GetFriendsRequest;
import domain.testapplication.models.GetFriendsResponse;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by keyst on 8/09/2017.
 */

public interface UMSApi {

    @POST("getFriends")
    Observable<GetFriendsResponse > getFriends(@Body GetFriendsRequest getFriendsRequest);
}
