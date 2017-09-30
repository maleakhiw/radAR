package radar.radar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import radar.radar.Models.UserLocation;
import radar.radar.Presenters.ARPresenter;
import radar.radar.Views.ARView;

class CameraData {
    String cameraID;
    CameraCharacteristics cameraCharacteristics;

    public CameraData(String cameraID, CameraCharacteristics cameraCharacteristics) {
        this.cameraID = cameraID;
        this.cameraCharacteristics = cameraCharacteristics;
    }
}

class ViewSize {
    int height;
    int width;

    public ViewSize(int height, int width) {
        this.height = height;
        this.width = width;
    }
}

public class ARActivity2 extends AppCompatActivity implements ARView {

    RelativeLayout mainRelativeLayout;
    HashMap<Integer, ARAnnotation> arAnnotations;   // userID -> ARAnnotation

    LayoutInflater inflater;

    ARPresenter presenter;

    TextureView previewView;
    Surface mSurface;
    SurfaceTexture mSurfaceTexture;
    CameraDevice mCameraDevice;
    CameraData mCameraData;
    CameraCaptureSession cameraCaptureSession;

    // camera info
    float[] focalLengths;
    SizeF sensorSize;

    // main relative layout size
    Observable<ViewSize> mainRelativeLayoutSizeObservable;
    int lastHeight;
    int lastWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar2);

        mainRelativeLayout = findViewById(R.id.ARview_layout_for_annotations);
        inflater = getLayoutInflater();
        arAnnotations = new HashMap<>();

        // add listener for when mainRelativeLayout changes size
        mainRelativeLayoutSizeObservable = Observable.create(emitter -> {
            mainRelativeLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (lastHeight != (bottom - top) || lastWidth != (right - left)) {
                        emitter.onNext(new ViewSize(bottom - top, right - left));
                        lastHeight = bottom - top;
                        lastWidth = right - left;
                    }
                }
            });
        });

        presenter = new ARPresenter(this);
        presenter.loadData();


        // stub: poll for data from server
//        ObservableInterval.

        previewView = findViewById(R.id.AR2_texture_view);

        setupCameraPrewiew();

        mainRelativeLayoutSizeObservable.subscribe(new Consumer<ViewSize>() {
            @Override
            public void accept(ViewSize viewSize) throws Exception {
                int height = viewSize.height;
                int width = viewSize.width;
                for (int userID: arAnnotations.keySet()) {
                    ARAnnotation annotation = arAnnotations.get(userID);
                    updateAnnotationOffsets(annotation, height, width);
                }
            }
        });

    }

    @Override
    public void onStop() {
        if (mCameraDevice != null) {
            System.out.println("Closing camera device");
            mCameraDevice.close();
        }
        mCameraData = null;
        if (cameraCaptureSession != null) {
            System.out.println("Closing capture session");
            cameraCaptureSession.close();
        }

        super.onStop();
    }

    @Override
    public void onStart() { // multitask to other apps
        if (cameraCaptureSession != null) {
            System.out.println("setupCameraPreview()");
            setupCameraPrewiew();
        }
        super.onStart();
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
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.ar_annotation, null);

        // add the layout to the view
        mainRelativeLayout.addView(layout);

        ARAnnotation arAnnotation = new ARAnnotation(userLocation, layout);

        // put it in the HashMap of annotations
        arAnnotations.put(userID, arAnnotation);

        setAnnotationOffsets(userID, 0, 0);
        // TODO unimplemented: calculate onscreen offsets from center using azimuth
    }

    // add more setters for other attributes of an annotation ltaer
    @Override
    public void setAnnotationMainText(int userID, String text) {
        if (arAnnotations != null) {
            ARAnnotation annotation = arAnnotations.get(userID);
            if (annotation != null) {
                TextView textView = annotation.getLayout().findViewById(R.id.ARAnnotation_TextView);
                textView.setText(text);
            }
        }
    }

    /**
     * Moves the on-screen position of an AR annotation for a user.
     * @param userID user (key for the Map of ARAnnotations)
     * @param offsetLeft margin from the left of the parent layout
     * @param offsetTop margin from the top of the parent layout
     */
    @Override
    public void setAnnotationOffsets(int userID, int offsetLeft, int offsetTop) {
        ARAnnotation annotation = arAnnotations.get(userID);
        if (annotation != null) {
            annotation.offsetX = offsetLeft;
            annotation.offsetY = offsetTop;
        } else {
            // TODO throw exception?
            Log.w("setLayoutPadding", "invalid key");
        }
    }

    void updateAnnotationOffsets(ARAnnotation annotation, int height, int width) {
        if (annotation != null) {
            RelativeLayout layout = annotation.getLayout();
            RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            int midpointHeight = height/2;
            int midpointWidth = width/2;

            // TODO center relative to annotation size
            int annotationHeight = layout.getHeight();
            int annotationWidth = layout.getWidth();


            layoutParam.setMargins(midpointWidth + annotation.offsetX - annotationWidth/2, midpointHeight + annotation.offsetY - annotationHeight/2, 0, 0);
//            layoutParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);   // matches above
            layout.setLayoutParams(layoutParam);

        } else {
            // TODO throw exception?
            Log.w("setLayoutPadding", "invalid key");
        }
    }

    // camera code adapted from https://willowtreeapps.com/ideas/camera2-and-you-leveraging-android-lollipops-new-camera/
    int REQUEST_FOR_CAMERA = 1;

    Observable<Surface> getSurfaceFromTextureView(TextureView previewView) {
        System.out.println("getSurfaceFromTextureView()");
        return Observable.create(observableEmitter -> {
            if (mSurfaceTexture != null) {
                observableEmitter.onNext(new Surface(mSurfaceTexture));
            }

            previewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    System.out.println(surfaceTexture);
                    mSurfaceTexture = surfaceTexture;
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
                System.out.println("cameraID");
                System.out.println(cameraID);
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

    void calculateFocalLengthSensorSize(CameraCharacteristics characteristics) {
        focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
    }




    Observable<CameraDevice> getCameraDevice(@NonNull CameraManager cameraManager, @NonNull String cameraID) {
        return Observable.create(observableEmitter -> {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
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
                System.out.println("createCaptureSession2");
                System.out.println(message);
                return false;
            }));
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

        previewView.setLayoutParams(new ConstraintLayout.LayoutParams(previewWidth, Math.round(previewWidth * aspectRatio)));

        // also make the annotations overlay size to match the camera surface size
        mainRelativeLayout.setLayoutParams(new ConstraintLayout.LayoutParams(previewWidth, Math.round(previewWidth * aspectRatio)));

    }


    void setupCameraPrewiew() {
        // Using Camera2 as a camera preview API
        // 1. Get a Surface to draw on from the TextureView, then
        // 2. Fetch camera data, then
        // 3. Get capture device, then
        // 4. Create a new capture session

        // fetch camera data
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        System.out.println("setupCameraPreview() called");


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
                .subscribe(new Observer<CameraCaptureSession>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CameraCaptureSession session) {
                        cameraCaptureSession = session;
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
