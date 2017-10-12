package radar.radar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Listeners.CameraDataListener;
import radar.radar.Models.Android.ARAnnotation;
import radar.radar.Models.Android.CameraData;
import radar.radar.Models.Android.CompassDirection;
import radar.radar.Models.Domain.UserLocation;
import radar.radar.Presenters.ARPresenter;
import radar.radar.Services.CameraObjects;
import radar.radar.Services.CameraService;
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

public class ARActivity extends AppCompatActivity implements ARView, CameraDataListener {

    RelativeLayout mainRelativeLayout;
    HashMap<Integer, ARAnnotation> arAnnotations;   // userID -> ARAnnotation

    LayoutInflater inflater;

    ARPresenter presenter;

    // camera
    TextureView previewView;
    CameraService cameraService;
    SurfaceTexture surfaceTexture;

    // HUD Views
    TextView distanceToDestination;
    TextView distanceUnit;
    TextView destinationName;
    TextView relativeCompassDirection;
    TextView heading;
    LinearLayout layoutForButtons;

    // main relative layout size
    Observable<ViewSize> mainRelativeLayoutSizeObservable;
    ViewSize lastViewSize;
    int lastHeight = -1;
    int lastWidth = -1;

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

    public void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_FOR_CAMERA);
    }

    CameraManager cameraManager;

    LocationService locationService;
    GroupsService groupsService;
    SensorManager sensorManager;

    private void setupHUDViews() {
        destinationName = findViewById(R.id.HUD_destination_name);
        distanceToDestination = findViewById(R.id.HUD_distance_to_dest);
        distanceUnit = findViewById(R.id.HUD_distance_unit);
        relativeCompassDirection = findViewById(R.id.HUD_relative_compass_direction);
        heading = findViewById(R.id.HUD_heading);
        layoutForButtons = findViewById(R.id.HUD_buttons_layout);
    }

    private void setupAnnotationView() {
        mainRelativeLayout = findViewById(R.id.ARview_layout_for_annotations);
        inflater = getLayoutInflater();
        arAnnotations = new HashMap<>();

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

//        // observable for layout size changes
//        mainRelativeLayoutSizeObservable.subscribe(viewSize -> {
//            System.out.println("viewSize got updated");
//            for (int userID: arAnnotations.keySet()) {
//                ARAnnotation annotation = arAnnotations.get(userID);
//                updateAnnotationOffsets(annotation, lastHeight, lastWidth);
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        groupID = getIntent().getIntExtra("groupID", -1);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // setup services
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        LocationApi locationApi = retrofit.create(LocationApi.class);
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationService = new LocationService(locationApi, this, fusedLocationClient);
        groupsService = new GroupsService(this, groupsApi);
        cameraService = new CameraService(cameraManager, this);

        // setup camera preview
        previewView = findViewById(R.id.AR2_texture_view);
        setupAnnotationView();
        setupCameraPreviewAndPresenter();
        setupHUDViews();



        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    void setupPresenterImpl(double hFov, double vFov, int width, int height, int groupID) {
        System.out.println("setupPresenterImpl");
        // create a new presenter
        LocationTransformations locationTransformations = new LocationTransformations(width/hFov, height/vFov);
        if (presenter == null) {
            presenter = new ARPresenter(this, locationService, groupsService, sensorManager, locationTransformations, groupID);
        }
        presenter.updateLocationTransformations(width/hFov, height/vFov);
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
        cameraService.cleanup();

        if (presenter != null) {
            presenter.onStop();
        }

        // TODO read user preferences. User location is still being polled on the background.

        super.onStop();
    }

    @Override
    public void onStart() { // multitask to other apps
        cameraService.resumeCameraPreview().subscribe();

        // no guarantee that presenter will already be instantiated
        // presenter instantiation is asynchronous - as it is instantiated only when the camera data
        // is already available
        if (presenter != null) {
            presenter.onStart();
        }

        super.onStart();
    }

    // handle the result of requesting user permissions
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
                setupCameraPreviewAndPresenter();   // TODO check
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
    public ARAnnotation getAnnotation(int userID) {
        arAnnotations.get(userID);
        return null;
    }


    /**
     * Adds an annotation onscreen based on a UserLocation object.
     * @param userLocation location details of a user
     */
    @Override
    public void inflateARAnnotation(UserLocation userLocation) {
        int userID = userLocation.getUserID();
        System.out.println("Inflating for " + ((Integer) userID).toString());

        // inflate a new layout
        // TODO change to RecyclerView
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.ar_annotation, null);

        Button button = new Button(this);
        button.setOnClickListener(view -> {
            presenter.setActiveAnnotation(userID);
        });
        layoutForButtons.addView(button);

        // add the layout to the view
        mainRelativeLayout.addView(layout);

        ARAnnotation arAnnotation = new ARAnnotation(userLocation, layout, button);

        // put it in the HashMap of annotations
        arAnnotations.put(userID, arAnnotation);

        setAnnotationOffsets(userID, 0, 0);
    }

    // add more setters for other attributes of an annotation later
    @Override
    public void setAnnotationMainText(int userID, String text) {
        if (arAnnotations != null) {
            ARAnnotation annotation = arAnnotations.get(userID);
            if (annotation != null) {
                RelativeLayout layout = annotation.getLayout();
                TextView textView = layout.findViewById(R.id.ARAnnotation_TextView);
                textView.setText(text);

                Button button = annotation.getButton();

                // TODO use new class for different behaviour
                if (userID == -1) {
                    button.setText("Destination");
                } else {
                    button.setText(text);
                }
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
            annotation.setOffsetX(offsetLeft);
            annotation.setOffsetY(offsetTop);
            updateAnnotationOffsets(annotation, lastHeight, lastWidth);
        } else {
            // TODO throw exception
            Log.w("setLayoutPadding", "invalid key");
        }
    }

    /**
     * Updates the offsets (margins) for the AR annotations.
     * @param annotation annotation to update offsets for
     * @param height height of the annotation "canvas"
     * @param width width of the annotation "canvas"
     */
    void updateAnnotationOffsets(ARAnnotation annotation, int height, int width) {
        if (annotation != null) {
            RelativeLayout layout = annotation.getLayout();
            RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            int midpointHeight = height/2;
            int midpointWidth = width/2;

            // TODO center relative to annotation size
            int annotationHeight = layout.getHeight();
            int annotationWidth = layout.getWidth();

            int marginLeft = midpointWidth + annotation.getOffsetX() - annotationWidth/2;
            int marginTop = midpointHeight + annotation.getOffsetY() - annotationHeight/2;

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



    /**
     * Corrects the aspect ratio of the camera preview window
     * @param cameraWidth width of the camera
     * @param cameraHeight height of the camera
     */
    @Override
    public void correctAspectRatio(float cameraWidth, float cameraHeight) {
        System.out.println("correctAspectRatio()");
        if (cameraWidth > cameraHeight) {
            // height should be the taller one, assuming potrait. If not, swap
            float tmp = cameraHeight;
            cameraHeight = cameraWidth;
            cameraWidth = tmp;
        }

        float aspectRatio = cameraHeight / cameraWidth;
        System.out.println(aspectRatio);

        // adjust preview surface and annotations view to match camera aspect ratio
        int previewWidth = previewView.getMeasuredWidth();
        int newWidth = previewWidth;
        int newHeight = Math.round(previewWidth * aspectRatio);

        previewView.setLayoutParams(new ConstraintLayout.LayoutParams(newWidth, newHeight));
        mainRelativeLayout.setLayoutParams(new ConstraintLayout.LayoutParams(newWidth, newHeight));
    }


    void setupCameraPreviewAndPresenter() {

        System.out.println("setupCameraPreviewAndPresenter()");
        Observable.combineLatest(cameraService.getCameraObjects(previewView), mainRelativeLayoutSizeObservable,
                CameraObjectsAndMainRelativeLayoutSize::new).subscribe(new Observer<CameraObjectsAndMainRelativeLayoutSize>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(CameraObjectsAndMainRelativeLayoutSize cameraObjectsAndMainRelativeLayoutSize) {
                System.out.println(cameraObjectsAndMainRelativeLayoutSize);
                // got camera data, setup presenter
                CameraObjects cameraObjects = cameraObjectsAndMainRelativeLayoutSize.cameraObjects;
                ViewSize viewSize = cameraObjectsAndMainRelativeLayoutSize.relativeLayoutSize;
                CameraData cameraData = cameraObjects.getCameraData();

                System.out.println(viewSize);
                double hFov = cameraData.getHorizontalFov();
                double vFov = cameraData.getVerticalFov();
                int width = viewSize.width;
                int height = viewSize.height;
                setupPresenterImpl(hFov, vFov, width, height, groupID);

                // NOTE correctAspectRatio() won't work here because you cannot change the size of
                // the view once the camera is already using it.
                // Thus we define an interface CameraDataListener -> method gets called
                // when cameraData is ready; but the View is not used yet for the camera preview.
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("REQUEST_CAMERA_PERMISSIONS")) {
                    requestCameraPermissions();
                }

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
        relativeCompassDirection.setText(LocationTransformations.getRelativeDestinationString(compassDirection));
    }

    @Override
    public int getAnnotationHeight(int userID) {
        ARAnnotation annotation = arAnnotations.get(userID);
        if (annotation != null) {
            return annotation.getLayout().getMeasuredHeight();
        } else return -1;
    }

    @Override
    public int getAnnotationWidth(int userID) {
        ARAnnotation annotation = arAnnotations.get(userID);
        if (annotation != null) {
            return annotation.getLayout().getMeasuredWidth();
        } else return -1;
    }

    @Override
    public void updateHUDHeading(CompassDirection direction) {
        // TODO move to LocationTransformations
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


    private class CameraObjectsAndMainRelativeLayoutSize {
        CameraObjects cameraObjects;
        ViewSize relativeLayoutSize;

        public CameraObjectsAndMainRelativeLayoutSize(CameraObjects cameraObjects, ViewSize relativeLayoutSize) {
            this.cameraObjects = cameraObjects;
            this.relativeLayoutSize = relativeLayoutSize;
        }
    }
}

