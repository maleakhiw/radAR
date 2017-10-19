package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Domain.User;

/**
 * Interface for FriendsActivity to support MVP models.
 */
public interface FriendsView {
    void showToast(String toast);

    void launchHomeScreenActivity();

    void launchSearchFriendsActivity();

    void bindAdapterToRecyclerView(ArrayList<User> friends);

}
