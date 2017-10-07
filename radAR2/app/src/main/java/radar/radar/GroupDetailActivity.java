package radar.radar;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Fragments.GroupDetailsFragment;
import radar.radar.Fragments.GroupLocationsFragment;
import radar.radar.Models.Group;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailsLifecycleListener {

    ViewPager viewPager;
    FragmentPagerAdapter pagerAdapter;

    GroupDetailsFragment groupDetailsFragment;
    GroupLocationsFragment groupLocationsFragment;
    // fragment2

    GroupsService groupsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);


        Group group = (Group) getIntent().getSerializableExtra("group");
        if (group == null) {
            finish();   // nothing to do here
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        groupsService = new GroupsService(this, groupsApi);

        viewPager = findViewById(R.id.groupDetailPager);

        GroupDetailsLifecycleListener that = this;  // so that we know when the Fragment is fully inflated
                                                    // and ready to have its UI elements modified

        pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", group);
                if (position == 0) {
                    groupDetailsFragment = new GroupDetailsFragment();
                    groupDetailsFragment.setArguments(bundle);
                    groupDetailsFragment.setListener(that);
                    return groupDetailsFragment;
                } else {
                    groupLocationsFragment = new GroupLocationsFragment();
                    groupLocationsFragment.setArguments(bundle);
                    groupLocationsFragment.setListener(that);
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

    @Override
    public void onSetUp(Fragment fragment) {
        if (fragment instanceof GroupDetailsFragment) {

        } else if (fragment instanceof GroupLocationsFragment) {

        }
    }
}
