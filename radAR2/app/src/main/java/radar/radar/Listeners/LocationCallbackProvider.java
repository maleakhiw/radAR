package radar.radar.Listeners;

import com.google.android.gms.location.LocationCallback;

/**
 * Created by kenneth on 8/10/17.
 */

public interface LocationCallbackProvider {
    LocationCallback getLocationCallback(LocationUpdateListener locationUpdateListener);
}
