package radar.radar.Presenters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Responses.Status;
import radar.radar.Views.GroupsListView;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Services.GroupsService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

/**
 * This class is used to unit test the application logic of GroupList
 */
public class GroupsListPresenterTest {
    @BeforeClass
    public static void setUp() {
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

    /**
     * Unit test to make sure that data is successfully loaded
     */
    @Test
    public void loadData_Success() {
        // Mock necessary object
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        GroupsListView groupsListView = Mockito.mock(GroupsListView.class);

        // Create array list consisting groups
        ArrayList<Group> groups = new ArrayList<>();
        Group group = Mockito.mock(Group.class);
        groups.add(group);

        // Define behaviour
        GetChatsResponse getChatsResponse = Mockito.mock(GetChatsResponse.class);
        getChatsResponse.success = true;
        getChatsResponse.groups = groups;

        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse);
        Mockito.when(groupsService.getGroups()).thenReturn(observable);

        // Create object to be tested
        GroupsListPresenter presenter = new GroupsListPresenter(groupsService, groupsListView);

        // Call the presenter method
        presenter.loadData();

        // Verify
        Mockito.verify(groupsListView).updateRecyclerViewDataSet(any());
    }

    /**
     * Unit test to make sure that message is displayed when failure
     */
    @Test
    public void loadData_Failure() {
        // Mock necessary object
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        GroupsListView groupsListView = Mockito.mock(GroupsListView.class);

        // Create array list consisting groups
        ArrayList<Group> groups = new ArrayList<>();
        Group group = Mockito.mock(Group.class);
        groups.add(group);

        // Define behaviour
        GetChatsResponse getChatsResponse = Mockito.mock(GetChatsResponse.class);
        getChatsResponse.success = true;
        getChatsResponse.groups = groups;

        Observable<GetChatsResponse> observable = Observable.just(getChatsResponse)
                .map(getChatsResponse1 -> {
                    throw new SocketTimeoutException();
                });
        Mockito.when(groupsService.getGroups()).thenReturn(observable);

        // Create object to be tested
        GroupsListPresenter presenter = new GroupsListPresenter(groupsService, groupsListView);

        // Call the presenter method
        presenter.loadData();

        // Verify
        Mockito.verify(groupsListView).showToast(any());
    }

    /**
     * Unit test to make sure delete group behave appropriately
     */
    @Test
    public void deleteGroup_Success() {
        // Mock necessary object
        GroupsService service = Mockito.mock(GroupsService.class);
        GroupsListView view = Mockito.mock(GroupsListView.class);

        int groupID = 1;

        // Mock the observable
        Status status = Mockito.mock(Status.class);
        status.success = true;
        Observable<Status> observable = Observable.just(status);

        // Define behaviour
        Mockito.when(service.deleteGroup(groupID)).thenReturn(observable);

        // Create object that will be tested, spy it because need load data
        GroupsListPresenter presenter = new GroupsListPresenter(service, view);
        GroupsListPresenter spy = Mockito.spy(presenter);

        // Change behaviour of the spy
        Mockito.doNothing().when(spy).loadData();

        // Call the method to be tested
        spy.deleteGroup(groupID);

        // Verify
        Mockito.verify(view).showToast(any());
    }

    /**
     * Unit test to make sure if delete group fail will display error message
     */
    @Test
    public void deleteGroup_Failure() {
        // Mock necessary object
        GroupsService service = Mockito.mock(GroupsService.class);
        GroupsListView view = Mockito.mock(GroupsListView.class);

        int groupID = 1;

        // Mock the observable
        Status status = Mockito.mock(Status.class);
        status.success = true;
        Observable<Status> observable = Observable.just(status)
                .map(status1 -> {
                    throw new SocketTimeoutException();
                });

        // Define behaviour
        Mockito.when(service.deleteGroup(groupID)).thenReturn(observable);

        // Create object that will be tested, spy it because need load data
        GroupsListPresenter presenter = new GroupsListPresenter(service, view);
        GroupsListPresenter spy = Mockito.spy(presenter);

        // Change behaviour of the spy
        Mockito.doNothing().when(spy).loadData();

        // Call the method to be tested
        spy.deleteGroup(groupID);

        // Verify
        Mockito.verify(view).showToast(any());
    }

    /**
     * Unit test to make sure if delete group fail will display error message
     */
    @Test
    public void deleteGroup_StatusFalse() {
        // Mock necessary object
        GroupsService service = Mockito.mock(GroupsService.class);
        GroupsListView view = Mockito.mock(GroupsListView.class);

        int groupID = 1;

        // Mock the observable
        Status status = Mockito.mock(Status.class);
        status.success = false;
        Observable<Status> observable = Observable.just(status)
                .map(status1 -> {
                    throw new SocketTimeoutException();
                });

        // Define behaviour
        Mockito.when(service.deleteGroup(groupID)).thenReturn(observable);

        // Create object that will be tested, spy it because need load data
        GroupsListPresenter presenter = new GroupsListPresenter(service, view);
        GroupsListPresenter spy = Mockito.spy(presenter);

        // Change behaviour of the spy
        Mockito.doNothing().when(spy).loadData();

        // Call the method to be tested
        spy.deleteGroup(groupID);

        // Verify
        Mockito.verify(view).showToast(any());
    }
}