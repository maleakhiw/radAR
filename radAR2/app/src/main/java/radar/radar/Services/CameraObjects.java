package radar.radar.Services;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.view.Surface;

import radar.radar.Models.Android.CameraData;

/**
 * Created by kenneth on 7/10/17.
 */

public class CameraObjects {
    CameraData cameraData;
    SurfaceTexture surfaceTexture;
    Surface surface;
    CameraDevice cameraDevice;
    CameraCaptureSession captureSession;

    public CameraData getCameraData() {
        return cameraData;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public Surface getSurface() {
        return surface;
    }

    public CameraDevice getCameraDevice() {
        return cameraDevice;
    }

    public CameraCaptureSession getCaptureSession() {
        return captureSession;
    }

    public CameraObjects(CameraData cameraData, SurfaceTexture surfaceTexture, Surface surface, CameraDevice cameraDevice, CameraCaptureSession captureSession) {
        this.cameraData = cameraData;
        this.surfaceTexture = surfaceTexture;
        this.surface = surface;
        this.cameraDevice = cameraDevice;
        this.captureSession = captureSession;
    }
}
