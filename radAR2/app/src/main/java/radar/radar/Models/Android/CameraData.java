package radar.radar.Models.Android;

import android.hardware.camera2.CameraCharacteristics;
import android.util.SizeF;

/**
 * Data model for Camera, used in AR and map navigation
 */
public class CameraData {
    String cameraID;
    CameraCharacteristics cameraCharacteristics;
    double horizontalFov;
    double verticalFov;

    /**
     * Getter
     */
    public CameraCharacteristics getCameraCharacteristics() {
        return cameraCharacteristics;
    }

    public double getHorizontalFov() {
        return horizontalFov;
    }

    public double getVerticalFov() {
        return verticalFov;
    }

    public String getCameraID() {
        return cameraID;
    }

    public float getSensorWidth() {
        SizeF sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        return sensorSize.getWidth();
    }

    public float getSensorHeight() {
        SizeF sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        return sensorSize.getHeight();
    }

    /**
     * Calculate field of view of the camera
     */
    private void calcFov() {
        float focalLength = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];
        SizeF sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        float sensorHeight = sensorSize.getHeight();
        float sensorWidth = sensorSize.getWidth();

        horizontalFov = Math.toDegrees(2 * Math.atan(sensorWidth / (focalLength * 2)));
        verticalFov = Math.toDegrees(2 * Math.atan(sensorHeight / (focalLength * 2)));
    }

    /**
     * Constructor
     */
    public CameraData(String cameraID, CameraCharacteristics cameraCharacteristics) {
        this.cameraID = cameraID;
        this.cameraCharacteristics = cameraCharacteristics;
        calcFov();
    }
}
