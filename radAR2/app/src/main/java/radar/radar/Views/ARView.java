package radar.radar.Views;

import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 28/9/17.
 */

public interface ARView {
    void removeAnnotation(int userID);

    void getAnnotation(int userID);

    void inflateARAnnotation(UserLocation userLocation);

    void setAnnotationPadding(int userID, int paddingLeft, int paddingTop);
}
