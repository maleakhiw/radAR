package radar.radar.Views;

import com.google.android.gms.location.LocationCallback;

/

public interface HomeScreenView {
    String getStringRes(int resourceID);

    int getColorRes(int resourceID);

    void requestLocationPermissions();

}
