package radar.radar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

    private Location currentLocation;
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

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        ImageView image = navigationView.getHeaderView(0).findViewById(R.id.profile_picture);

        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, image, this);

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

        LocationApi locationApi = retrofit.create(LocationApi.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationService locationService = new LocationService(locationApi, this, fusedLocationClient);

        presenter = new HomeScreenPresenter(this, locationService);

        getDeviceLocation();

        // set up mapView
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_screen_map);
        mapFragment.getMapAsync(this);

        // set up place autocomplete
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                // TODO New Activity to create a new Group with that meeting point.
                googleMap.clear();
                Log.i(TAG, "Place: " + place.getName());
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng())
                        .title((String) place.getName()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),DEFAULT_ZOOM));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
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

        // set up floating action button behaviour
        fab_current_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB Action
                getDeviceLocation();
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB Action
                fab_add.setVisibility(View.INVISIBLE);
                fab_remove.setVisibility(View.VISIBLE);
                fab_new_friend.setVisibility(View.VISIBLE);
                text_new_friend.setVisibility(View.VISIBLE);
                fab_new_group.setVisibility(View.VISIBLE);
                text_new_group.setVisibility(View.VISIBLE);
            }
        });

        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB Action
                fab_add.setVisibility(View.VISIBLE);
                fab_remove.setVisibility(View.INVISIBLE);
                fab_new_friend.setVisibility(View.INVISIBLE);
                text_new_friend.setVisibility(View.INVISIBLE);
                fab_new_group.setVisibility(View.INVISIBLE);
                text_new_group.setVisibility(View.INVISIBLE);
            }
        });


        fab_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB Action
                Intent intent = new Intent(getApplicationContext(), FriendRequestActivity.class);
                startActivity(intent);
            }
        });

        fab_new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB Action
                Intent intent = new Intent(getApplicationContext(), NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        presenter.onMapReady(googleMap);
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
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

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            Task locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        currentLocation = (Location) task.getResult();

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()))
                                .zoom(DEFAULT_ZOOM).build();
                        googleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));

                        googleMap.setMyLocationEnabled(true);


                        if (currentLocation != null) {
                            googleMap.clear();
                            // TODO do not remove PoI
                            LatLng currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

//                            googleMap.addCircle(new CircleOptions()
//                                    .center(currentPosition)
//                                    .strokeColor(getColorRes(R.color.colorPrimary))
//                                    .radius(currentLocation.getAccuracy()));
//                            googleMap.addCircle(new CircleOptions()
//                                    .center(currentPosition)
//                                    .fillColor(getColorRes(R.color.colorPrimaryDark))
//                                    .strokeColor(getColorRes(R.color.colorPrimaryDark))
//                                    .radius(1));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentPosition, DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}

