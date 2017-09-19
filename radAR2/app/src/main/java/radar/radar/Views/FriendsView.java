package radar.radar.Views;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;

import radar.radar.Models.Responses.User;

/**
 * Created by kenneth on 19/9/17.
 */

public interface FriendsView {
    void showToast(String toast);

    void bindAdapterToRecyclerView(ArrayList<User> friends);

    void setFABOnClickListener(View.OnClickListener onClickListener);

    void startActivityFromIntent(Intent intent);
}
