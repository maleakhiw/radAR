package radar.radar.Models.Android;

import android.widget.Button;
import android.widget.RelativeLayout;

import radar.radar.Models.Domain.UserLocation;


public class ARAnnotation {
    UserLocation userLocation;
    RelativeLayout layout;

    Button button;

    int offsetX;
    int offsetY;


    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }


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
