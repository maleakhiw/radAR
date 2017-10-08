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

import com.google.android.gms.maps.MapView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.ARActivity2;
import radar.radar.GroupDetailsLifecycleListener;
import radar.radar.Models.Group;
import radar.radar.Models.MeetingPoint;
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


        TextInputLayout meetingPointName = rootView.findViewById(R.id.meeting_point_name);
        TextInputLayout meetingPointLat = rootView.findViewById(R.id.meeting_point_lat);
        TextInputLayout meetingPointLon = rootView.findViewById(R.id.meeting_point_lon);


        Bundle args = getArguments();

        System.out.println("groupLocationsFragment");
        if (args != null) {
            Group group = (Group) args.getSerializable("group");
            rootView.findViewById(R.id.start_tracking_in_AR).setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), ARActivity2.class);
                intent.putExtra("groupID", group.groupID);
                startActivity(intent);
            });

            Button setMeetingPointBtn = rootView.findViewById(R.id.setMeetingPointButton);
            setMeetingPointBtn.setOnClickListener(view -> {
                String name = meetingPointName.getEditText().getText().toString();
                String lat = meetingPointLat.getEditText().getText().toString();
                String lon = meetingPointLon.getEditText().getText().toString();

                Double latDouble = Double.parseDouble(lat);
                Double lonDouble = Double.parseDouble(lon);

                System.out.println(name);
                System.out.println(lat);
                System.out.println(lon);

                if (name != null && latDouble != null && lonDouble != null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://35.185.35.117/api/")
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
                            Toast.makeText(getActivity(), "Updated meeting point", Toast.LENGTH_SHORT).show();
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
