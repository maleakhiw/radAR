package com.oxygen.radar.Views;

import com.oxygen.radar.Models.GroupDetails;

/**
 * Created by kenneth on 6/9/17.
 */

public interface TrackingGroupView {
    void setTitle(String title);
    void loadGroupDetails(GroupDetails groupDetails);
}
