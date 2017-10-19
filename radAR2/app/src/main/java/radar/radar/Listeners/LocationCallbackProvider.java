package radar.radar.Listeners;

import com.google.android.gms.location.LocationCallback;

public interface LocationCallbackProvider {
    LocationCallback getLocationCallback(LocationUpdateListener locationUpdateListener);
}
