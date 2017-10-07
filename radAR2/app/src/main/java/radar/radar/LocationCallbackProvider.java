package radar.radar;

import com.google.android.gms.location.LocationCallback;

/**
 * Created by kenneth on 8/10/17.
 */

interface LocationCallbackProvider {
    LocationCallback getLocationCallback(LocationConsumer locationConsumer);
}
