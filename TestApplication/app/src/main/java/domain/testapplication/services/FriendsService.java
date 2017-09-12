package domain.testapplication.services;

import domain.testapplication.API.UMSApi;
import domain.testapplication.models.GetFriendsRequest;
import domain.testapplication.models.GetFriendsResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Created by keyst on 8/09/2017.
 */

public class FriendsService {
    Retrofit retrofit;
    UMSApi umsApi;

    public FriendsService(Retrofit retrofit) {
        this.retrofit = retrofit;
        this.umsApi = retrofit.create(UMSApi.class);
    }

    public Observable<GetFriendsResponse> getFriends(int userID, String token) {
        return umsApi.getFriends(new GetFriendsRequest(1, "79"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
