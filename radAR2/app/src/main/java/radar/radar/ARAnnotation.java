package radar.radar;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout;

import radar.radar.Models.UserLocation;

/**
 * Created by kenneth on 28/9/17.
 */

public class ARAnnotation {
    UserLocation userLocation;
    RelativeLayout layout;

    Button button;

    int offsetX;
    int offsetY;

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public Button getButton() {
        return button;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public void setLayout(RelativeLayout layout) {
        this.layout = layout;
    }

    public ARAnnotation(UserLocation userLocation, RelativeLayout layout, Button button) {

        this.userLocation = userLocation;
        this.layout = layout;
        this.button = button;

    }
}
