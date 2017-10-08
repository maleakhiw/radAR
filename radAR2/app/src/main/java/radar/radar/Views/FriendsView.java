package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Domain.User;

/**
 * Created by kenneth on 19/9/17.
 */

public interface FriendsView {
    void showToast(String toast);

    void launchHomeScreenActivity();

    void launchSearchFriendsActivity();

    void bindAdapterToRecyclerView(ArrayList<User> friends);

}
