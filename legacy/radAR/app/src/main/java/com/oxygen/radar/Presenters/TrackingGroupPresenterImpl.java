package com.oxygen.radar.Presenters;

import com.oxygen.radar.Models.GroupDetails;
import com.oxygen.radar.Models.Info;
import com.oxygen.radar.Services.TrackingGroupInteractor;
import com.oxygen.radar.Services.TrackingGroupInteractorImpl;
import com.oxygen.radar.Views.TrackingGroupView;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kenneth on 6/9/17.
 */

public class TrackingGroupPresenterImpl implements TrackingGroupPresenter {

    private TrackingGroupView mView;
    public TrackingGroupPresenterImpl(TrackingGroupView view) {
        mView = view;
    }

    @Override
    public void loadGroupDetailsToView() {
        // TODO: get userID from Auth Component, groupID from Bundle (change Presenter interface)
        TrackingGroupInteractor mTrackingGroupInteractor = new TrackingGroupInteractorImpl();
        mTrackingGroupInteractor.getGroupDetails(1, 1, "79").
                subscribeOn(Schedulers.io()).
                subscribe(new Consumer<GroupDetails>() {
            @Override
            public void accept(GroupDetails groupDetails) throws Exception {
                // TODO: load to view
                System.out.println(groupDetails.getSuccess());
                System.out.println(groupDetails.getErrors());
                Info groupInfo = groupDetails.getInfo();

                if (groupInfo != null) {
                    String groupName = groupDetails.getInfo().getName();
                    System.out.println(groupName);

                    ArrayList<Integer> members = groupDetails.getInfo().getMembers();
                    for (int i=0; i<members.size(); i++) {
                        System.out.println(members.get(i));
                    }
                }

            }
        });
    }

    @Override
    public void onDestroy() {

    }
}
