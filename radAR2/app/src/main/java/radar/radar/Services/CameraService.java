package radar.radar.Services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Android.CameraData;
import radar.radar.Listeners.CameraDataListener;

/**
 * Created by kenneth on 7/10/17.
 */

class SurfaceAndSurfaceTexture {
    Surface surface;
    SurfaceTexture surfaceTexture;

    public SurfaceAndSurfaceTexture(Surface surface, SurfaceTexture surfaceTexture) {
        this.surface = surface;
        this.surfaceTexture = surfaceTexture;
    }
}

public class CameraService {
    // camera code adapted from https://willowtreeapps.com/ideas/camera2-and-you-leveraging-android-lollipops-new-camera/
    CameraManager cameraManager;
    Activity activity;
    public CameraService(CameraManager cameraManager, Activity activity) {
        this.cameraManager = cameraManager;
        this.activity = activity;
    }

    SurfaceTexture mSurfaceTexture;
    CameraData mCameraData;
    Surface mSurface;
    CameraDevice mCameraDevice;
    CameraCaptureSession mCameraCaptureSession;

    /**
     * Sets up a camera preview using Camera2 API.
     * @return camera objects for a camera preview
     */
    public Observable<CameraObjects> getCameraObjects(TextureView previewView) {
        System.out.println("getCameraObjects()");
        // 1. Get a Surface to draw on from the TextureView, then
        // 2. Fetch camera data, then
        // 3. Get capture device, then
        // 4. Create a new capture session

        return Observable.create(emitter -> {
            getSurfaceFromTextureView(previewView)
            .switchMap((surfaceAndSurfaceTexture) -> {
                mSurface = surfaceAndSurfaceTexture.surface;
                mSurfaceTexture = surfaceAndSurfaceTexture.surfaceTexture;
                System.out.println("got surface and surfaceTexture");
                return getCameraData(cameraManager);
            })
            .switchMap((cameraData) -> {
                mCameraData = cameraData;
                if (activity instanceof CameraDataListener) {
                    ((CameraDataListener) activity).correctAspectRatio(cameraData.getSensorWidth(), cameraData.getSensorHeight());
                }

                System.out.println("got cameraData");
                return getCameraDevice(cameraManager, cameraData.getCameraID());
            })
            .switchMap((cameraDevice -> {
                mCameraDevice = cameraDevice;
                System.out.println("got cameraDevice");
                return createCaptureSession(mSurface, cameraDevice);
            }))
            .subscribe(new Observer<CameraCaptureSession>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(CameraCaptureSession cameraCaptureSession) {
                    System.out.println("got cameraCaptureSession");
                    mCameraCaptureSession = cameraCaptureSession;
                    emitter.onNext(new CameraObjects(mCameraData, mSurfaceTexture, mSurface, mCameraDevice, cameraCaptureSession));
                }

                @Override
                public void onError(Throwable e) {
                    emitter.onError(e); // propagate the error
                }

                @Override
                public void onComplete() {

                }
            });
        });
    }

    public void cleanup() {
        if (mCameraDevice != null) {
            System.out.println("Closing camera device");
            mCameraDevice.close();
        }
        if (mCameraCaptureSession != null) {
            System.out.println("Closing capture session");
            mCameraCaptureSession.close();
        }
    }


    /**
     * Resumes the camera preview.
     * @return
     */
    public Observable<CameraObjects> resumeCameraPreview() {
        return Observable.create(emitter -> {
            if (mCameraCaptureSession != null) {
                System.out.println("setupCameraPreviewAndPresenter()");
                getCameraDevice(cameraManager, mCameraData.getCameraID())
                .switchMap((cameraDevice -> {
                    mCameraDevice = cameraDevice;
                    return createCaptureSession(mSurface, cameraDevice);
                }))
                .subscribe(new Observer<CameraCaptureSession>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CameraCaptureSession cameraCaptureSession) {
                        mCameraCaptureSession = cameraCaptureSession;
                        emitter.onNext(new CameraObjects(mCameraData, mSurfaceTexture, mSurface, mCameraDevice, cameraCaptureSession));
                    }

                    @Override
                    public void onError(Throwable e) {
                        emitter.onError(e); // propagate the error
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

        });
    }

    Observable<SurfaceAndSurfaceTexture> getSurfaceFromTextureView(TextureView previewView) {
        System.out.println("getSurfaceFromTextureView()");
        return Observable.create(observableEmitter -> {
            if (mSurfaceTexture != null) {
                observableEmitter.onNext(new SurfaceAndSurfaceTexture(new Surface(mSurfaceTexture), mSurfaceTexture));
            }

            previewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    observableEmitter.onNext(new SurfaceAndSurfaceTexture(new Surface(surfaceTexture), surfaceTexture));
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    if (mCameraDevice != null) {
                        mCameraDevice.close();
                    }
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            });
        });
    }

    Observable<CameraData> getCameraData(@NonNull CameraManager cameraManager) {
        return Observable.create(observableEmitter -> {
            String[] cameraIDs = cameraManager.getCameraIdList();   // first camera
            if (cameraIDs.length > 0) {
                // take the 1st camera. Assume to be rear camera. TODO remove assumption, allow selection
                String cameraID = cameraIDs[0];
                System.out.println("cameraID");
                System.out.println(cameraID);
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);

                // NOTE cameraCharacteristics contains camera info (lens, sensor)
                // use to calculate FoV


                // check for permissions
                int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // granted, open the camera and get the CameraDevice
                    // then create a capture session
                    observableEmitter.onNext(new CameraData(cameraID, cameraCharacteristics));
                } else {    // PERMISSION_DENIED
                    observableEmitter.onError(new Throwable("REQUEST_CAMERA_PERMISSIONS"));
                }
            }
        });
    }

    Observable<CameraDevice> getCameraDevice(@NonNull CameraManager cameraManager, @NonNull String cameraID) {
        return Observable.create(observableEmitter -> {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraID, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice cameraDevice) {
                        System.out.println(cameraDevice);
                        observableEmitter.onNext(cameraDevice);

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                    }

                    @Override
                    public void onError(@NonNull CameraDevice cameraDevice, int i) {

                    }
                }, new Handler(message -> {
                    System.out.println("getCameraDevice");
                    System.out.println(message);
                    return false;
                }));
            } else {
                observableEmitter.onError(new Throwable("REQUEST_CAMERA_PERMISSIONS"));
            }
        });
    }

    Observable<CameraCaptureSession> createCaptureSession(@NonNull Surface previewSurface, @NonNull CameraDevice cameraDevice) {
        return Observable.create(observableEmitter -> {
            // create a new CaptureSession
            List<Surface> surfaces = Arrays.asList(previewSurface); // NOTE if we have other surfaces, include them too
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // we now have a capture session
                    System.out.println(cameraCaptureSession);

                    // now we have full control over image capture
                    CaptureRequest request;
                    try {
                        CaptureRequest.Builder request2 = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        request2.addTarget(previewSurface);
                        request = request2.build();

                        List<CaptureRequest> requestList = new ArrayList<>();
                        requestList.add(request);

                        // set capture options here

                        // set the session to continuously get data from the camera
                        cameraCaptureSession.setRepeatingBurst(requestList, new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                super.onCaptureCompleted(session, request, result);
                            }
                        }, new Handler(message -> {
                            System.out.println("createCaptureSession");
                            System.out.println(message);
                            return false;
                        }));

                        observableEmitter.onNext(cameraCaptureSession);
                    } catch (CameraAccessException e) {
                        observableEmitter.onError(e);
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, new Handler(message -> {
                System.out.println(message);
                return false;
            }));
        });
    }
}
