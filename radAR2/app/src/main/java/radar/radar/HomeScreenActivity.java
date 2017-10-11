package radar.radar;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import radar.radar.Listeners.LocationCallbackProvider;
import radar.radar.Listeners.LocationUpdateListener;
import radar.radar.Presenters.HomeScreenPresenter;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import radar.radar.Views.HomeScreenView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenActivity extends AppCompatActivity implements OnMapReadyCallback, HomeScreenView, LocationCallbackProvider {

    NavigationActivityHelper helper;

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    private HomeScreenPresenter presenter;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    static final int REQUEST_FOR_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        TextView name = navigationView.getHeaderView(0).findViewById(R.id.nav_header_name);
        TextView email = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);

        helper = new NavigationActivityHelper(navigationView, drawerLayout, toolbar, name, email, this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        LocationApi locationApi = retrofit.create(LocationApi.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationService locationService = new LocationService(locationApi, this, fusedLocationClient);

        presenter = new HomeScreenPresenter(this, locationService);

        // set up mapView
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_screen_map);
        mapFragment.getMapAsync(this);

        //BUTTON

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

