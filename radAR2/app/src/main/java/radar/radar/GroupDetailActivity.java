package radar.radar;

import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import radar.radar.Fragments.GroupDetailsFragment;

public class GroupDetailActivity extends AppCompatActivity {

    ViewPager viewPager;
    FragmentPagerAdapter pagerAdapter;

    // fragment1
    // fragment2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        viewPager = findViewById(R.id.groupDetailPager);

        pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new GroupDetailsFragment();
                return fragment;
//                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        viewPager.setAdapter(pagerAdapter);


    }
}
