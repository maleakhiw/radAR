package radar.radar.Services;

import radar.radar.Models.Domain.GroupDetails;
import radar.radar.Models.Requests.RMSGetGroupInfoRequest;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kenneth on 6/9/17.
 */

public class TrackingGroupInteractorImpl implements TrackingGroupInteractor {
    public Observable<GroupDetails> getGroupDetails(int userID, int queryUserID, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/GMS/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        GroupManagementSystemApi gmsApi = retrofit.create(GroupManagementSystemApi.class);

        return gmsApi.getGroupInfo(new RMSGetGroupInfoRequest(1, 1, 1, "79"));    // TODO: replace with actual params
    }

    @Override
    public Observable<GroupDetails> getGroupDetails(String username, int queryUserID, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/GMS/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        GroupManagementSystemApi gmsApi = retrofit.create(GroupManagementSystemApi.class);

        return gmsApi.getGroupInfo(new RMSGetGroupInfoRequest("username", 1, 1, "79"));    // TODO: replace with actual params
    }
}
