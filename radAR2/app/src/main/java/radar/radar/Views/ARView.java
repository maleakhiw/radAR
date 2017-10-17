package radar.radar.Views;

import radar.radar.Models.Android.ARAnnotation;
import radar.radar.Models.Android.CompassDirection;
import radar.radar.Models.Domain.UserLocation;

/**
 * Created by kenneth on 28/9/17.
 */

public interface ARView {
    void requestLocationPermissions();

    void showToast(String toast);

    boolean isInflated(int userID);

    void removeAnnotation(int userID);

    ARAnnotation getAnnotation(int userID);

    void inflateARAnnotation(UserLocation userLocation);

    // add more setters for other attributes of an annotation ltaer
    void setAnnotationMainText(int userID, String text);

    void setAnnotationOffsets(int userID, int offsetLeft, int offsetTop);

    void updateDistanceToDestination(double distance);

    void updateDestinationName(String name);

    void updateRelativeDestinationPosition(CompassDirection compassDirection);

    int getAnnotationHeight(int userID);

    int getAnnotationWidth(int userID);

    void updateHUDHeading(CompassDirection direction);
}
