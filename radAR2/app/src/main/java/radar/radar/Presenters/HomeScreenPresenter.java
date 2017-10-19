package radar.radar.Presenters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.reactivex.disposables.Disposable;
import radar.radar.Listeners.LocationCallbackProvider;
import radar.radar.NewGroupActivity;
import radar.radar.Services.LocationService;
import radar.radar.Views.HomeScreenView;

import static android.content.ContentValues.TAG;


public class HomeScreenPresenter {
    private static final float DEFAULT_ZOOM = 15;
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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, DEFAULT_ZOOM));
                googleMap.setMyLocationEnabled(true);
                first = false;
            }
        });

        locationUpdates();
    }

    public void jumpToCurrentLocation() {
        if (current != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, DEFAULT_ZOOM));
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

    public void setUpAutoCompleteFragment(PlaceAutocompleteFragment autocompleteFragment, Context context) {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // Get info about the selected place.
                googleMap.clear();
                Log.i(TAG, "Place: " + place.getName());
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .title((String) place.getName()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
                // handle marker click event
                googleMap.setOnMarkerClickListener(marker -> {
                    Log.i(TAG, "Successful click ");
                    Intent intent = new Intent(context, NewGroupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("status", "successful");
                    context.startActivity(intent);
                    return true;
                });
            }

            @Override
            public void onError(Status status) {
                // Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

}
