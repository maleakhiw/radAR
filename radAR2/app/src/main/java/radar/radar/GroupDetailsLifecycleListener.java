package radar.radar;

import android.app.Fragment;

import java.io.Serializable;

import radar.radar.Fragments.GroupDetailsFragment;

/**
 * Created by kenneth on 3/10/17.
 */

public interface GroupDetailsLifecycleListener extends Serializable {
    void onSetUp(Fragment fragment);
}
