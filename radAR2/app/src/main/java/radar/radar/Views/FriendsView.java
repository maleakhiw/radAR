package radar.radar.Views;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;

import radar.radar.Models.User;

/**
 * Created by kenneth on 19/9/17.
 */

public interface FriendsView {
    void showToast(String toast);

    void launchHomeScreenActivity();

    void bindAdapterToRecyclerView(ArrayList<User> friends);

}
