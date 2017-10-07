package radar.radar.Views;

/**
 * Created by kenneth on 8/10/17.
 */

public interface HomeScreenView {
    String getStringRes(int resourceID);

    int getColorRes(int resourceID);

    void requestLocationPermissions();
}
