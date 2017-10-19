package radar.radar.Presenters;

import android.annotation.SuppressLint;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

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
    private LatLng current = null;

    @SuppressLint("MissingPermission")
    public HomeScreenPresenter(HomeScreenView homeScreenView, LocationService locationService) {
        this.homeScreenView = homeScreenView;
        this.locationService = locationService;
        // NOTE locationCallback just has to be instantiated in constructor! Moving it to method call
        // makes it unable to be unregistered.
        locationCallback = ((LocationCallbackProvider) homeScreenView).getLocationCallback(location -> {

            current = new LatLng(location.getLatitude(), location.getLongitude());
            if (first) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                googleMap.setMyLocationEnabled(true);
                first = false;
            }
        });

        locationUpdates();
    }

    public void jumpToCurrentLocation() {
        if (current != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
        }
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
            locationService.getLocationUpdates(10000, 5000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, locationCallback);
        } catch (SecurityException e) {
            homeScreenView.requestLocationPermissions();
        }
    }

    public LatLng getCurrent() {
        return current;
    }

}
