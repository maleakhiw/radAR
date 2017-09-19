package radar.radar.Services;


import radar.radar.Models.GroupDetails;

import io.reactivex.Observable;

/**
 * Created by kenneth on 6/9/17.
 */

// interactor, implements a use case
// corresponds to TrackingGroupService in Design document
public interface TrackingGroupInteractor {
    public Observable<GroupDetails> getGroupDetails(int userID, int queryUserID, String token);
    public Observable<GroupDetails> getGroupDetails(String username, int queryUserID, String token);
}
