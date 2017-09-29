package radar.radar;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import radar.radar.Models.Responses.GetLocationResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.UpdateLocationResponse;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupActivity extends AppCompatActivity {
    private static final int REQUEST_FOR_LOCATION = 0;

    NavigationActivityHelper helper;
    Retrofit retrofit;

    AuthService authService;
    GroupsService groupsService;
    LocationService locationService;

    ArrayList<Integer> groupMembers;

    FusedLocationProviderClient fusedLocationClient;

    Observable<Location> userLocation;
    Observable<Location> locationUpdates;

    void getLastLocation() {
        userLocation = Observable.create(emitter -> {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FOR_LOCATION);
            } else {
                // get last known location
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        emitter.onNext(location);
                    }
                });
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        AuthApi authApi = retrofit.create(AuthApi.class);
        authService = new AuthService(authApi, this);

        LocationApi locationApi = retrofit.create(LocationApi.class);
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        // static measure to test location service working

        locationService = new LocationService(locationApi, this);
        groupsService = new GroupsService(this, groupsApi);

        ArrayList<Integer> members = new ArrayList<>();
        members.add(1);
        members.add(2);

        // location stuff
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // get last location
        getLastLocation();
        userLocation.subscribe(new Consumer<Location>() {
            @Override
            public void accept(Location location) throws Exception {
                System.out.println(location);
            }
        });

        // get polling location of the device
        startLocationUpdates();
        locationUpdates.subscribe(new Observer<Location>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Location location) {
                System.out.print("locationUpdate: ");
                System.out.println(location);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });







//        groupsService.newGroup("keebs2", members)
//            .subscribe(new Observer<GroupsResponse>() {
//                @Override
//                public void onSubscribe(Disposable d) {
//
//                }
//
//                @Override
//                public void onNext(GroupsResponse groupsResponse) {
//                    System.out.println("got response");
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    System.out.println(e);
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//            });

        groupsService.getGroup(12).subscribe(new Observer<GroupsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GroupsResponse groupsResponse) {
                // received a response from the server
                if (groupsResponse.group != null) {
                    // update list of members in instance variable in class
                    groupMembers = groupsResponse.group.members;
                    pollForGroupMembersLocations();

                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
                // e.g. invalid token, internal errors, etc.
            }

            @Override
            public void onComplete() {

            }
        });

        // example for updating location on the server
        locationService.updateLocation(1, 1, 1, 72)
                .subscribe(new Observer<UpdateLocationResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UpdateLocationResponse updateLocationResponse) {
                        Toast.makeText(getApplicationContext(),
                                "Something didn't happen.",

                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(),
                                "Something happened.",

                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        // FAB used to create new chat
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Current placeholder
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        helper = new NavigationActivityHelper(navigationView, drawer, toolbar, this);

    }

    /**
     * Call this once you have a list of members.
     * A route to get data for multiple members will be implemented soon.
     */
    void pollForGroupMembersLocations() {
        Observable.interval(1, TimeUnit.SECONDS)
                .map((tick) -> {
                    getUserLocationsAndUpdateUI();
                    return 1;
                }).subscribe();
    }

    void getUserLocationsAndUpdateUI() {
        System.out.println("getUserLocationsAndUpdateUI()");
        for (int i = 0; i < groupMembers.size(); i++) {
            int userID = groupMembers.get(i);
            locationService.getLocation(userID).subscribe(new Observer<GetLocationResponse>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(GetLocationResponse getLocationResponse) {
                    System.out.print(userID);
                    System.out.print(": ");
                    System.out.print(getLocationResponse.lat);
                    System.out.print(", ");
                    System.out.println(getLocationResponse.lon);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // stuff for location
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    // TODO handle onResume - resume looking for location updates

    private void startLocationUpdates() {
        locationUpdates = Observable.create(emitter -> {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FOR_LOCATION);
            } else {
                fusedLocationClient.requestLocationUpdates(createLocationRequest(), new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {   // List of Location objects - updates might be pending
                            // relay updates to the appropriate observable
                            emitter.onNext(location);
                        }
                    }
                }, /* looper */ null);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_FOR_LOCATION) {
            // if request is cancelled, the result arrays are empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted!
                getLastLocation();

            } else {
                // permission denied!
                // TODO show TextView in activity, say that permission was not granted
            }
        }
    }





}
