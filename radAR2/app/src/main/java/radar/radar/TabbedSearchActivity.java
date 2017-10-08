package radar.radar;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import radar.radar.Fragments.PendingRequestsFragment;
import radar.radar.Fragments.SearchUserFragment;

public class TabbedSearchActivity extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        // setup tab layout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchUserFragment(), "Search Users");
        adapter.addFragment(new PendingRequestsFragment(), "Pending Requests");
        viewPager.setAdapter(adapter);
    }

}
