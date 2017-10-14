package radar.radar.Views;

import radar.radar.Models.Responses.FriendRequestsResponse;

/**
 * Interface that are used to support MVP for PendingRequestsFragment
 */
public interface PendingRequestsView {
    void showToast(String message);

    void bindAdapterToRecyclerView(FriendRequestsResponse friendRequestsResponse);
}
