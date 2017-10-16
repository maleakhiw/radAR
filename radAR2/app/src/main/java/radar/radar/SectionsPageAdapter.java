package radar.radar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Pager that are used for FriendRequestActivity
 */
public class SectionsPageAdapter extends FragmentPagerAdapter {
    /** Variable to keep track of the fragments and title */
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> titleList = new ArrayList<>();

    /**
     * Constructor for this class
     * @param fm fragment manager
     */
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Add fragment to the adapter
     * @param fragment fragment to add for the adapter
     * @param title title of the fragment that are added
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    /**
     * Getter to get the fragment/ page title
     * @param position used to indicate which fragment title that we want
     * @return return the fragment title of a particular fragment
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    /**
     * Getter for fragment
     * @param position used to indicate which fragment that we want
     * @return fragments
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * Count how many fragments that are saved in the adapter
     * @return number of fragment that are stored in the adapter
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }
}

