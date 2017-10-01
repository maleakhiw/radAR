package radar.radar.Presenters;



import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

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
        System.out.println("location and device data");
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

        // TODO remove
        userLocations = new ArrayList<>();
        // for now, return fake data

        UserLocation userLocation1 = new UserLocation(1, 37.7978317f,144.9604245f, 0.1f, 2, new Date());
        userLocations.add(userLocation1);
        arView.inflateARAnnotation(userLocation1);

//        arView.setAnnotationOffsets(1, 200, 16);
        arView.setAnnotationMainText(1, "University of Melbourne");

        // to remove an annotation, call ARView.removeAnnotationById
    }

    public void updateData(double hPixelsPerDegree, double vPixelsPerDegree) {
        System.out.println("updateData");
        // update number of pixels per degree
        locationTransformations.sethPixelsPerDegree(hPixelsPerDegree);
        locationTransformations.setvPixelsPerDegree(vPixelsPerDegree);

        Observable<Float> azimuthObservable = sensorService.azimuthUpdates.map(x -> (float) (double) x);
        Observable<Float> pitchObservable = sensorService.azimuthUpdates.map(x -> (float) (double) x);

        Observable<Location> locationObservable = locationService.getLocationUpdates(5000, 1000, LocationRequest.PRIORITY_HIGH_ACCURACY);
//        azimuthObservable.subscribe(azimuth -> System.out.println("azimuth"));
//        pitchObservable.subscribe(pitch -> System.out.println("pitch"));
//        locationObservable.subscribe(location -> System.out.println("location"));

        Observable.combineLatest(azimuthObservable, pitchObservable, locationObservable, LocationAndDeviceData::new).subscribe(new Observer<LocationAndDeviceData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LocationAndDeviceData locationAndDeviceData) {
                // TODO later refactor to List
                Log.d("locationAndDeviceData", locationAndDeviceData.toString());
                float azimuth = locationAndDeviceData.azimuth;
                float pitch = locationAndDeviceData.pitch;
                Location location = locationAndDeviceData.location;
                float latUser = (float) location.getLatitude();
                float lonUser = (float) location.getLongitude();

                UserLocation unimelb = userLocations.get(0);
                float latUnimelb = unimelb.getLat();
                float lonUnimelb = unimelb.getLon();

                double bearing = locationTransformations.bearingBetween(latUser, lonUser, latUnimelb, lonUnimelb);

                // get xOffset and yOffset
                int xOffset = locationTransformations.xOffset(bearing, azimuth);
                int yOffset = locationTransformations.yOffset(pitch, 0);

                arView.setAnnotationOffsets(1, xOffset, yOffset);
            }

            @Override
            public void onError(Throwable e) {
                // TODO check for the GRANT_LOCATION_PERMISSIONS
                if (e.getMessage().equals("GRANT_ACCESS_FINE_LOCATION")) {
                    arView.requestLocationPermissions();
                }
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
