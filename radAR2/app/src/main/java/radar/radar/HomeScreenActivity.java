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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import radar.radar.Views.HomeScreenView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenActivity extends AppCompatActivity implements OnMapReadyCallback, HomeScreenView {

    NavigationActivityHelper helper;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private HomeScreenPresenter presenter;

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

        // set up mapView
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_screen_map);
        mapFragment.getMapAsync(this);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        LocationApi locationApi = retrofit.create(LocationApi.class);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationService locationService = new LocationService(locationApi, this, fusedLocationClient);

        presenter = new HomeScreenPresenter(this, locationService);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        presenter.onMapReady(googleMap);

//        // Add a marker in Melbourne Univeristy and move the camera
//        double unimelb_lat = Double.parseDouble(getString(R.string.melbourne_university_lat));
//        double unimelb_lng = Double.parseDouble(getString(R.string.melbourne_university_lng));
//
//        LatLng melbourne_university = new LatLng(unimelb_lat, unimelb_lng);
//        googleMap.addMarker(new MarkerOptions().position(melbourne_university)
//                .title(getString(R.string.unimelb)));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne_university, 15));
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
                presenter.onMapReady(mMap);
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
        presenter.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.getMapAsync(this); // reload map

        presenter.onStart();
    }

}

class HomeScreenPresenter {
    HomeScreenView homeScreenView;
    LocationService locationService;

    GoogleMap googleMap;

    Disposable locationServiceDisposable;

    HomeScreenPresenter(HomeScreenView homeScreenView, LocationService locationService) {
        this.homeScreenView = homeScreenView;
        this.locationService = locationService;
    }

    void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationUpdates();
    }

    void onStop() {
        System.out.println("onStop @ presenter");
        locationServiceDisposable.dispose();
        locationService.disconnect();
    }

    void onStart() {
        if (googleMap != null) {
            locationUpdates();
        }
    }

    boolean first = true;

    void locationUpdates() {
        locationService.getLocationUpdates(10000, 5000, LocationRequest.PRIORITY_HIGH_ACCURACY).subscribe(new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {
                locationServiceDisposable = d;
            }

            @Override
            public void onNext(Location location) {
                System.out.println(location.getLatitude());
                System.out.println(location.getLongitude());

                googleMap.clear();

                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addCircle(new CircleOptions()
                        .center(current)
                        .strokeColor(homeScreenView.getColorRes(R.color.colorPrimary))
                        .radius(location.getAccuracy()/2));

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
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("GRANT_ACCESS_FINE_LOCATION")) {
                    homeScreenView.requestLocationPermissions();
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
