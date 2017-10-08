package radar.radar.Presenters;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.reactivex.disposables.Disposable;
import radar.radar.Listeners.LocationCallbackProvider;
import radar.radar.R;
import radar.radar.Services.LocationService;
import radar.radar.Views.HomeScreenView;

/**
 * Created by kenneth on 8/10/17.
 */
public class HomeScreenPresenter {
    HomeScreenView homeScreenView;
    LocationService locationService;

    GoogleMap googleMap;

    Disposable locationServiceDisposable;

    LocationCallback locationCallback;

    private boolean first = true;


    public HomeScreenPresenter(HomeScreenView homeScreenView, LocationService locationService) {
        this.homeScreenView = homeScreenView;
        this.locationService = locationService;
        // NOTE locationCallback just has to be instantiated in constructor! Moving it to method call
        // makes it unable to be unregistered.
        locationCallback = ((LocationCallbackProvider) homeScreenView).getLocationCallback(location -> {
            System.out.println(location.getLatitude());
            System.out.println(location.getLongitude());

            googleMap.clear();

            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addCircle(new CircleOptions()
                    .center(current)
                    .strokeColor(homeScreenView.getColorRes(R.color.colorPrimary))
                    .radius(location.getAccuracy()));

            googleMap.addCircle(new CircleOptions()
                    .center(current)
                    .fillColor(homeScreenView.getColorRes(R.color.colorPrimaryDark))
                    .strokeColor(homeScreenView.getColorRes(R.color.colorPrimaryDark))
                    .radius(1));

            // Add a marker in Melbourne Uni and move the camera
            double unimelb_lat = Double.parseDouble(homeScreenView.getStringRes(R.string.melbourne_university_lat));
            double unimelb_lng = Double.parseDouble(homeScreenView.getStringRes(R.string.melbourne_university_lng));

            LatLng melbourne_university = new LatLng(unimelb_lat, unimelb_lng);
            googleMap.addMarker(new MarkerOptions().position(melbourne_university)
                    .title(homeScreenView.getStringRes(R.string.unimelb)));

            if (first) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                first = false;
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationUpdates();
    }

    public void onStop() {
        // don't need to do anything
    }

    public void onStart() {
        if (googleMap != null) {
            locationUpdates();
        }
    }



    public void locationUpdates() {
        try {
            locationService.getLocationUpdates(10000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY, locationCallback);
        } catch (SecurityException e) {
            homeScreenView.requestLocationPermissions();
        }
    }

}
