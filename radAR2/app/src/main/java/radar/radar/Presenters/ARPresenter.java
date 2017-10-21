package radar.radar.Presenters;

import android.hardware.SensorManager;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Domain.DestinationLocation;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Domain.LocationAndDeviceData;
import radar.radar.Models.Responses.GroupLocationsInfo;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Domain.UserLocation;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationService;
import radar.radar.Services.LocationTransformations;
import radar.radar.AnnotationRenderer;
import radar.radar.Services.SensorService;
import radar.radar.Views.ARView;

/**
 * This class encapsulates the logic of AR activity of the application
 */
public class ARPresenter {
    private ARView arView;

    // services (part of model)
    private LocationService locationService;
    private GroupsService groupsService;
    private SensorService sensorService;

    private LocationTransformations locationTransformations;

    /* RxJava */
    private Observable<GroupLocationsInfo> groupMemberLocationsObservable;
    private Disposable locationPushDisposable;
    private Disposable combinedDataDisposable;

    public static final int DESTINATION_ID = -1;

    private int activeAnnotationUserID = DESTINATION_ID;

    public ARPresenter(ARView arView, LocationService locationService, GroupsService groupsService, SensorManager sensorManager, LocationTransformations locationTransformations, int groupID) {
        this.arView = arView;
        this.locationService = locationService;
        this.groupsService = groupsService;
        this.sensorService = new SensorService(sensorManager);
        this.locationTransformations = locationTransformations;

        groupMemberLocationsObservable = locationService.getGroupLocationInfo(groupID, 1000);
    }

    void renderDestination(double latUser, double lonUser, UserLocation userLocation, double azimuth, double pitch) {
        int userID = -1;
        double bearing = LocationTransformations.bearingBetween(latUser, lonUser, userLocation.getLat(), userLocation.getLon());

        // get xOffset and yOffset
        int xOffset = locationTransformations.xOffset(bearing, azimuth);
        int yOffset = locationTransformations.yOffset(pitch, 0);
        int height = arView.getAnnotationHeight(userID);
        int width = arView.getAnnotationWidth(userID);

        arView.setAnnotationOffsets(userID, xOffset, yOffset);  // TODO make a class to hold the offsets - check for overlaps, etc.
    }

    public void setActiveAnnotation(int userID) {
        activeAnnotationUserID = userID;
    }

    void renderDestinationLocation(MeetingPoint meetingPoint, double latUser, double lonUser, double azimuth, double pitch) {
        // render destination location, userID DESTINATION_ID
        // TODO new class
        UserLocation destination = new UserLocation(DESTINATION_ID, (float) meetingPoint.lat, (float) meetingPoint.lon, 0, 0, meetingPoint.timeAdded);
        if (!arView.isInflated(DESTINATION_ID)) {
            arView.inflateARAnnotation(destination);
        }
        arView.setAnnotationMainText(DESTINATION_ID, meetingPoint.name);
        renderDestination(latUser, lonUser, destination, azimuth, pitch);
    }

    public void updateLocationTransformations() {
        Observable<Double> azimuthObservable = sensorService.azimuthUpdates;
        Observable<Double> pitchObservable = sensorService.pitchUpdates;
        Observable<Location> locationObservable = locationService.getLocationUpdates(5000, 1000, LocationRequest.PRIORITY_HIGH_ACCURACY);

        // push location to server
        Observable.zip(azimuthObservable, locationObservable, (azimuth, location) -> {
//            System.out.println(azimuth.toString() + " " + location.toString());
            locationService.updateLocation((float) location.getLatitude(), (float) location.getLongitude(), location.getAccuracy(), azimuth)
                    .subscribe(response -> {}, error -> {});
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
                double latCurrent = location.getLatitude();
                double lonCurrent = location.getLongitude();
                double azimuth = locationAndDeviceData.azimuth;
                double pitch = locationAndDeviceData.pitch;


                // meeting point
                MeetingPoint meetingPoint = locationAndDeviceData.groupLocationDetails.meetingPoint;

                // group members locations
                ArrayList<UserLocation> userLocations = new ArrayList<>();
                HashMap<Integer, User> usersDetails = locationAndDeviceData.groupLocationDetails.userDetails;
                HashMap<Integer, UserLocation> userLocationsMap = new HashMap<>();

                if (meetingPoint != null) {
                    userLocations.add(new DestinationLocation(DESTINATION_ID, (float) meetingPoint.lat, (float) meetingPoint.lon, 0, 0, meetingPoint.timeAdded, meetingPoint.name));
                }
                for (UserLocation userLocation: locationAndDeviceData.groupLocationDetails.locations) {
                    userLocations.add(userLocation);
                }

                for (UserLocation annotationLatLon: userLocations) {
                    int userID = annotationLatLon.getUserID();
                    userLocationsMap.put(userID, annotationLatLon);
                }

                AnnotationRenderer renderer = new AnnotationRenderer(latCurrent, lonCurrent, azimuth, pitch, userLocations, usersDetails, locationTransformations, arView);

                renderer.render();

                /* render HUD */
                UserLocation destination;
                if (activeAnnotationUserID == DESTINATION_ID) {
                    destination = new UserLocation(DESTINATION_ID,
                            (float) meetingPoint.lat,
                            (float) meetingPoint.lon,
                            0,
                            0,
                            meetingPoint.timeAdded);
                    arView.updateDestinationName(meetingPoint.name);
                } else {
                    destination = userLocationsMap.get(activeAnnotationUserID);
                    arView.updateDestinationName(usersDetails.get(activeAnnotationUserID).firstName);
                }
                double bearingToDest = LocationTransformations.bearingBetween(latCurrent, lonCurrent, destination.getLat(), destination.getLon());
                arView.updateDistanceToDestination(LocationTransformations.distance(latCurrent, lonCurrent, destination.getLat(), destination.getLon(), 'K') * 1000);
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


    public void updateLocationTransformations(double hPixelsPerDegree, double vPixelsPerDegree) {
        // update number of pixels per degree
        locationTransformations.sethPixelsPerDegree(hPixelsPerDegree);
        locationTransformations.setvPixelsPerDegree(vPixelsPerDegree);

        updateLocationTransformations();
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

        updateLocationTransformations();
    }
}
