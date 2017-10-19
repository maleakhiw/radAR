package radar.radar.Listeners;

import android.app.Fragment;

import java.io.Serializable;

import radar.radar.Fragments.GroupDetailsFragment;

public interface GroupDetailsLifecycleListener extends Serializable {
    void onSetUp(Fragment fragment);
}
