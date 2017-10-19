package radar.radar.Presenters;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Listeners.LocationCallbackProvider;
import radar.radar.Listeners.LocationUpdateListener;
import radar.radar.Services.LocationService;
import radar.radar.Views.HomeScreenView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Created by rtanudjaja on 20/10/17.
 */
public class HomeScreenPresenterTest {

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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}