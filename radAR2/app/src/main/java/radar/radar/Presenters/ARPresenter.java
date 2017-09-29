package radar.radar.Presenters;



import java.util.Date;

import radar.radar.ARActivity2;
import radar.radar.Models.UserLocation;
import radar.radar.Views.ARView;

/**
 * Created by kenneth on 28/9/17.
 */

public class ARPresenter {
    ARView arView;

    public ARPresenter(ARView arView) {
        this.arView = arView;
    }

    public void loadData() {
        // for now, return fake data

        UserLocation userLocation1 = new UserLocation(1, 0.1, 0.2, 0.1, 2, new Date());
        UserLocation userLocation2 = new UserLocation(2, 0.2, 0.3, 0.3, 3, new Date());
        arView.inflateARAnnotation(userLocation1);
        arView.inflateARAnnotation(userLocation2);

        arView.setAnnotationPadding(2, 200, 16);

        arView.setAnnotationMainText(2, "I can now change the text");

        // to remove an annotation, call ARView.removeAnnotationById

    }
}
