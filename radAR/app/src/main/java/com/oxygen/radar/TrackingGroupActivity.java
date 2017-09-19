package com.oxygen.radar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.oxygen.radar.Models.GroupDetails;
import com.oxygen.radar.Presenters.TrackingGroupPresenter;
import com.oxygen.radar.Presenters.TrackingGroupPresenterImpl;
import com.oxygen.radar.Views.TrackingGroupView;

public class TrackingGroupActivity extends AppCompatActivity implements TrackingGroupView {

    private TrackingGroupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_group_list);

        // use custom title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.tracking_group_details);

        presenter = new TrackingGroupPresenterImpl(this);
        presenter.loadGroupDetailsToView();

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void loadGroupDetails(GroupDetails groupDetails) {

    }
}
