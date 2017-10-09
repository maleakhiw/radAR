package radar.radar.Services;

import radar.radar.Models.Android.CompassDirection;
import static radar.radar.Models.Android.CompassDirection.*;

/**
 * Created by kenneth on 30/9/17.
 */

public class LocationTransformations {

    double hPixelsPerDegree; // horizontal pixels per degree
    double vPixelsPerDegree; // vertical pixels per degree

    /**
     * Constructor
     * @param hPixelsPerDegree number of horizontal pixels per degree of the field of view
     * @param vPixelsPerDegree number of vertical pixels per degree of the field of view
     */
    public LocationTransformations(double hPixelsPerDegree, double vPixelsPerDegree) {
        this.hPixelsPerDegree = this.hPixelsPerDegree;
        this.vPixelsPerDegree = this.vPixelsPerDegree;
    }

    public void sethPixelsPerDegree(double hPixelsPerDegree) {
        this.hPixelsPerDegree = hPixelsPerDegree;
    }

    public void setvPixelsPerDegree(double vPixelsPerDegree) {
        this.vPixelsPerDegree = vPixelsPerDegree;
    }

    /**
     * Calculates the shortest angles between 2 angles.
     * Both angles should be between 0 and 360 degrees.
     * @param angle1 angle in degrees
     * @param angle2 angle in degrees
     * @return
     */
    public static double deltaAngle(double angle1, double angle2) {
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
//        System.out.println(deltaAngle(azimuth, heading));
//        Log.d("deltaAngle", String.valueOf(deltaAngle(azimuth, heading)));
//        System.out.println(hPixelsPerDegree);
        return (int) -Math.round(deltaAngle(azimuth, heading) * hPixelsPerDegree);
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
        return (int) -Math.round(offset + arPositionOffset);
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
    public static double bearingBetween(double lat1, double lon1, double lat2, double lon2) {
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

    /**
     * Calculates the distance between two latitudes and longitudes
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @param unit
     * @return
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        // adapted from https://stackoverflow.com/a/3694410
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    public static CompassDirection getDeltaAngleCompassDirection(double azimuth, double heading) {
        double deltaAngle = deltaAngle(azimuth, heading);
        if (deltaAngle < 0) {
            deltaAngle += 360;
        }
        if (deltaAngle >= 0 && deltaAngle < 22.5 || deltaAngle >= 337.5 && deltaAngle < 360) {
            return NORTH;
        } else if (deltaAngle >= 22.5 && deltaAngle < 67.5) {
            return NORTHEAST;
        } else if (deltaAngle >= 67.5 && deltaAngle < 112.5) {
            return EAST;
        } else if (deltaAngle >= 112.5 && deltaAngle < 157.5) {
            return SOUTHEAST;
        } else if (deltaAngle >= 157.5 && deltaAngle < 202.5) {
            return SOUTH;
        } else if (deltaAngle >= 202.5 && deltaAngle < 247.5) {
            return SOUTHWEST;
        } else if (deltaAngle >= 247.5 && deltaAngle < 292.5) {
            return WEST;
        } else {
            return NORTHWEST;
        }
    }

    public static CompassDirection getCompassDirection(double degree) {
        if (degree < 0) {
            degree += 360;
        }
        if (degree >= 0 && degree < 22.5 || degree >= 337.5 && degree < 360) {
            return NORTH;
        } else if (degree >= 22.5 && degree < 67.5) {
            return NORTHEAST;
        } else if (degree >= 67.5 && degree < 112.5) {
            return EAST;
        } else if (degree >= 112.5 && degree < 157.5) {
            return SOUTHEAST;
        } else if (degree >= 157.5 && degree < 202.5) {
            return SOUTH;
        } else if (degree >= 202.5 && degree < 247.5) {
            return SOUTHWEST;
        } else if (degree >= 247.5 && degree < 292.5) {
            return WEST;
        } else {
            return NORTHWEST;
        }
    }
    
    public static String getRelativeDestinationString(CompassDirection compassDirection) {
        switch (compassDirection) {
            case NORTH:
                return "front";
            case NORTHEAST:
                return "front";
            case EAST:
                return "right";
            case SOUTHEAST:
                return "right";
            case SOUTH:
                return "back";
            case SOUTHWEST:
                return "left";
            case WEST:
                return "left";
            case NORTHWEST:
                return "front";
            default:
                return "invalid";
        }
    }
}
