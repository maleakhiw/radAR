package radar.radar.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.ARActivity;
import radar.radar.Listeners.GroupDetailsLifecycleListener;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Responses.Status;
import radar.radar.R;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupLocationsFragment extends Fragment {
    private GroupDetailsLifecycleListener listener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("listener", listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.

        // restore the listener
        if (savedInstanceState != null) {
            listener = (GroupDetailsLifecycleListener) savedInstanceState.getSerializable("listener");
        }

        View rootView = inflater.inflate(R.layout.fragment_group_locations, container, false);

        Bundle args = getArguments();
        if (args != null) {
            Group group = (Group) args.getSerializable("group");
            rootView.findViewById(R.id.start_tracking_in_AR).setOnClickListener(view -> {
                if (group != null) {
                    Intent intent = new Intent(getActivity(), ARActivity.class);
                    intent.putExtra("groupID", group.groupID);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Invalid group.", Toast.LENGTH_SHORT);
                }
                }

            });


        // notify main activity that we have done initiating
        listener.onSetUp(this);
        return rootView;
    }

    public void setListener(GroupDetailsLifecycleListener listener) {
        this.listener = listener;
    }
}
