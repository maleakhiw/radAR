package radar.radar.Listeners;

/**
 * Implement this interface if you want CameraService to call you back
 * with camera data.
 */
public interface CameraDataListener {
    void correctAspectRatio(float cameraWidth, float cameraHeight);
}
