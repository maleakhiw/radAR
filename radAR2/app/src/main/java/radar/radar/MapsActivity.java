package radar.radar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Domain.UserLocation;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import retrofit2.Retrofit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private MeetingPoint meetingPoint;
    private Group group;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        // Obtain the SupportMapFragment and get notified when the googleMap is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        group = (Group) getIntent().getExtras().getSerializable("group");
        meetingPoint = (MeetingPoint) getIntent().getExtras().getSerializable("meetingPoint");  // TODO remove, take from group

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        locationService = new LocationService(retrofit.create(LocationApi.class), this, fusedLocationClient);

        // set up the onLocationCallback
        // TODO not for here - for GroupLocationsFragment

    }

    /** Create a GeoApiContext to set API key and some restrictions
     *  ConnectTimeout: The default connect timeout for new connections.
     *  QueryRate: The maximum number of queries that will be executed during a 1 second intervals.
     *  ReadTimeout: The default read timeout for new connections.
     *  WriteTimeout: The default write timeout for new connections. */
    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].endAddress).snippet(getEndLocationTitle(results)));
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return  "Time: "+ results.routes[0].legs[0].duration.humanReadable + " Distance: " + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    LatLng from;
    LatLng to;

    HashMap<Integer, Marker> markers = new HashMap<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (meetingPoint != null) {
            // Add a marker in Sydney and move the camera
           drawRoute();
        }

        loadGroupLocations();
    }

    boolean first = true;
    public void drawRoute() {
        locationService.getLastLocation().subscribe(location -> {
            from = new LatLng(location.getLatitude(), location.getLongitude());
            to = new LatLng(meetingPoint.lat, meetingPoint.lon);

            // Testing purposes
            CharSequence text = "Null";
            int duration = Toast.LENGTH_SHORT;

            String orig;
            String dest;
            DateTime now = new DateTime();
            DirectionsResult results;
            try {
                orig = String.valueOf(from.latitude) + "," + String.valueOf(from.longitude);
                dest = String.valueOf(to.latitude) + "," + String.valueOf(to.longitude);

                results = DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.DRIVING).origin(orig)
                        .destination(dest).departureTime(now)
                        .await();
                addMarkersToMap(results,mMap);
                addPolyline(results, mMap);
                text = "Success";

            } catch (ApiException e) {
                e.printStackTrace();
                text = "Exception Error";
            } catch (InterruptedException e) {
                e.printStackTrace();
                text = "Exception Error";
            } catch (IOException e) {
                e.printStackTrace();
                text = "Exception Error";
            } finally {
//                Toast toast = Toast.makeText(this, text, duration).show();
            }
            //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            if (first) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from,18));
                first = false;
            }

        }, System.out::println);
    }

    private void loadGroupLocations() {
        locationService.getGroupLocationInfo(group.groupID, 3000).subscribe(
            groupLocationsInfo -> {
                mMap.clear();  // clear all previous pins
                drawRoute();


                for (UserLocation location: groupLocationsInfo.locations) {
                    Marker existingMarker = markers.get(location.getUserID());
//                    if (existingMarker != null) {
//                        existingMarker.setPosition(new LatLng(location.lat, location.lon));
//                    } else {
//                        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.lat, location.lon)).title(group.usersDetails.get(location.getUserID()).firstName));
//                        marker.showInfoWindow();
//
//                        markers.put(location.getUserID(), marker);  // add to list
//                    }
                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.lat, location.lon)).title(group.usersDetails.get(location.getUserID()).firstName));
                    marker.showInfoWindow();

                    markers.put(location.getUserID(), marker);  // add to list

                }
            }, System.out::println
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        loadGroupLocations();
    }

    @Override
    public void onStop() {
        super.onStop();

        locationService.stopPollingGroupLocation();
    }
}
