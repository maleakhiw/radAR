package radar.radar.Presenters;



import android.hardware.SensorManager;
import android.location.Location;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.MembersLocationResponse;
import radar.radar.Models.Responses.UpdateLocationResponse;
import radar.radar.Models.User;
import radar.radar.Models.UserLocation;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationService;
import radar.radar.Services.LocationTransformations;
import radar.radar.Services.SensorService;
import radar.radar.Views.ARView;

/**
 * Created by kenneth on 28/9/17.
 */

class LocationAndDeviceData {
    float azimuth;
    float pitch;
    Location location;
    MembersLocationResponse groupLocationDetails;

    public LocationAndDeviceData(float azimuth, float pitch, Location location, MembersLocationResponse groupLocationDetails) {
        this.pitch = pitch;
        this.azimuth = azimuth;
        this.location = location;
        this.groupLocationDetails = groupLocationDetails;
    }

    @Override
    public String toString() {
        return ((Float) azimuth).toString() + ", " + ((Float) pitch).toString() + ", " + location.toString();
    }
}

public class ARPresenter {
    ARView arView;

    // services (part of model)
    LocationService locationService;
    GroupsService groupsService;
    SensorService sensorService;
    LocationTransformations locationTransformations;
    // mock
    ArrayList<UserLocation> userLocations;  // will be part of the Observable later on

                                            // mock for testing

    Observable<MembersLocationResponse> groupMemberLocationsObservable;

    UserLocation destinationLocation;

    public ARPresenter(ARView arView, LocationService locationService, GroupsService groupsService, SensorManager sensorManager, LocationTransformations locationTransformations) {
        this.arView = arView;
        this.locationService = locationService;
        this.groupsService = groupsService;
        this.sensorService = new SensorService(sensorManager);
        this.locationTransformations = locationTransformations;

        // TODO warn if no location in 5sec

        userLocations = new ArrayList<>();

        // TODO safe get group

        groupMemberLocationsObservable = Observable.create(emitter -> {
            groupsService.getGroup(1)
            .switchMap(groupsResponse -> {
                ArrayList<Integer> members = groupsResponse.group.members;
                return locationService.getGroupMembersLocations(1);
            }).subscribe(new Observer<MembersLocationResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(MembersLocationResponse membersLocationResponse) {
                    emitter.onNext(membersLocationResponse);
                }

                @Override
                public void onError(Throwable e) {
                    arView.showToast("Unexpected error occurred");
                    Log.w("getGroup()", e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });;
        });


        UserLocation userLocation1 = new UserLocation(1, -37.797639f, 144.958405f, 0.1f, 2, new Date());
        userLocations.add(userLocation1);
        arView.inflateARAnnotation(userLocation1);
//
        destinationLocation = new UserLocation(79, -37.829293f, 144.956805f, 0.1f, 2, new Date());
        arView.inflateARAnnotation(destinationLocation);
        arView.setAnnotationMainText(79, "Southbank");
        arView.updateDestinationName("Southbank");

        // to remove an annotation, call ARView.removeAnnotationById
    }

    void render(int userID, double latUser, double lonUser, UserLocation userLocation, double azimuth, double pitch) {
        double bearing = LocationTransformations.bearingBetween(latUser, lonUser, userLocation.getLat(), userLocation.getLon());

        // get xOffset and yOffset
        int xOffset = locationTransformations.xOffset(bearing, azimuth);
//                    System.out.println(((Float) azimuth).toString() + ": " + ((Integer) xOffset).toString());
        int yOffset = locationTransformations.yOffset(pitch, 0);

        arView.setAnnotationOffsets(userID, xOffset, yOffset);  // TODO make a class to hold the offsets too
        // so we can check if they are overlapping
    }

    public void updateData(double hPixelsPerDegree, double vPixelsPerDegree) {
        System.out.println("updateData");
        // update number of pixels per degree
        locationTransformations.sethPixelsPerDegree(hPixelsPerDegree);
        locationTransformations.setvPixelsPerDegree(vPixelsPerDegree);

        Observable<Float> azimuthObservable = sensorService.azimuthUpdates.map(x -> (float) (double) x);
        Observable<Float> pitchObservable = sensorService.pitchUpdates.map(x -> (float) (double) x);
        Observable<Location> locationObservable = locationService.getLocationUpdates(5000, 1000, LocationRequest.PRIORITY_HIGH_ACCURACY);

        // push location to server. Unlike combineLatest, zip only emits when both Observables
        // have something ready to emit
        Observable.zip(azimuthObservable, locationObservable, (azimuth, location) -> {
            System.out.println(azimuth.toString() + " " + location.toString());
            locationService.updateLocation((float) location.getLatitude(), (float) location.getLongitude(), location.getAccuracy(), azimuth).subscribe(new Observer<UpdateLocationResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UpdateLocationResponse updateLocationResponse) {
                    System.out.println(updateLocationResponse);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
            return 0;
        }).subscribe();


        Observable.combineLatest(azimuthObservable, pitchObservable, locationObservable, groupMemberLocationsObservable, LocationAndDeviceData::new).subscribe(new Observer<LocationAndDeviceData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LocationAndDeviceData locationAndDeviceData) {
                Location location = locationAndDeviceData.location;
                float latUser = (float) location.getLatitude();
                float lonUser = (float) location.getLongitude();
                float azimuth = locationAndDeviceData.azimuth;
                float pitch = locationAndDeviceData.pitch;

                // group members locations
                ArrayList<UserLocation> userLocations = locationAndDeviceData.groupLocationDetails.locations;
                HashMap<Integer, User> userDetails = locationAndDeviceData.groupLocationDetails.userDetails;

                for (UserLocation userLocation: userLocations) {
                    int userID = userLocation.getUserID();

                    arView.setAnnotationMainText(userID, userDetails.get(userID).firstName);
                    if (!arView.isInflated(userID)) {
                        // inflate if not inflated
                        arView.inflateARAnnotation(userLocation);
                        arView.setAnnotationMainText(userID, userDetails.get(userID).firstName);
                    }
                    render(userID, latUser, lonUser, userLocation, azimuth, pitch);
                }

                // redraw destination location too!
                render(79, latUser, lonUser, destinationLocation, azimuth, pitch);

                // update distance to destination
                // TODO: hardcoded destination
                UserLocation destination = destinationLocation;
                arView.updateDistanceToDestination(LocationTransformations.distance(latUser, lonUser, destination.getLat(), destination.getLon(), 'K') * 1000);
                double bearingToDest = LocationTransformations.bearingBetween(latUser, lonUser, destination.getLat(), destination.getLon());
                arView.updateRelativeDestinationPosition(LocationTransformations.getDeltaAngleCompassDirection(bearingToDest, azimuth));

                // update heading
                double headingAzimuth = azimuth;
                if (azimuth < 0) {
                    headingAzimuth += 180;
                }
//                System.out.println(azimuth);
                arView.updateHUDHeading(LocationTransformations.getCompassDirection(headingAzimuth));


            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("GRANT_ACCESS_FINE_LOCATION")) {
                    arView.requestLocationPermissions();
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }

    // null checks since this function will be called on Activity creation too
    // when the Service has not been created yet.

    public void unregisterSensors() {
        if (sensorService != null) {
            sensorService.unregisterSensorEventListener();
        }
    }

    public void reregisterSensors() {
        if (sensorService != null) {
            sensorService.reregisterSensorEventListener();
        }
    }
}
