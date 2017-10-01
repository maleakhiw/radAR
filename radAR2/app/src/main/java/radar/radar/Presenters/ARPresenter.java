package radar.radar.Presenters;



import android.hardware.SensorManager;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.UserLocation;
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

    public LocationAndDeviceData(float azimuth, float pitch, Location location) {
        this.pitch = pitch;
        this.azimuth = azimuth;
        this.location = location;
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
    SensorService sensorService;
    LocationTransformations locationTransformations;
    // mock
    ArrayList<UserLocation> userLocations;  // will be part of the Observable later on

                                            // mock for testing

    public ARPresenter(ARView arView, LocationService locationService, SensorManager sensorManager, LocationTransformations locationTransformations) {
        this.arView = arView;
        this.locationService = locationService;
        this.sensorService = new SensorService(sensorManager);
        this.locationTransformations = locationTransformations;

        // TODO warn if no location in 5sec

        // TODO remove
        userLocations = new ArrayList<>();
        // for now, return fake data

        UserLocation userLocation1 = new UserLocation(1, -37.797639f, 144.958405f, 0.1f, 2, new Date());
        userLocations.add(userLocation1);
        arView.inflateARAnnotation(userLocation1);

        UserLocation userLocation2 = new UserLocation(2, -37.829293f, 144.956805f, 0.1f, 2, new Date());
        userLocations.add(userLocation2);
        arView.inflateARAnnotation(userLocation2);

        arView.setAnnotationMainText(1, "MCM");
        arView.setAnnotationMainText(2, "Southbank");

        // TODO remove hardcode on destination
        arView.updateDestinationName("MCM");

        // to remove an annotation, call ARView.removeAnnotationById
    }

    public void updateData(double hPixelsPerDegree, double vPixelsPerDegree) {
        System.out.println("updateData");
        // update number of pixels per degree
        locationTransformations.sethPixelsPerDegree(hPixelsPerDegree);
        locationTransformations.setvPixelsPerDegree(vPixelsPerDegree);

        Observable<Float> azimuthObservable = sensorService.azimuthUpdates.map(x -> (float) (double) x);
        Observable<Float> pitchObservable = sensorService.pitchUpdates.map(x -> (float) (double) x);

        //        Observable.combineLatest(azimuthObservable, pitchObservable, (azimuth, pitch) ->
//            ((Float) azimuth).toString() + ", " + ((Float) pitch).toString()
//        ).subscribe(text -> System.out.println(text));


        Observable<Location> locationObservable = locationService.getLocationUpdates(5000, 1000, LocationRequest.PRIORITY_HIGH_ACCURACY);
//        azimuthObservable.subscribe(azimuth -> System.out.println("azimuth"));
//        pitchObservable.subscribe(pitch -> System.out.println("pitch"));
//        locationObservable.subscribe(location -> System.out.println("location"));

//        locationObservable.take(1).subscribe(location -> System.out.println(location));

        Observable.combineLatest(azimuthObservable, pitchObservable, locationObservable, LocationAndDeviceData::new).subscribe(new Observer<LocationAndDeviceData>() {
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

                // update distance to destination
                // TODO: hardcoded destination
                UserLocation destination = userLocations.get(0);
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

                for (UserLocation userLocation: userLocations) {
                    int userID = userLocation.getUserID();
                    double bearing = LocationTransformations.bearingBetween(latUser, lonUser, userLocation.getLat(), userLocation.getLon());

                    // get xOffset and yOffset
                    int xOffset = locationTransformations.xOffset(bearing, azimuth);
//                    System.out.println(((Float) azimuth).toString() + ": " + ((Integer) xOffset).toString());
                    int yOffset = locationTransformations.yOffset(pitch, 0);

                    arView.setAnnotationOffsets(userID, xOffset, yOffset);  // TODO make a class to hold the offsets too
                                                                            // so we can check if they are overlapping
                }

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
