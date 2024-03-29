package radar.radar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import radar.radar.Listeners.LocationCallbackProvider;
import radar.radar.Listeners.LocationUpdateListener;
import radar.radar.Presenters.HomeScreenPresenter;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import radar.radar.Views.HomeScreenView;
import retrofit2.Retrofit;

public class HomeScreenActivity extends AppCompatActivity implements OnMapReadyCallback, HomeScreenView, LocationCallbackProvider {

    private static final String TAG = "Home Screen Activity";
    private static final float DEFAULT_ZOOM = 16;
    private static final int REQUEST_FOR_LOCATION = 1;

    private Place meetingPoint = null;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private HomeScreenPresenter presenter;

    NavigationActivityHelper helper;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // set up navigation bar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        ImageView image = navigationView.getHeaderView(0).findViewById(R.id.profile_picture);

        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, image, this);

        // set up retrofit
        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

        // set up locationApi
        LocationApi locationApi = retrofit.create(LocationApi.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationService locationService = new LocationService(locationApi, this, fusedLocationClient);
        presenter = new HomeScreenPresenter(this, locationService);

        // set up mapView
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_screen_map);
        mapFragment.getMapAsync(this);

        // set up place autocomplete
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // Get info about the selected place.
                meetingPoint = place;
                googleMap.clear();
                Log.i(TAG, "Place: " + place.getName());
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .title((String) place.getName()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
                // handle marker click event
                googleMap.setOnMarkerClickListener(marker -> {
                    Log.i(TAG, "Successful click ");
                    Intent intent = new Intent(getApplicationContext(), NewGroupActivity.class);
                    intent.putExtra("status", "successful");
                    if (meetingPoint != null) {
                        intent.putExtra("name", meetingPoint.getName().toString());
                        intent.putExtra("lat", meetingPoint.getLatLng().latitude);
                        intent.putExtra("lng", meetingPoint.getLatLng().longitude);
                    }
                    startActivity(intent);
                    return true;
                });
            }

            @Override
            public void onError(Status status) {
                // Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // set up floating action button
        FloatingActionButton fab_current_loc = (FloatingActionButton) findViewById(R.id.fab_current_loc);
        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fab_remove = (FloatingActionButton) findViewById(R.id.fab_remove);
        FloatingActionButton fab_new_friend = (FloatingActionButton) findViewById(R.id.fab_new_friend);
        FloatingActionButton fab_new_group = (FloatingActionButton) findViewById(R.id.fab_new_group);

        TextView text_new_friend = (TextView) findViewById(R.id.text_new_friend);
        TextView text_new_group = (TextView) findViewById(R.id.text_new_group);

        // set up floating action button (fab_current, fab_add, etc.) behaviour
        fab_current_loc.setOnClickListener(v -> {
            // FAB Action
            presenter.jumpToCurrentLocation();
        });

        fab_add.setOnClickListener(v -> {
            // FAB Action
            fab_add.setVisibility(View.INVISIBLE);
            fab_remove.setVisibility(View.VISIBLE);
            fab_new_friend.setVisibility(View.VISIBLE);
            text_new_friend.setVisibility(View.VISIBLE);
            fab_new_group.setVisibility(View.VISIBLE);
            text_new_group.setVisibility(View.VISIBLE);
        });

        fab_remove.setOnClickListener(v -> {
            fab_add.setVisibility(View.VISIBLE);
            fab_remove.setVisibility(View.INVISIBLE);
            fab_new_friend.setVisibility(View.INVISIBLE);
            text_new_friend.setVisibility(View.INVISIBLE);
            fab_new_group.setVisibility(View.INVISIBLE);
            text_new_group.setVisibility(View.INVISIBLE);
        });


        fab_new_friend.setOnClickListener(v -> {
            // FAB Action
            Intent intent = new Intent(getApplicationContext(), FriendRequestActivity.class);
            startActivity(intent);
        });

        fab_new_group.setOnClickListener(v -> {
            // FAB Action
            Intent intent = new Intent(getApplicationContext(), NewGroupActivity.class);
            intent.putExtra("status", "successful");
            if (meetingPoint != null) {
                intent.putExtra("name", meetingPoint.getName().toString());
                intent.putExtra("lat", meetingPoint.getLatLng().latitude);
                intent.putExtra("lng", meetingPoint.getLatLng().longitude);
            }
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        presenter.onMapReady(googleMap);
    }


    @Override
    public String getStringRes(int resourceID) {
        return getString(resourceID);
    }


    @Override
    public int getColorRes(int resourceID) {
        return ContextCompat.getColor(this, resourceID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_FOR_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.onMapReady(googleMap);
            } else {
                // TODO display to user
            }
        }
    }

    @Override
    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FOR_LOCATION);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        presenter.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.getMapAsync(this); // reload map

        // might have updated profile
        helper.updateDisplay();

        presenter.onStart();
    }

    @Override
    public LocationCallback getLocationCallback(LocationUpdateListener locationUpdateListener) {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                for (Location location : result.getLocations()) {
                    locationUpdateListener.onLocationUpdate(location);
                }
            }
        };
        return locationCallback;
    }
}

