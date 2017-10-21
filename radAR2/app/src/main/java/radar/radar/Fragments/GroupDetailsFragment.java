package radar.radar.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.GroupMembersAdapter;
import radar.radar.AddMembersActivity;
import radar.radar.ChatActivity;
import radar.radar.Listeners.GroupDetailsLifecycleListener;
import radar.radar.MapsActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.Status;
import radar.radar.Presenters.GroupDetailsPresenter;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.AuthService;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import radar.radar.Services.LocationTransformations;
import radar.radar.Views.GroupDetailView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class GroupDetailsFragment extends Fragment implements GroupDetailView {
    TextView nameTextView;
    TextView mainTextView;
    RecyclerView recyclerView;
    GroupMembersAdapter friendsAdapter;

    Button addMembersButton;

    TextView destinationTV;
    TextView distanceTV;

    MapView mapView;

    GroupDetailsLifecycleListener listener;

    LocationService locationService;

    GroupDetailsPresenter presenter;

    private Group group = null;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String TAG = "GroupDetailFragment";

    private static final int REQUEST_FOR_LOCATION_DISTANCE = 2;
    private static final int REQUEST_FOR_LOCATION_MAP = 3;

    public void setListener(GroupDetailsLifecycleListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (listener != null) {
            bundle.putSerializable("listener", listener);
        }
        super.onSaveInstanceState(bundle);
        mapView.onSaveInstanceState(bundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.


        if (savedInstanceState != null) {
            // restore the listener
            listener = (GroupDetailsLifecycleListener) savedInstanceState.getSerializable("listener");
        }

        View rootView = inflater.inflate(
                R.layout.fragment_group_details, container, false);
        Bundle args = getArguments();

        group = (Group) args.getSerializable("group");

//        nameTextView = rootView.findViewById(R.id.fragment_group_details_name);
//        nameTextView.setText(group.name);

        addMembersButton = rootView.findViewById(R.id.add_members_button);
        addMembersButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddMembersActivity.class);
            intent.putExtra("group", group);
            startActivity(intent);
        });


        mainTextView = rootView.findViewById(R.id.group_detail_textview);
        mainTextView.setText("Members");

        destinationTV = rootView.findViewById(R.id.group_detail_dest_name);
        distanceTV = rootView.findViewById(R.id.group_detail_distance);

        recyclerView = rootView.findViewById(R.id.group_details_members_recyclerView);
        friendsAdapter = new GroupMembersAdapter(getActivity(), new HashMap<Integer, User>());  // getContext becomes getActivity inside a fragment
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsAdapter.updateFriends(group.usersDetails);

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

        LocationApi locationApi = retrofit.create(LocationApi.class);
        LocationService locationService = new LocationService(locationApi, getActivity(), LocationServices.getFusedLocationProviderClient(getActivity()));


        mapView = rootView.findViewById(R.id.group_detail_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            // UI now ready
            this.googleMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermissions(REQUEST_FOR_LOCATION_MAP);
                System.out.println("Request location - map");
            } else {
                googleMap.setMyLocationEnabled(true);
            }
            presenter = new GroupDetailsPresenter(this, group, locationService);
        });

        // Make a marker when the button is clicked.
        Button openButton = (Button) rootView.findViewById(R.id.add_new_location);
        openButton.setOnClickListener(view -> onSetButtonClicked());

        // Navigate
        Button navigateButton = (Button) rootView.findViewById(R.id.navigate_to_location);
        navigateButton.setOnClickListener(view -> onNavigateButtonClicked());

        // go to the group chat
        FloatingActionButton fab = rootView.findViewById(R.id.group_details_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("group", group);
            intent.putExtra("user", group.usersDetails.get(AuthService.getUserID(getActivity())));
            intent.putExtra("load", true);

            startActivity(intent);
        });

        // notify main activity that we have done initiating
        listener.onSetUp(this);

        return rootView;
    }

    /**
     * Click event handler to handle clicking the "Set" Button
     */
    public void onSetButtonClicked() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(getActivity());
            startActivityForResult(i, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
        }
    }

    /**
     * Click event handler to handle clicking the "Navigate" Button
     */
    public void onNavigateButtonClicked() {
        try {
            //open map activity and display navigation
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            if (meetingPoint != null) {
                intent.putExtra("meetingPoint", meetingPoint);
                intent.putExtra("group", group);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Please set a meeting point first.", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    double lastMeetingPointLat;
    double lastMeetingPointLon;

    public void updateLabels(String name, double lat, double lon) {
        destinationTV.setText(name);
        lastMeetingPointLat = lat;
        lastMeetingPointLon = lon;
    }

    MeetingPoint meetingPoint;

    @Override
    public void setMeetingPoint(MeetingPoint meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    DecimalFormat df = new DecimalFormat();

    public void updateDistance(double currentLat, double currentLon) {
        System.out.println("updateDistance");
        double distance = LocationTransformations.distance(currentLat, currentLon, lastMeetingPointLat, lastMeetingPointLon, 'K');

        if (distance >= 1) {
//            distance = distance/1000;
            df.setMaximumFractionDigits(2);
            distanceTV.setText(df.format(distance) + " " + "km");   // TODO miles
        } else {
            distance *= 1000;
            distanceTV.setText(((Integer) (int) distance).toString() + " m");
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlacePicker.getPlace(getActivity(), data);
                Log.i(TAG, "Place Selected: " + place.getName());
                mapView.getMapAsync(googleMap -> {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(place.getLatLng())
                            .title(getString(R.string.unimelb)));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                });

                double latDouble = place.getLatLng().latitude;
                double lonDouble = place.getLatLng().longitude;
                String name = place.getName().toString();

                updateLabels(name, latDouble, lonDouble);

                //update group location settings
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://radar.fadhilanshar.com/api/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                LocationApi locationApi = retrofit.create(LocationApi.class);
                LocationService locationService = new LocationService(locationApi, getActivity(), LocationServices.getFusedLocationProviderClient(getActivity()));

                locationService.getLastLocation().subscribe(new Observer<Location>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Location location) {
                        updateDistance(location.getLatitude(), location.getLongitude());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().equals("GRANT_ACCESS_FINE_LOCATION")) {
//                            requestLocationPermissions(REQUEST_FOR_LOCATION_DISTANCE);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
                
                meetingPoint = new MeetingPoint(latDouble, lonDouble, name, "");

                GroupsService groupsService = new GroupsService(getActivity(), retrofit.create(GroupsApi.class));
                groupsService.updateMeetingPoint(group.groupID, new MeetingPoint(latDouble, lonDouble, name, "")).subscribe(new Observer<Status>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Status status) {
                        if (status.success) {
                            Toast.makeText(getActivity(), "Meeting point updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Error update");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

            }
        }
    }

    public void setMainTextView(String text) {
        if (mainTextView != null) {
            mainTextView.setText(text);
        }
    }

    @Override
    public void moveCameraTo(double lat, double lon) {
        if (googleMap != null) {
            LatLng latLng = new LatLng(lat, lon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    public void dropPinAt(double lat, double lon, String name) {
        if (googleMap != null) {
//            googleMap.clear();  // TODO
            LatLng latLng = new LatLng(lat, lon);
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(name));
        }
    }

    @Override
    @Deprecated
    public void setMeetingPointLatLon(double lat, double lon) {
        System.out.println(lat);
        System.out.println(lon);
        lastMeetingPointLat = lat;
        lastMeetingPointLon = lon;
    }

    @Override
    @Deprecated
    public void setMeetingPointName(String name) {
        if (destinationTV != null) {
            destinationTV.setText(name);
        }
    }

    @Override
    public void updateDistanceToMeetingPoint() {
        if (distanceTV != null) {
            //update group location settings
            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

            LocationApi locationApi = retrofit.create(LocationApi.class);
            LocationService locationService = new LocationService(locationApi, getActivity(), LocationServices.getFusedLocationProviderClient(getActivity()));

            locationService.getLastLocation().subscribe(new Observer<Location>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Location location) {
                    updateDistance(location.getLatitude(), location.getLongitude());
                }

                @Override
                public void onError(Throwable e) {
                    if (e.getMessage().equals("GRANT_ACCESS_FINE_LOCATION")) {
//                        requestLocationPermissions(REQUEST_FOR_LOCATION_DISTANCE);
                    }
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    @Override
    public void requestLocationPermissions(int requestCode) {
        FragmentCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        System.out.println("onRequestPermissionsResult");
        if (requestCode == REQUEST_FOR_LOCATION_DISTANCE) {
            System.out.println("Request location - distance");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateDistanceToMeetingPoint();
                if (googleMap != null) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
            }
        }

        if (requestCode == REQUEST_FOR_LOCATION_MAP) {
            System.out.println("Request location - map");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(googleMap);
                updateDistanceToMeetingPoint();
                if (googleMap != null) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {}
        }
    }

}
