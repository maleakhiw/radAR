package radar.radar.Services;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.MeetingPoint;
import radar.radar.Models.Requests.UpdateLocationRequest;
import radar.radar.Models.Responses.GetLocationResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.GroupLocationsInfo;
import radar.radar.Models.Responses.UpdateLocationResponse;

public class LocationService {
    LocationApi locationApi;
    Context context;
    Activity activity;
    int userID;
    int queryUserID;
    String token;

    Observable<Integer> intervalObservable;

    FusedLocationProviderClient fusedLocationClient;

    public LocationService(LocationApi locationApi, Activity activity, FusedLocationProviderClient fusedLocationClient) {
        this.activity = activity;
        this.context = activity;
        this.locationApi = locationApi;
        this.fusedLocationClient = fusedLocationClient;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    /**
     * Gets the last location of the device.
     * @return Observable that emits the last location of the device
     */
    public Observable<Location> getLastLocation() {
        return Observable.create(emitter -> {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                emitter.onError(new Throwable("GRANT_ACCESS_FINE_LOCATION"));
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(activity, (location) -> {
                emitter.onNext(location);
            });
        });
    }

    public void disconnect() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    LocationCallback locationCallback;

    /**
     * Stream of Location updates (current position of the device).
     * @param interval interval between requests in ms
     * @param fastestInterval fastestInterval between requests in ms
     * @param priority priority, defined in LocationRequest
     * @return Observable of Location objects
     */
    public Observable<Location> getLocationUpdates(int interval, int fastestInterval, int priority) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(priority);

        return Observable.create(emitter -> {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                emitter.onError(new Throwable("GRANT_ACCESS_FINE_LOCATION"));
                return;
            }
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {   // includes unconsumed location updates
                        emitter.onNext(location);
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, /* looper */ null);
        });
    }

    /**
     * Updates the location of a user to the server
     * @param lat Latitude
     * @param lon Longitude
     * @param accuracy Relative reported GPS accuracy on device
     * @param heading Relative heading reported on device in degrees
     * @return response from the API server
     */

    public Observable<UpdateLocationResponse> updateLocation(float lat, float lon, float accuracy, float heading) {
        if (heading < 0) {
            heading += 360;
        }

        Observable<UpdateLocationResponse> observable = locationApi.updateLocation(userID, token,
                                                                new UpdateLocationRequest(lat, lon, accuracy, heading))
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
        Observable<GetLocationResponse> observable = locationApi.getLocation(queryUserID, userID, token)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread());

        return observable;

    }

    /**
     * Returns location info for a group.
     * @param groupID group for which location info is requested
     * @param interval time between requests in milliseconds
     * @return location info
     */
    public Observable<GroupLocationsInfo> getGroupLocationInfo(int groupID, int interval) {
        return Observable.interval(interval, TimeUnit.MILLISECONDS).switchMap(tick ->
                locationApi.getGroupLocations(userID, groupID, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
    }


}
