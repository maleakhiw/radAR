package radar.radar.Services;

/**
 * Created by kenneth on 30/9/17.
 */

public class LocationTransformations {

    double hPixelsPerDegree; // horizontal pixels per degree
    double vPixelsPerDegree; // vertical pixels per degree

    public LocationTransformations(double hPixelsPerDegree, double vPixelsPerDegree) {
        this.hPixelsPerDegree = hPixelsPerDegree;
        this.vPixelsPerDegree = vPixelsPerDegree;
    }

    /**
     * Calculates the shortest angles between 2 angles.
     * Both angles should be between 0 and 360 degrees.
     * @param angle1 angle in degrees
     * @param angle2 angle in degrees
     * @return
     */
    public double deltaAngle(double angle1, double angle2) {
        double deltaAngle = angle1 - angle2;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle < -180) {
            deltaAngle += 360;
        }
        return deltaAngle;
    }

    /**
     * Calculates the offset from the center of the layout of an AR annotation.
     * @param heading heading of the device (e.g. from compass/GPS)
     * @param azimuth angle between where the device is heading and the target
     * @return horizontal offset from center of layout
     */
    public int xOffset(double heading, double azimuth) {
        return (int) Math.round(deltaAngle(azimuth, heading) * hPixelsPerDegree);
    }



    /**
     * Calculates the offset from the center of the layout of an AR annotation.
     * @param pitch pitch of the device (e.g. from gyroscope/accelerometer)
     * @param arPositionOffset any offsets to be added after the fact
     *                         e.g. to layout annotations so they don't overlap
     * @return vertical offset from center of layout
     */
    public int yOffset(double pitch, double arPositionOffset) {
        double offset = pitch * vPixelsPerDegree;
        return (int) Math.round(offset + arPositionOffset);
    }

    /**
     * Calculates the bearing between 2 points (precise)
     * @param lat1 latitude of the 1st point, in degrees
     * @param lon1 longitude of the 1st point, in degrees
     * @param lat2 latitude of the 2nd point, in degrees
     * @param lon2 longitude of the 2nd point, in degrees
     * @return azimuth between the 2 points
     */
    // http://mathforum.org/library/drmath/view/55417.html
    public double bearingBetween(double lat1, double lon1, double lat2, double lon2) {
        double azimuth = 0;
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double deltaLon = lon2 - lon1;  // "imagine" lon1 to be 0 degrees to simplify calculations

        double y = Math.sin(deltaLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLon);
        double bearingRad = Math.atan2(y, x);

        azimuth = Math.toDegrees(bearingRad);
        if (azimuth < 0) {
            azimuth += 360;
        }

        return azimuth;

    }
}
