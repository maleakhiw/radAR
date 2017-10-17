package radar.radar.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.ARActivity;
import radar.radar.Adapters.GroupMemberLocationsAdapter;
import radar.radar.ChatActivity;
import radar.radar.Listeners.GroupDetailsLifecycleListener;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Responses.GroupLocationsInfo;
import radar.radar.Models.Responses.Status;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupLocationsFragment extends Fragment {

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        // from https://stackoverflow.com/a/39965170
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private GroupDetailsLifecycleListener listener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("listener", listener);
    }

    Button startTracking;
    TextView trackingBlurb;
    TextView offOn;

    RecyclerView groupLocations;

    LocationService locationService;
    FusedLocationProviderClient fusedLocationClient;

    boolean isTracking = false;

    GroupMemberLocationsAdapter adapter;

    Group group;

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
            group = (Group) args.getSerializable("group");

            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();

            LocationApi locationApi = retrofit.create(LocationApi.class);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            locationService = new LocationService(locationApi, getActivity(), fusedLocationClient);

            adapter = new GroupMemberLocationsAdapter(rootView.getContext(), group.usersDetails, new ArrayList<>(), 0, 0);

            groupLocations = rootView.findViewById(R.id.locationsRV);
            groupLocations.setAdapter(adapter);
            groupLocations.setLayoutManager(new LinearLayoutManager(rootView.getContext()));


            FloatingActionButton fab = rootView.findViewById(R.id.fab);
            fab.setOnClickListener(view -> {
                if (group != null) {
                    Intent intent = new Intent(getActivity(), ARActivity.class);
                    intent.putExtra("groupID", group.groupID);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Invalid group.", Toast.LENGTH_SHORT);
                }
            });
            fab.setImageBitmap(textAsBitmap("AR", 40, Color.WHITE));
        }

        startTracking = rootView.findViewById(R.id.start_tracking);
        trackingBlurb = rootView.findViewById(R.id.tracking_blurb);
        offOn = rootView.findViewById(R.id.offOnTV);

        startTracking.setOnClickListener(v -> {
            if (isTracking) {
                setStopTracking();
            } else {
                setTracking();
            }
        });



        // notify main activity that we have done initiating
        listener.onSetUp(this);
        return rootView;

    }

    public void setTracking() {
        isTracking = true;
        // TODO locationService
        trackingBlurb.setText(getString(R.string.tracking));
        offOn.setText(getString(R.string.on_caps));
        startTracking.setText(getString(R.string.stop_tracking));

        // TODO use the safer one which can be unsubscribed from
        Observable<Location> locationObservable = locationService.getLocationUpdates(3000, 3000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        Observable<GroupLocationsInfo> groupLocationsInfoObservable = locationService.getGroupLocationInfo(group.groupID, 3000);
        Observable.combineLatest(locationObservable, groupLocationsInfoObservable, (location, groupLocation) -> {
            adapter.updateData(groupLocation.userDetails, groupLocation.locations, location.getLatitude(), location.getLongitude());
            locationService.updateLocation(location.getLatitude(), location.getLongitude(), location.getAccuracy(), 0f);
            return 1;
        }).takeUntil(aLong -> isTracking).subscribe(result -> {}, error -> {
            System.out.println("Grant permissions!");
            // TODO grant permissions
        });
    }

    public void setStopTracking() {
        isTracking = false;
        // TODO call "unsubscribe" on the service
        trackingBlurb.setText(getString(R.string.not_tracking));
        offOn.setText(getString(R.string.off_caps));
        startTracking.setText(getString(R.string.start_tracking));

    }

    public void setListener(GroupDetailsLifecycleListener listener) {
        this.listener = listener;
    }
}
