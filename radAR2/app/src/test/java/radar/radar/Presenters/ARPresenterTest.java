package radar.radar.Presenters;

import android.location.Location;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Android.CompassDirection;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Responses.GroupLocationsInfo;
import radar.radar.Models.Responses.UpdateLocationResponse;
import radar.radar.Services.GroupsService;
import radar.radar.Services.LocationService;
import radar.radar.Services.LocationTransformations;
import radar.radar.Services.SensorService;
import radar.radar.Views.ARView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;

/**
 * Created by kenneth on 21/10/17.
 */

public class ARPresenterTest {

    @BeforeClass
    public static void setupClass() {
        // set all schedulers to trampoline scheduler - to run on the "main thread"
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                __ -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());
    }


    @Test
    public void setActiveAnnotation() throws Exception {
        ARView arView = Mockito.mock(ARView.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        SensorService sensorService = Mockito.mock(SensorService.class);
        LocationTransformations locationTransformations = new LocationTransformations(79, 79);
        int groupID = 1;

        ARPresenter presenter = new ARPresenter(arView, locationService, groupsService, sensorService, locationTransformations, groupID);
        presenter.setActiveAnnotation(1);
        assertEquals(presenter.getActiveAnnotationUserID(), 1);
    }

    @Test
    public void updateLocationTransformations() throws Exception {
        ARView arView = Mockito.mock(ARView.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        SensorService sensorService = Mockito.mock(SensorService.class);
        LocationTransformations locationTransformations = Mockito.mock(LocationTransformations.class);
        int groupID = 1;

        Location locationMock = Mockito.mock(Location.class);

        Observable<Double> azimuthObservable = Observable.just(15d);
        Observable<Double> pitchObservable = Observable.just(15d);
        Observable<Location> locationObservable = Observable.just(locationMock);

        Mockito.when(locationService.updateLocation(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(Observable.just(new UpdateLocationResponse()));
        Mockito.when(sensorService.getAzimuthUpdates()).thenReturn(azimuthObservable);
        Mockito.when(sensorService.getPitchUpdates()).thenReturn(pitchObservable);
        Mockito.when(locationService.getLocationUpdates(anyInt(), anyInt(), anyInt())).thenReturn(locationObservable);
        Mockito.when(locationService.getGroupLocationInfo(anyInt(), anyInt())).thenReturn(Observable.just(
                new GroupLocationsInfo(new MeetingPoint(23, 23, "MeetingPoint", ""),
                                        new ArrayList<>(),
                                        new HashMap<>())));

        ARPresenter presenter = new ARPresenter(arView, locationService, groupsService, sensorService, locationTransformations, groupID);
        presenter = Mockito.spy(presenter);

        presenter.updateLocationTransformations(80, 80);
        Mockito.verify(locationTransformations).sethPixelsPerDegree(80);
        Mockito.verify(locationTransformations).setvPixelsPerDegree(80);
        Mockito.verify(presenter).updateLocationTransformations();
    }


    @Test
    public void renderHUD() throws Exception {
        ARView arView = Mockito.mock(ARView.class);
        LocationService locationService = Mockito.mock(LocationService.class);
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        SensorService sensorService = Mockito.mock(SensorService.class);
        LocationTransformations locationTransformations = new LocationTransformations(79, 79);
        int groupID = 1;

        ARPresenter presenter = new ARPresenter(arView, locationService, groupsService, sensorService, locationTransformations, groupID);

        presenter.renderHUD(new MeetingPoint(79.0, 79.0, "Test",""), 78, 78, 45);
        Mockito.verify(arView).updateDestinationName("Test");   // DESTINATION_ID by default
        Mockito.verify(arView).updateDistanceToDestination(anyDouble());
        Mockito.verify(arView).updateHUDHeading(any(CompassDirection.class));

    }

}