package radar.radar.Presenters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.GroupsListView;
import radar.radar.Models.Group;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Services.GroupsService;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;

/**
 * Created by kenneth on 3/10/17.
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

    @Test
    public void loadDataError() {
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        GroupsListView groupsListView = Mockito.mock(GroupsListView.class);

        Mockito.when(groupsService.getGroups()).thenReturn(Observable.error(new Throwable("error")));

        GroupsListPresenter groupsListPresenter = new GroupsListPresenter(groupsService, groupsListView);
        // method called in presenter, verify
        Mockito.verify(groupsListView, times(0)).updateRecyclerViewDataSet(any());

    }

    @Test
    public void loadData() {
        GroupsService groupsService = Mockito.mock(GroupsService.class);
        GroupsListView groupsListView = Mockito.mock(GroupsListView.class);

        ArrayList<Integer> groups = new ArrayList<>();
        groups.add(1);
        groups.add(2);
        Mockito.when(groupsService.getGroups()).thenReturn(Observable.just(new GetChatsResponse(groups, true)));
        Mockito.when(groupsService.getGroup(1)).thenReturn(Observable.just(new GroupsResponse(new Group("test", true))));
        Mockito.when(groupsService.getGroup(2)).thenReturn(Observable.just(new GroupsResponse(new Group("test2", true))));

        GroupsListPresenter groupsListPresenter = new GroupsListPresenter(groupsService, groupsListView);
        // method called in presenter, verify
        Mockito.verify(groupsListView, times(1)).updateRecyclerViewDataSet(any());

    }


}