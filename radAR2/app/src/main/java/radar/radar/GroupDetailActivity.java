package radar.radar;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import radar.radar.Fragments.GroupDetailsFragment;
import radar.radar.Fragments.GroupLocationsFragment;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailsLifecycleListener {

    ViewPager viewPager;
    FragmentPagerAdapter pagerAdapter;

    GroupDetailsFragment groupDetailsFragment;
    GroupLocationsFragment groupLocationsFragment;
    // fragment2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        viewPager = findViewById(R.id.groupDetailPager);

        GroupDetailsLifecycleListener that = this;  // so that we know when the Fragment is fully inflated
                                                    // and ready to have its UI elements modified

        pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    groupDetailsFragment = new GroupDetailsFragment();
                    groupDetailsFragment.setListener(that);
                    return groupDetailsFragment;
                } else {
                    groupLocationsFragment = new GroupLocationsFragment();
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
        tabLayout.getTabAt(0).setText("Details");
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
        // TODO get title from bundle
        setTitle("group name");

    }

    @Override
    public void onSetUp(Fragment fragment) {
        if (fragment instanceof GroupDetailsFragment) {
//            ((groupDetailsFragment) groupDetailsFragment).setMainTextView("Programmatically set text");
        }
    }
}
