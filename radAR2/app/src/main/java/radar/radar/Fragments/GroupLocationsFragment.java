package radar.radar.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;

import radar.radar.ARActivity2;
import radar.radar.GroupDetailsLifecycleListener;
import radar.radar.Models.Group;
import radar.radar.R;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupLocationsFragment extends Fragment {
    private GroupDetailsLifecycleListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_group_locations, container, false);

        Bundle args = getArguments();

        if (args != null) {
            Group group = (Group) args.getSerializable("group");
            rootView.findViewById(R.id.start_tracking_in_AR).setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), ARActivity2.class);
                intent.putExtra("groupID", group.groupID);
                startActivity(intent);
            });
            rootView.findViewById(R.id.go_to_mapView).setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), MapView.class);
                intent.putExtra("groupID", group.groupID);
                startActivity(intent);
            });
        } else {

        }


        // notify main activity that we have done initiating
        listener.onSetUp(this);



        return rootView;
    }

    public void setListener(GroupDetailsLifecycleListener listener) {
        this.listener = listener;
    }
}
