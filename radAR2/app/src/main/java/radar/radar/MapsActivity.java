package radar.radar;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        // Obtain the SupportMapFragment and get notified when the googleMap is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].startLocation.lat,
                        results.routes[0].legs[0].startLocation.lng)).title("Origin"));
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].endLocation.lat,
                        results.routes[0].legs[0].endLocation.lng)).title("Destination"));
    }


    /**
     * Manipulates the googleMap once available.
     * This callback is triggered when the googleMap is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng unimelb = new LatLng(-37.7963689,144.9611738);
        LatLng sydney = new LatLng(-34, 151);

        // Testing purposes
        CharSequence text = "Null";
        int duration = Toast.LENGTH_LONG;

        String orig;
        String dest;
        DateTime now = new DateTime();
        DirectionsResult results;
        try {
            orig = String.valueOf(unimelb.latitude) + "," + String.valueOf(unimelb.longitude);
            dest = String.valueOf(sydney.latitude) + "," + String.valueOf(sydney.longitude);

            results = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(orig)
                    .destination(dest).departureTime(now)
                    .await();
            addMarkersToMap(results,mMap);

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
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(unimelb,12));
    }
}
