package radar.radar.Presenters;



import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import radar.radar.CameraData;
import radar.radar.DataForPresenter;
import radar.radar.Models.UserLocation;
import radar.radar.Services.LocationService;
import radar.radar.Services.LocationTransformations;
import radar.radar.Views.ARView;

/**
 * Created by kenneth on 28/9/17.
 */

class AzimuthLocation {
    float azimuth;
    Location location;

    public AzimuthLocation(float azimuth, Location location) {
        this.azimuth = azimuth;
        this.location = location;
    }

    @Override
    public String toString() {
        return ((Float) azimuth).toString() + ", " + location.toString();
    }
}

public class ARPresenter {
    ARView arView;
    LocationService locationService;

    ArrayList<UserLocation> userLocations;  // will be part of the Observable later on
                                            // mock for testing

    LocationTransformations locationTransformations;

    public ARPresenter(ARView arView, LocationService locationService) {
        this.arView = arView;
        this.locationService = locationService;
    }

    public void loadData() {
        userLocations = new ArrayList<>();
        // for now, return fake data

        UserLocation userLocation1 = new UserLocation(1, 37.7978317f,144.9604245f, 0.1f, 2, new Date());
        userLocations.add(userLocation1);
        arView.inflateARAnnotation(userLocation1);

        arView.setAnnotationOffsets(1, 200, 16);
        arView.setAnnotationMainText(1, "University of Melbourne");

        // to remove an annotation, call ARView.removeAnnotationById
    }


    boolean called = false;

    Observable<AzimuthLocation> azimuthLocationObservable;

    public void updateFovs(DataForPresenter dataForPresenter) {
        System.out.println("updateFovs");

        if (locationTransformations == null) {
            locationTransformations = new LocationTransformations(dataForPresenter.hPixelsPerDegree, dataForPresenter.vPixelsPerDegree);
        } else {
            locationTransformations.sethPixelsPerDegree(dataForPresenter.hPixelsPerDegree);
            locationTransformations.setvPixelsPerDegree(dataForPresenter.vPixelsPerDegree);
        }

        Observable<Float> azimuthObservable = arView.getAzimuthObservable();
        Observable<Location> locationObservable = locationService.getLocationUpdates(5000, 1000, LocationRequest.PRIORITY_HIGH_ACCURACY);
        azimuthLocationObservable = Observable.combineLatest(azimuthObservable, locationObservable, (azimuth, location) -> {
            AzimuthLocation azimuthLocation = new AzimuthLocation(azimuth, location);
            return azimuthLocation;
        });


        azimuthLocationObservable.subscribe(new Observer<AzimuthLocation>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(AzimuthLocation azimuthLocation) {

                // TODO later refactor to List
                Log.d("azimuthLocation", azimuthLocation.toString());
                float azimuth = azimuthLocation.azimuth;
                Location location = azimuthLocation.location;
                float latUser = (float) location.getLatitude();
                float lonUser = (float) location.getLongitude();

                UserLocation unimelb = userLocations.get(0);
                float latUnimelb = unimelb.getLat();
                float lonUnimelb = unimelb.getLon();

                double bearing = locationTransformations.bearingBetween(latUser, lonUser, latUnimelb, lonUnimelb);

                // get xOffset and yOffset
                int xOffset = locationTransformations.xOffset(bearing, azimuth);
                // TODO yOffset

                arView.setAnnotationOffsets(1, xOffset, 0);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
}
