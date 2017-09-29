package radar.radar.Services;


import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.PostLocation;
import radar.radar.Models.Responses.GetLocationResponse;
import radar.radar.Models.Responses.UpdateLocationResponse;

public class LocationService {
    LocationApi locationApi;
    Context context;
    int userID;
    int queryUserID;
    String token;

    public LocationService(LocationApi locationApi, Context context){
        this.context = context;
        this.locationApi = locationApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);

    }

    /**
     * Updates the location of a user to the server
     * @param lat Latitude
     * @param lon Longitude
     * @param accuracy Relative reported GPS accuracy on device
     * @param heading Relative heading reported on device
     * @return response from the API server
     */

    public Observable<UpdateLocationResponse> updateLocation(int userID, float lat, float lon, float accuracy, float heading) {
        Observable<UpdateLocationResponse> observable = locationApi.updateLocation(userID, token,
                                                                new PostLocation(lat, lon, accuracy, heading))
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread());

        return observable;


    }

    /**
     * Gets location of other users with location data on the server
     * @param queryUserID the user which location needs to be queried
     * @return response from the API server
     */

    public Observable<GetLocationResponse> getLocation(int queryUserID) {
        Observable<GetLocationResponse> observable = locationApi.getLocation(queryUserID, token)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread());

        return observable;

    }


}
