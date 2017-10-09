package radar.radar.Models.Android;

import android.hardware.camera2.CameraCharacteristics;
import android.util.SizeF;

/**
 * Created by kenneth on 30/9/17.
 */


public class CameraData {
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

    String cameraID;
    CameraCharacteristics cameraCharacteristics;
    double horizontalFov;
    double verticalFov;

    private void calcFov() {
        float focalLength = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];
        SizeF sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        float sensorHeight = sensorSize.getHeight();
        float sensorWidth = sensorSize.getWidth();

        horizontalFov = Math.toDegrees(2 * Math.atan(sensorWidth / (focalLength * 2)));
        verticalFov = Math.toDegrees(2 * Math.atan(sensorHeight / (focalLength * 2)));
    }

    public float getSensorWidth() {
        SizeF sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        return sensorSize.getWidth();
    }

    public float getSensorHeight() {
        SizeF sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        return sensorSize.getHeight();
    }

    public CameraData(String cameraID, CameraCharacteristics cameraCharacteristics) {
        this.cameraID = cameraID;
        this.cameraCharacteristics = cameraCharacteristics;
        calcFov();
    }
}
