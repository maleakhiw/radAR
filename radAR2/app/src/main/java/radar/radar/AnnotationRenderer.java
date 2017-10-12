package radar.radar;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.AnnotationData;
import radar.radar.Models.Domain.DestinationLocation;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Domain.UserLocation;
import radar.radar.Services.LocationTransformations;
import radar.radar.Views.ARView;

/**
 * Created by kenneth on 12/10/17.
 */

public class AnnotationRenderer {
    float latCurrent;
    float lonCurrent;
    float azimuth;
    float pitch;

    ARView arView;

    ArrayList<UserLocation> userLocations;
    HashMap<Integer, User> usersDetails;
    LocationTransformations locationTransformations;

    HashMap<Integer, AnnotationData> annotations;

    public AnnotationRenderer(float latCurrent, float lonCurrent, float azimuth, float pitch, ArrayList<UserLocation> userLocations, HashMap<Integer, User> usersDetails, LocationTransformations locationTransformations, ARView arView) {
        this.latCurrent = latCurrent;
        this.lonCurrent = lonCurrent;
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.userLocations = userLocations;
        this.usersDetails = usersDetails;
        this.locationTransformations = locationTransformations;
        this.arView = arView;

        annotations = new HashMap<>();
    }

    static boolean checkIfOverlapping(AnnotationData current, AnnotationData checkedAgainst) {
        // only check x axis (horizontal)
        int currentStart = current.offsetX; // A
        int currentEnd = currentStart + current.width;  // B
        int checkedStart = checkedAgainst.offsetX;  // C
        int checkedEnd = checkedStart + checkedAgainst.width;   // D
        int currentLevel = current.stackingLevel;
        int checkedLevel = checkedAgainst.stackingLevel;

        // overlapping and in same level, check both ways
        return !( (currentEnd < checkedStart && currentEnd < checkedEnd) || (checkedEnd < currentStart && checkedEnd < currentEnd) )
                && currentLevel == checkedLevel;
    }


    public void renderOne(int userID, double latUser, double lonUser, UserLocation userLocation, double azimuth, double pitch) {
        double bearing = LocationTransformations.bearingBetween(latUser, lonUser, userLocation.getLat(), userLocation.getLon());

        // get xOffset and yOffset
        int xOffset = locationTransformations.xOffset(bearing, azimuth);
        int yOffset = locationTransformations.yOffset(pitch, 0);
        int height = arView.getAnnotationHeight(userID);
        int width = arView.getAnnotationWidth(userID);

        AnnotationData current = new AnnotationData(xOffset, yOffset, width, height, 0);

        // iterate over all the existing annotations and check for overlaps
        boolean overlapping = true;
        while (overlapping == true) {   // TODO infinite loops are a bad idea, give a maximum number, say, 100
            for (AnnotationData annotation : annotations.values()) {
                if (checkIfOverlapping(current, annotation)) {
                    yOffset -= height;  // negative goes towards top of screen
                    current.stackingLevel += 1;
                }
                continue;   // go back to start of while loop
            }
            overlapping = false;
        }

        arView.setAnnotationOffsets(userID, xOffset, yOffset);
        annotations.put(userID, current);

    }

    public void setAnnotationText(int userID, UserLocation annotationLatLon) {
        if (annotationLatLon instanceof DestinationLocation) {
            arView.setAnnotationMainText(userID, ((DestinationLocation) annotationLatLon).name);
        } else {
            // set view properties (labels, profile pictures)
            arView.setAnnotationMainText(userID, usersDetails.get(userID).firstName);
        }
    }

    public void render() {
        // put all the annotations into place
        for (UserLocation annotationLatLon: userLocations) {
            int userID = annotationLatLon.getUserID();
            setAnnotationText(userID, annotationLatLon);

            // TODO profile pictures

            // inflate the view, if it has not been inflated
            if (!arView.isInflated(userID)) {
                arView.inflateARAnnotation(annotationLatLon);
                setAnnotationText(userID, annotationLatLon);
            }
//            render(userID, latCurrent, lonCurrent, annotationLatLon, azimuth, pitch);
            renderOne(userID, latCurrent, lonCurrent, annotationLatLon, azimuth, pitch);
        }
    }
}
