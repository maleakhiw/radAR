package radar.radar.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import radar.radar.GroupDetailsLifecycleListener;
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

        // notify main activity that we have done initiating
        listener.onSetUp(this);

        return rootView;
    }

    public void setListener(GroupDetailsLifecycleListener listener) {
        this.listener = listener;
    }
}
