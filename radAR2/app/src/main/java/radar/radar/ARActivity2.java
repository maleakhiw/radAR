package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.internal.operators.observable.ObservableInterval;
import radar.radar.Models.UserLocation;
import radar.radar.Presenters.ARPresenter;
import radar.radar.Views.ARAnnotation;
import radar.radar.Views.ARView;

public class ARActivity2 extends AppCompatActivity implements ARView {

    RelativeLayout mainRelativeLayout;
    HashMap<Integer, ARAnnotation> arAnnotations;   // userID -> ARAnnotation

    LayoutInflater inflater;

    ARPresenter presenter;

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


}
