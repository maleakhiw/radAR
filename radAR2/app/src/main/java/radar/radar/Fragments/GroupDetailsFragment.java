package radar.radar.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import radar.radar.Adapters.GroupMembersAdapter;
import radar.radar.ChatActivity;
import radar.radar.Listeners.GroupDetailsLifecycleListener;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.Services.AuthService;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupDetailsFragment extends Fragment {
    TextView nameTextView;
    TextView mainTextView;
    RecyclerView recyclerView;
    GroupMembersAdapter friendsAdapter;

    MapView mapView;

    GroupDetailsLifecycleListener listener;

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

        Group group = (Group) args.getSerializable("group");

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
            googleMap.addMarker(new MarkerOptions().position(melbourne_university)
                    .title(getString(R.string.unimelb)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne_university, 15));
        });

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



    public void setMainTextView(String text) {
        if (mainTextView != null) {
            mainTextView.setText(text);
        }
    }
}
