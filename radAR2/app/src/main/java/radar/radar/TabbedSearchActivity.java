package radar.radar;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import radar.radar.Fragments.PendingRequestsFragment;
import radar.radar.Fragments.SearchUserFragment;

/**
 * Main Activity that are used to display search user functionality and also pending friend requests
 */
public class TabbedSearchActivity extends AppCompatActivity {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        // Setup title of the page
        setTitle("Search");

        // Enable back action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        // setup tab layout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Enable back button functionality to previous activity
     */
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    /**
     * Setup the pager to connect pending friend request and search user functionality using tab
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchUserFragment(), "Search Users");
        adapter.addFragment(new PendingRequestsFragment(), "Pending Requests");
        viewPager.setAdapter(adapter);
    }

}
