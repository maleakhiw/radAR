package radar.radar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.UserLocation;
import radar.radar.Presenters.ARPresenter;
import radar.radar.Views.ARAnnotation;
import radar.radar.Views.ARView;

class CameraData {
    String cameraID;
    CameraCharacteristics cameraCharacteristics;

    public CameraData(String cameraID, CameraCharacteristics cameraCharacteristics) {
        this.cameraID = cameraID;
        this.cameraCharacteristics = cameraCharacteristics;
    }
}

public class ARActivity2 extends AppCompatActivity implements ARView {

    RelativeLayout mainRelativeLayout;
    HashMap<Integer, ARAnnotation> arAnnotations;   // userID -> ARAnnotation

    LayoutInflater inflater;

    ARPresenter presenter;

    TextureView previewView;
    Surface mSurface;
    CameraDevice mCameraDevice;
    CameraData mCameraData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar2);

        mainRelativeLayout = findViewById(R.id.ARview_layout_for_annotations);
        inflater = getLayoutInflater();
        arAnnotations = new HashMap<>();

        presenter = new ARPresenter(this);

        presenter.loadData();

        // stub: poll for data from server
//        ObservableInterval.

        previewView = findViewById(R.id.AR2_texture_view);

        setupCameraPrewiew();

    }

    // called when permissions request completed (event from Activity)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_FOR_CAMERA) {
            // if request is cancelled, the result arrays are empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted!
                setupCameraPrewiew();

            } else {
                // permission denied!
                // TODO show TextView in activity, say that permission was not granted
            }
        }
    }

    @Override
    public void removeAnnotation(int userID) {
        arAnnotations.remove(userID);
    }

    @Override
    public void getAnnotation(int userID) {
        arAnnotations.get(userID);
    }

    /**
     * Adds an annotation onscreen based on a UserLocation object.
     * @param userLocation location details of a user
     */
    @Override
    public void inflateARAnnotation(UserLocation userLocation) {
        int userID = userLocation.getUserID();

        // inflate a new layout
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.ar_annotation, null);
        System.out.println(layout);

        // add the layout to the view
        mainRelativeLayout.addView(layout);

        ARAnnotation arAnnotation = new ARAnnotation(userLocation, layout);

        // put it in the HashMap of annotations
        arAnnotations.put(userID, arAnnotation);

        // TODO unimplemented: calculate onscreen offsets from center using azimuth
    }

    /**
     * Moves the on-screen position of an AR annotation for a user.
     * @param userID user (key for the Map of ARAnnotations)
     * @param paddingLeft padding from the left of the parent layout
     * @param paddingTop padding from the top of the parent layout
     */
    @Override
    public void setAnnotationPadding(int userID, int paddingLeft, int paddingTop) {
        ARAnnotation annotation = arAnnotations.get(userID);
        if (annotation != null) {
            LinearLayout layout = annotation.getLayout();
//            LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(this);
            layout.setPadding(paddingLeft, paddingTop, 0, 0);
        } else {
            // TODO throw exception?
            Log.w("setLayoutPadding", "invalid key");
        }
    }

    // camera code adapted from https://willowtreeapps.com/ideas/camera2-and-you-leveraging-android-lollipops-new-camera/
    int REQUEST_FOR_CAMERA = 1;

    Observable<Surface> getSurfaceFromTextureView(TextureView previewView) {
        return Observable.create(observableEmitter -> {
            previewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    observableEmitter.onNext(new Surface(surfaceTexture));
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
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
                // NOTE cameraCharacteristics contains camera info (lens, sensor)
                // use to calculate FoV

                // check for permissions
                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // granted, open the camera and get the CameraDevice
                    // then create a capture session
                    CameraData cameraData = new CameraData(cameraID, cameraCharacteristics);
                    mCameraData = cameraData;
                    System.out.println(cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE));

                    // adjust camera size
                    Rect cameraResolution = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                    int width = cameraResolution.width();
                    int height = cameraResolution.height();
                    correctAspectRatio(width, height);

                    observableEmitter.onNext(cameraData);

                } else {    // PERMISSION_DENIED
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_FOR_CAMERA);
                }
            }
        });
    }


    void correctAspectRatio(int cameraWidth, int cameraHeight) {
        if (cameraWidth > cameraHeight) {
            // height should be the taller one, assuming potratit. If not, swap
            int tmp = cameraHeight;
            cameraHeight = cameraWidth;
            cameraWidth = tmp;
        }

        float aspectRatio = (float) cameraHeight / cameraWidth;

        // adjust
        int previewWidth = previewView.getMeasuredWidth();
        int previewHeight = previewView.getMeasuredHeight();

        previewView.setLayoutParams(new ConstraintLayout.LayoutParams(previewWidth, Math.round(previewWidth * aspectRatio)));

    }

    Observable<CameraDevice> getCameraDevice(@NonNull CameraManager cameraManager, @NonNull String cameraID) {
        return Observable.create(observableEmitter -> {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraID, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice cameraDevice) {
                        observableEmitter.onNext(cameraDevice);

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                    }

                    @Override
                    public void onError(@NonNull CameraDevice cameraDevice, int i) {

                    }
                }, new Handler(message -> false));

            }
        });
    }

    Observable<Boolean> createCaptureSession(@NonNull Surface previewSurface, @NonNull CameraDevice cameraDevice) {
        return Observable.create(observableEmitter -> {
            // create a new CaptureSession
            List<Surface> surfaces = Arrays.asList(previewSurface); // NOTE if we have other surfaces, include them too
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // we now have a capture session

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
                        }, new Handler(message -> false));

                        observableEmitter.onNext(true);
                    } catch (CameraAccessException e) {
                        observableEmitter.onError(e);
                    }

                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, new Handler(message -> {
                // TODO log
                return false;
            }));
        });
    }


    void setupCameraPrewiew() {
        // Using Camera2 as a camera preview API
        // 1. Get a Surface to draw on from the TextureView, then
        // 2. Fetch camera data, then
        // 3. Get capture device, then
        // 4. Create a new capture session

        // fetch camera data
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


        getSurfaceFromTextureView(previewView)
                .switchMap((surface) -> {   // similar to .then() chaining in JS
                    System.out.println(surface);
                    mSurface = surface;
                    return getCameraData(cameraManager);
                })
                .switchMap((cameraData) -> getCameraDevice(cameraManager, cameraData.cameraID))
                .switchMap((cameraDevice -> {
                    mCameraDevice = cameraDevice;
                    return createCaptureSession(mSurface, cameraDevice);
                }))
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("setupCameraPreview", "Error occurred");
                        System.out.println(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }









}
