package radar.radar.Views;

import io.reactivex.Observable;
import radar.radar.CompassDirection;
import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 28/9/17.
 */

public interface ARView {
    void requestLocationPermissions();

    void showToast(String toast);

    boolean isInflated(int userID);

    void removeAnnotation(int userID);

    void getAnnotation(int userID);

    void inflateARAnnotation(UserLocation userLocation);

    // add more setters for other attributes of an annotation ltaer
    void setAnnotationMainText(int userID, String text);

    void setAnnotationOffsets(int userID, int offsetLeft, int offsetTop);

    void updateDistanceToDestination(double distance);

    void updateDestinationName(String name);

    void updateRelativeDestinationPosition(CompassDirection compassDirection);

    void updateHUDHeading(CompassDirection direction);
}
