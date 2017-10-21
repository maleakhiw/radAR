package radar.radar;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Fragments.GroupDetailsFragment;
import radar.radar.Fragments.GroupLocationsFragment;
import radar.radar.Listeners.GroupDetailsLifecycleListener;
import radar.radar.Listeners.LocationUpdateListener;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import retrofit2.Retrofit;

public class MeetingPointActivity extends AppCompatActivity implements LocationUpdateListener, MeetingPointUpdateListener {

    ViewPager viewPager;
    FragmentPagerAdapter pagerAdapter;

    GroupDetailsFragment groupDetailsFragment;
    GroupLocationsFragment groupLocationsFragment;

    MeetingPoint meetingPoint;
    // fragment2

    GroupsService groupsService;

    static GroupDetailsLifecycleListener lifecycleListener = new GroupDetailsLifecycleListener() {
        @Override
        public void onSetUp(Fragment fragment) {
            if (fragment instanceof GroupDetailsFragment) {

            } else if (fragment instanceof GroupLocationsFragment) {

            }
        }
    };  // so that we know when the Fragment is fully inflated
    // and ready to have its UI elements modified

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        group = (Group) getIntent().getSerializableExtra("group");
        if (group == null) {
            finish();   // nothing to do here
        }

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        groupsService = new GroupsService(this, groupsApi);

        viewPager = findViewById(R.id.groupDetailPager);

        pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", group);
                if (position == 0) {
                    groupDetailsFragment = new GroupDetailsFragment();
                    groupDetailsFragment.setArguments(bundle);
                    groupDetailsFragment.setListener(lifecycleListener);
                    return groupDetailsFragment;
                } else {
                    groupLocationsFragment = new GroupLocationsFragment();
                    groupLocationsFragment.setArguments(bundle);
                    groupLocationsFragment.setListener(lifecycleListener);
                    return groupLocationsFragment;
                }

            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        viewPager.setAdapter(pagerAdapter);

        // setup tabs
        TabLayout tabLayout = findViewById(R.id.group_detail_tabs);
//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                tabLayout.setupWithViewPager(viewPager);
//            }
//        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Info");
        tabLayout.getTabAt(1).setText("Location Tracking");

        // set up back button on the toolbar
        Toolbar toolbar = findViewById(R.id.group_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        String name = group.name;
        setTitle(name);

    }

    // NOTE: adding menu to code: hook into onCreateOptionsMenu, inflate the menu
    // then handle menu selection events

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_detail, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        groupsService.getGroup(group.groupID).subscribe(new Observer<GroupsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(GroupsResponse groupsResponse) {
                if (groupsResponse.success) {   // TODO - update fragments too
                    group = groupsResponse.group;
                    setTitle(group.name);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(this, EditGroupActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onLocationUpdate(Location location) {
        if (groupDetailsFragment != null) {
            groupDetailsFragment.updateDistance(location.getLatitude(), location.getLongitude());
        }
    }


    @Override
    public void setMeetingPoint(MeetingPoint meetingPoint) {
        System.out.println("setMeetingPoint()");
        this.meetingPoint = meetingPoint;
        if (groupLocationsFragment != null) {
            groupLocationsFragment.setMeetingPoint(meetingPoint);
        }
    }
}
