package radar.radar.Presenters;



import android.hardware.SensorManager;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.MeetingPoint;
import radar.radar.Models.Responses.GroupLocationsInfo;
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
    GroupLocationsInfo groupLocationDetails;

    public LocationAndDeviceData(float azimuth, float pitch, Location location, GroupLocationsInfo groupLocationDetails) {
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

    Observable<GroupLocationsInfo> groupMemberLocationsObservable;

    int activeAnnotationUserID = -1; // TODO remove magic number

    public ARPresenter(ARView arView, LocationService locationService, GroupsService groupsService, SensorManager sensorManager, LocationTransformations locationTransformations, int groupID) {
        this.arView = arView;
        this.locationService = locationService;
        this.groupsService = groupsService;
        this.sensorService = new SensorService(sensorManager);
        this.locationTransformations = locationTransformations;

        // TODO warn if no location in 5sec
        groupMemberLocationsObservable = locationService.getGroupLocationInfo(groupID, 1000);

//        destinationLocation = new UserLocation(79, -37.829293f, 144.956805f, 0.1f, 2, new Date());
//        arView.inflateARAnnotation(destinationLocation);
//        arView.setAnnotationMainText(79, "Southbank");
//        arView.updateDestinationName("Southbank");

    }

    void render(int userID, double latUser, double lonUser, UserLocation userLocation, double azimuth, double pitch) {
        double bearing = LocationTransformations.bearingBetween(latUser, lonUser, userLocation.getLat(), userLocation.getLon());

        // get xOffset and yOffset
        int xOffset = locationTransformations.xOffset(bearing, azimuth);
//        System.out.println(((Float) azimuth).toString() + ": " + ((Integer) xOffset).toString());
        int yOffset = locationTransformations.yOffset(pitch, 0);

        arView.setAnnotationOffsets(userID, xOffset, yOffset);  // TODO make a class to hold the offsets - check for overlaps, etc.
    }

    Disposable locationPushDisposable;
    Disposable combinedDataDisposable;

    public void setActiveAnnotation(int userID) {
        activeAnnotationUserID = userID;
    }

    private void updateDataImpl() {
        // TODO verify
        Observable<Float> azimuthObservable = sensorService.azimuthUpdates.map(x -> (float) (double) x);

        Observable<Float> pitchObservable = sensorService.pitchUpdates.map(x -> (float) (double) x);
        Observable<Location> locationObservable = locationService.getLocationUpdates(5000, 1000, LocationRequest.PRIORITY_HIGH_ACCURACY);


        // push location to server
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
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                locationPushDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {

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


        // combines the latest data from all the streams - compass, accelerometer/gyroscope, Google Maps location
        Observable.combineLatest(azimuthObservable, pitchObservable, locationObservable, groupMemberLocationsObservable, LocationAndDeviceData::new).subscribe(new Observer<LocationAndDeviceData>() {
            @Override
            public void onSubscribe(Disposable d) {
                combinedDataDisposable = d;
            }

            @Override
            public void onNext(LocationAndDeviceData locationAndDeviceData) {
                Location location = locationAndDeviceData.location;
                float latUser = (float) location.getLatitude();
                float lonUser = (float) location.getLongitude();
                float azimuth = locationAndDeviceData.azimuth;
                float pitch = locationAndDeviceData.pitch;


                // meeting point
                MeetingPoint meetingPoint = locationAndDeviceData.groupLocationDetails.meetingPoint;

                // group members locations
                ArrayList<UserLocation> userLocations = locationAndDeviceData.groupLocationDetails.locations;
                HashMap<Integer, User> userDetails = locationAndDeviceData.groupLocationDetails.userDetails;
                HashMap<Integer, UserLocation> userLocationsMap = new HashMap<>();

                for (UserLocation userLocation: userLocations) {
                    int userID = userLocation.getUserID();
                    userLocationsMap.put(userID, userLocation);

                    arView.setAnnotationMainText(userID, userDetails.get(userID).firstName);
                    if (!arView.isInflated(userID)) {
                        // inflate if not inflated
                        arView.inflateARAnnotation(userLocation);
                        arView.setAnnotationMainText(userID, userDetails.get(userID).firstName);
                    }
                    render(userID, latUser, lonUser, userLocation, azimuth, pitch);
                }

                // render destination location, userID -1
                if (meetingPoint != null) {
                    // TODO new class
                    UserLocation destination = new UserLocation(-1, (float) meetingPoint.lat, (float) meetingPoint.lon, 0, 0, meetingPoint.timeAdded);
                    if (!arView.isInflated(-1)) {
                        arView.inflateARAnnotation(destination);
                    }
                    arView.setAnnotationMainText(-1, meetingPoint.name);
                    render(-1, latUser, lonUser, destination, azimuth, pitch);
                }

                /* render HUD */
                UserLocation destination;
                if (activeAnnotationUserID == -1) {
                    destination = new UserLocation(-1, (float) meetingPoint.lat, (float) meetingPoint.lon, 0, 0, meetingPoint.timeAdded);
                    arView.updateDestinationName(meetingPoint.name);
                } else {
                    destination = userLocationsMap.get(activeAnnotationUserID);
                    arView.updateDestinationName(userDetails.get(activeAnnotationUserID).firstName);
                }
                double bearingToDest = LocationTransformations.bearingBetween(latUser, lonUser, destination.getLat(), destination.getLon());
                arView.updateDistanceToDestination(LocationTransformations.distance(latUser, lonUser, destination.getLat(), destination.getLon(), 'K') * 1000);
                arView.updateRelativeDestinationPosition(LocationTransformations.getDeltaAngleCompassDirection(bearingToDest, azimuth));

                // update heading
                arView.updateHUDHeading(LocationTransformations.getCompassDirection(azimuth));

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }



    public void updateData(double hPixelsPerDegree, double vPixelsPerDegree) {
        System.out.println("presenter updateData");
        // update number of pixels per degree
        locationTransformations.sethPixelsPerDegree(hPixelsPerDegree);
        locationTransformations.setvPixelsPerDegree(vPixelsPerDegree);

        updateDataImpl();
    }

    // null checks since this function will be called on Activity creation too
    // when the Service has not been created yet.

    public void onStop() {
        if (sensorService != null) {
            sensorService.unregisterSensorEventListener();
        }
        boolean tracking = false;    // TODO move out

        if (!tracking) {    // do not send location data in the background
            locationPushDisposable.dispose();
        }

        if (combinedDataDisposable != null) {
            combinedDataDisposable.dispose();
        }
    }

    public void onStart() {
        if (sensorService != null) {
            sensorService.reregisterSensorEventListener();
        }

        updateDataImpl();
    }
}
