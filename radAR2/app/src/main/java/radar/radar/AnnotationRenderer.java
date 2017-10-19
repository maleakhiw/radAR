package radar.radar;

import java.util.ArrayList;
import java.util.HashMap;

import radar.radar.Models.Domain.AnnotationData;
import radar.radar.Models.Domain.DestinationLocation;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Domain.UserLocation;
import radar.radar.Services.LocationTransformations;
import radar.radar.Views.ARView;

import static radar.radar.Presenters.ARPresenter.DESTINATION_ID;

/**
 * Created by kenneth on 12/10/17.
 */

public class AnnotationRenderer {
    double latCurrent;
    double lonCurrent;
    double azimuth;
    double pitch;

    ARView arView;

    ArrayList<UserLocation> userLocations;
    HashMap<Integer, User> usersDetails;
    LocationTransformations locationTransformations;

    HashMap<Integer, AnnotationData> annotations;

    public AnnotationRenderer(double latCurrent, double lonCurrent, double azimuth, double pitch, ArrayList<UserLocation> userLocations, HashMap<Integer, User> usersDetails, LocationTransformations locationTransformations, ARView arView) {
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

    AnnotationData getAnnotationData(int userID, double latUser, double lonUser, UserLocation userLocation, double azimuth, double pitch) {
        double bearing = LocationTransformations.bearingBetween(latUser, lonUser, userLocation.getLat(), userLocation.getLon());

        // get xOffset and yOffset
        int xOffset = locationTransformations.xOffset(bearing, azimuth);
        int yOffset = locationTransformations.yOffset(pitch, 0);
        int height = arView.getAnnotationHeight(userID);
        int width = arView.getAnnotationWidth(userID);

        return new AnnotationData(userID, xOffset, yOffset, width, height, 0);
    }


    void renderOne(int userID) {

        AnnotationData current = annotations.get(userID);
        int xOffset = current.offsetX;
        int yOffset = current.offsetY;
        int height = current.height;

        // iterate over existing annotations, check for overlaps
        for (AnnotationData annotation : annotations.values()) {
            System.out.println("Checking " + ((Integer) userID).toString() + " against " + ((Integer) annotation.userID).toString());
            if (userID == annotation.userID) {
                continue;   // don't check against yourself!
            }
            boolean overlapping = true;
            while (overlapping) {   // until we do not overlap, keep moving up
                if (checkIfOverlapping(current, annotation)) {
                    yOffset -= 1.5 * height;  // negative goes towards top of screen
                    current.stackingLevel += 1;
                } else {
                    overlapping = false;
                }
            }
        }

        System.out.println(((Integer) userID).toString() + ": " + ((Integer) current.stackingLevel).toString());

        arView.setAnnotationOffsets(userID, xOffset, yOffset);

        annotations.put(userID, current);   // update the current "memory"

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
        for (UserLocation annotationLocation: userLocations) {
            int userID = annotationLocation.getUserID();
            setAnnotationText(userID, annotationLocation);

            // TODO profile pictures

            // inflate the view, if it has not been inflated
            if (!arView.isInflated(userID)) {
                arView.inflateARAnnotation(annotationLocation);
                setAnnotationText(userID, annotationLocation);
            }

            annotations.put(userID, getAnnotationData(userID, latCurrent, lonCurrent, annotationLocation, azimuth, pitch));

        }

        for (UserLocation annotationLatLon: userLocations) {
            int userID = annotationLatLon.getUserID();
            renderOne(userID);
        }
    }
}
