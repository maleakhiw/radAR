package radar.radar.Services;

import radar.radar.Models.Domain.GroupDetails;
import radar.radar.Models.Requests.RMSGetGroupInfoRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface GroupManagementSystemApi {
    @POST("getGroupInfo")
    Observable<GroupDetails> getGroupInfo(@Body RMSGetGroupInfoRequest body);
}
