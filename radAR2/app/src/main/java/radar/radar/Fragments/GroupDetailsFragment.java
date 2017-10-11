package radar.radar.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.GroupMembersAdapter;
import radar.radar.ChatActivity;
import radar.radar.Listeners.GroupDetailsLifecycleListener;
import radar.radar.MapsActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.Status;
import radar.radar.R;
import radar.radar.Services.AuthService;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kenneth on 3/10/17.
 * Modified by rtanudjaja on 10/10/17
 */

public class GroupDetailsFragment extends Fragment {
    TextView nameTextView;
    TextView mainTextView;
    RecyclerView recyclerView;
    GroupMembersAdapter friendsAdapter;

    MapView mapView;

    GroupDetailsLifecycleListener listener;

    private Group group = null;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final String TAG = "SearchLocationActivity";

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

        mainTextView = rootView.findViewById(R.id.group_detail_textview);
        mainTextView.setText("Members");

        recyclerView = rootView.findViewById(R.id.group_details_members_recyclerView);
        friendsAdapter = new GroupMembersAdapter(getActivity(), new HashMap<Integer, User>());  // getContext becomes getActivity inside a fragment
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsAdapter.updateFriends(group.usersDetails);

        mapView = rootView.findViewById(R.id.group_detail_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            // Add a marker in Melbourne Uni and move the camera
            double unimelb_lat = Double.parseDouble(getString(R.string.melbourne_university_lat));
            double unimelb_lng = Double.parseDouble(getString(R.string.melbourne_university_lng));

            LatLng melbourne_university = new LatLng(unimelb_lat, unimelb_lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne_university, 15));
        });

        // Make a marker when the button is clicked.
        Button openButton = (Button) rootView.findViewById(R.id.add_new_location);
        openButton.setOnClickListener(view -> onSetButtonClicked());

        // Navigate
        Button navigateButton = (Button) rootView.findViewById(R.id.navigate_to_location);
        navigateButton.setOnClickListener(view -> onNavigateButtonClicked());

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


            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Click event handler to handle clicking the "Track" Button
     */
    public void onTrackButtonClicked() {
        try {
            //open map activity and display friends
        } catch (Exception e) {
            Log.e(TAG, e.toString());
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
                    googleMap.addMarker(new MarkerOptions().position(place.getLatLng())
                            .title(getString(R.string.unimelb)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                });

                double latDouble = place.getLatLng().latitude;
                double lonDouble = place.getLatLng().longitude;
                String name = place.getName().toString();

                //update group location settings
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://radar.fadhilanshar.com/api/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                GroupsService groupsService = new GroupsService(getActivity(), retrofit.create(GroupsApi.class));
                groupsService.updateMeetingPoint(group.groupID, new MeetingPoint(latDouble, lonDouble, name, "")).subscribe(new Observer<Status>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Status status) {
                        Toast.makeText(getActivity(), "Update meeting point to " + group.meetingPoint.name, Toast.LENGTH_SHORT).show();
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
}
