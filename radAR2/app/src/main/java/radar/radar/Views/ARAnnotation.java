package radar.radar.Views;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout;

import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 28/9/17.
 */

public class ARAnnotation {
    UserLocation userLocation;
    LinearLayout layout;

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public void setLayout(LinearLayout layout) {
        this.layout = layout;
    }

    public ARAnnotation(UserLocation userLocation, LinearLayout layout) {

        this.userLocation = userLocation;
        this.layout = layout;

    }
}
