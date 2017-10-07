package radar.radar;

import android.location.Location;

/**
 * Created by kenneth on 8/10/17.
 */

interface LocationConsumer {
    void onLocationUpdate(Location location);
}
