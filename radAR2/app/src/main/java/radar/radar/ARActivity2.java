package radar.radar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import radar.radar.Models.UserLocation;
import radar.radar.Presenters.ARPresenter;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationApi;
import radar.radar.Services.LocationService;
import radar.radar.Services.LocationTransformations;
import radar.radar.Views.ARView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


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

    // HUD Views
    TextView distanceToDestination;
    TextView distanceUnit;
    TextView destinationName;
    TextView relativeCompassDirection;
    TextView heading;

    // main relative layout size
    Observable<ViewSize> mainRelativeLayoutSizeObservable;
    ViewSize lastViewSize;
    int lastHeight;
    int lastWidth;

    int groupID;

    static final int REQUEST_FOR_CAMERA = 1;
    static final int REQUEST_FOR_LOCATION = 2;

    @Override
    protected void onPause() {
        super.onPause();
        // TODO call presenter to pass to sensorService
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO call presenter to pass to sensorService
    }

    /**
     * Asks for permissions to access fine (GPS) location from the user.
     */
    @Override
    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FOR_LOCATION);
    }

    CameraManager cameraManager;

    LocationService locationService;
    GroupsService groupsService;
    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar2);

        groupID = 1;    // TODO get from bundle
                        // TODO getGroup and check if it exists

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // TODO also ask for location permissions

        mainRelativeLayout = findViewById(R.id.ARview_layout_for_annotations);
        inflater = getLayoutInflater();
        arAnnotations = new HashMap<>();

        // HUD views
        destinationName = findViewById(R.id.HUD_destination_name);
        distanceToDestination = findViewById(R.id.HUD_distance_to_dest);
        distanceUnit = findViewById(R.id.HUD_distance_unit);
        relativeCompassDirection = findViewById(R.id.HUD_relative_compass_direction);
        heading = findViewById(R.id.HUD_heading);

        // add listener for when mainRelativeLayout changes size
        mainRelativeLayoutSizeObservable = Observable.create(emitter -> {
            mainRelativeLayout.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                if (lastHeight != (bottom - top) || lastWidth != (right - left)) {
                    lastViewSize = new ViewSize(bottom - top, right - left);
                    emitter.onNext(lastViewSize);
                    lastHeight = bottom - top;
                    lastWidth = right - left;
                }
            });
        });

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new HttpLoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://35.185.35.117/api/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .build();

        LocationApi locationApi = retrofit.create(LocationApi.class);
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationService = new LocationService(locationApi, this, fusedLocationClient);
        groupsService = new GroupsService(this, groupsApi);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        previewView = findViewById(R.id.AR2_texture_view);

        setupCameraPreviewAndPresenter();   // TODO refactor to camera service

        // observable for layout size changes
        mainRelativeLayoutSizeObservable.subscribe(viewSize -> {
//            System.out.println("viewSize got updated");
            for (int userID: arAnnotations.keySet()) {
                ARAnnotation annotation = arAnnotations.get(userID);
                updateAnnotationOffsets(annotation, lastHeight, lastWidth);
            }
        });

    }

    void setupPresenterImpl(double hFov, double vFov, int width, int height, int groupID) {
        System.out.println("setupPresenterImpl");
        // create a new presenter
        LocationTransformations locationTransformations = new LocationTransformations(width/hFov, height/vFov);
        if (presenter == null) {
            presenter = new ARPresenter(this, locationService, groupsService, sensorManager, locationTransformations, groupID);
        }
        presenter.updateData(width/hFov, height/vFov);
    }

    void setupPresenter() {
        System.out.println("setupPresenter()");
        if (mCameraData != null) {
            System.out.println("cameraData not null @ setupPresenter");
            double hFov = mCameraData.horizontalFov;
            double vFov = mCameraData.verticalFov;
            int width = lastViewSize.width;
            int height = lastViewSize.height;
            setupPresenterImpl(hFov, vFov, width, height, groupID);
            return;
        } else {
            System.out.println("cameraData null @ setupPresenter");
        }

        // update view, create a new presenter or update the data
        Observable.combineLatest(mainRelativeLayoutSizeObservable, cameraDataObservable, (viewSize, cameraData) -> {
            double hFov = cameraData.horizontalFov;
            double vFov = cameraData.verticalFov;
            int width = viewSize.width; // in pixels (can be relative as long as we keep consistent)
            int height = viewSize.height;   // in pixels
            setupPresenterImpl(hFov, vFov, width, height, groupID);
            return 1;   // does not matter, no observers that consume output
        }).subscribe();
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG);
    }

    /**
     * Returns whether an annotation for a user is already inflated or not.
     * @param userID
     * @return
     */
    @Override
    public boolean isInflated(int userID) {
        return arAnnotations.get(userID) != null;
    }


    @Override
    public void onStop() {
        if (mCameraDevice != null) {
            System.out.println("Closing camera device");
            mCameraDevice.close();
        }
        if (cameraCaptureSession != null) {
            System.out.println("Closing capture session");
            cameraCaptureSession.close();
        }

        if (presenter != null) {
            presenter.unregisterSensors();
        }

        // TODO read user preferences. User location is still being polled on the background.

        super.onStop();
    }

    @Override
    public void onStart() { // multitask to other apps
        if (cameraCaptureSession != null) {
            System.out.println("setupCameraPreviewAndPresenter()");
            restartCameraPreview();
        }

        // no guarantee that presenter will already be instantiated
        // presenter instantiation is asynchronous, dependent on camera data being available
        if (presenter != null) {
            presenter.reregisterSensors();
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
                setupCameraPreviewAndPresenter();
            } else {
                // permission denied!
                // TODO show TextView in activity, say that permission was not granted
            }
        }

        if (requestCode == REQUEST_FOR_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupPresenter();
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
            updateAnnotationOffsets(annotation, lastHeight, lastWidth);
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

            int marginLeft = midpointWidth + annotation.offsetX - annotationWidth/2;
            int marginTop = midpointHeight + annotation.offsetY - annotationHeight/2;

//            System.out.println(marginLeft);
            if (marginLeft > 0 && marginLeft < width) {
                layout.setVisibility(View.VISIBLE);
                layoutParam.setMargins(marginLeft, marginTop, 0, 0);
            } else {
                layout.setVisibility(View.GONE);
            }
//            layoutParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);   // matches above
            layout.setLayoutParams(layoutParam);

        } else {
            // TODO throw exception?
            Log.w("setLayoutPadding", "invalid key");
        }
    }

    // camera code adapted from https://willowtreeapps.com/ideas/camera2-and-you-leveraging-android-lollipops-new-camera/

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
                    observableEmitter.onError(new Throwable("TEST"));
                }
            }
        });
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
                System.out.println(message);
                return false;
            }));
        });
    }

    /**
     * Corrects the aspect ratio of the camera preview window
     * @param cameraWidth width of the camera
     * @param cameraHeight height of the camera
     */
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

    Observable<CameraData> cameraDataObservable;

    void setupCameraPreviewAndPresenter() {
        // uses Camera2 API
        // 1. Get a Surface to draw on from the TextureView, then
        // 2. Fetch camera data, then
        // 3. Get capture device, then
        // 4. Create a new capture session

        // fetch camera data
        System.out.println("setupCameraPreviewAndPresenter() called");

        cameraDataObservable = Observable.create(emitter -> {
            getSurfaceFromTextureView(previewView)
                    .switchMap((surface) -> {   // similar to .then() chaining in JS
                        System.out.println(surface);
                        mSurface = surface;
                        return getCameraData(cameraManager);
                    })
                    .map((cameraData) -> {  // do something with the cameraData
                        System.out.println("got cameraData");
                        System.out.println(cameraData);
                        emitter.onNext(cameraData);
                        return cameraData;
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
                            Log.d("captureSessionObserver", "Camera setup complete");

                            setupPresenter();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.w("setup", "Error occurred");
                            System.out.println(e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        });

        // to get the above code going without depending on the Presenter
        cameraDataObservable.subscribe();

    }

    void restartCameraPreview() {
        // fetch camera data
        System.out.println("resetCameraPreview() called");

        getCameraDevice(cameraManager, mCameraData.cameraID)
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
                Log.d("captureSessionObserver", "Camera setup complete");


            }

            @Override
            public void onError(Throwable e) {
                Log.w("restartCameraPreview", "Error occurred");
                System.out.println(e);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    DecimalFormat df = new DecimalFormat();

    @Override
    public void updateDistanceToDestination(double distance) {
        if (distance >= 1000) {
            distance = distance/1000;
            distanceUnit.setText("km");
            df.setMaximumFractionDigits(2);
            distanceToDestination.setText(df.format(distance));
        } else {
            distanceUnit.setText("m");
            distanceToDestination.setText(((Integer) (int) distance).toString());
        }


    }

    @Override
    public void updateDestinationName(String name) {
        destinationName.setText(name);
    }

    @Override
    public void updateRelativeDestinationPosition(CompassDirection compassDirection) {
//        System.out.println(compassDirection);
        switch (compassDirection) {
            case NORTH:
                relativeCompassDirection.setText("front");
                break;
            case NORTHEAST:
                relativeCompassDirection.setText("front");
                break;
            case EAST:
                relativeCompassDirection.setText("right");
                break;
            case SOUTHEAST:
                relativeCompassDirection.setText("right");
                break;
            case SOUTH:
                relativeCompassDirection.setText("back");
                break;
            case SOUTHWEST:
                relativeCompassDirection.setText("left");
                break;
            case WEST:
                relativeCompassDirection.setText("left");
                break;
            case NORTHWEST:
                relativeCompassDirection.setText("front");
                break;
            default:
                Log.d("updatePosition", "Invalid compass direction");
        }
    }

    @Override
    public void updateHUDHeading(CompassDirection direction) {
        switch (direction) {
            case NORTH:
                heading.setText("N");
                break;
            case NORTHEAST:
                heading.setText("NE");
                break;
            case EAST:
                heading.setText("E");
                break;
            case SOUTHEAST:
                heading.setText("SE");
                break;
            case SOUTH:
                heading.setText("S");
                break;
            case SOUTHWEST:
                heading.setText("SW");
                break;
            case WEST:
                heading.setText("W");
                break;
            case NORTHWEST:
                heading.setText("NW");
                break;
            default:
                Log.d("updateHeading", "Invalid compass direction");
        }
    }





}

