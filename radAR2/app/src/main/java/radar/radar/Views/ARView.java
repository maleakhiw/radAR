package radar.radar.Views;

import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 28/9/17.
 */

public interface ARView {
    void removeAnnotation(int userID);

    void getAnnotation(int userID);

    void inflateARAnnotation(UserLocation userLocation);

    // add more setters for other attributes of an annotation ltaer
    void setAnnotationMainText(int userID, String text);

    void setAnnotationMargins(int userID, int marginLeft, int marginTop);
}
