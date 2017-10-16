package radar.radar.Presenters;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Views.GroupsListView;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Services.GroupsService;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupsListPresenter {
    GroupsService groupsService;
    GroupsListView view;

    public GroupsListPresenter(GroupsService groupsService, GroupsListView view) {
        this.groupsService = groupsService;
        this.view = view;

        loadData();
    }

    public void loadData() {
        System.out.println("loadData()");

        groupsService.getGroups()
                .map(response -> {
                    System.out.println("got groupIDs");
                    ArrayList<Observable<GroupsResponse>> observablesArrayList = new ArrayList<>();

                    if (response.success != true) {
                        System.out.println("response.success is false");
//                        Log.w("loadData()", "response.success is false");
                    } else {
                        for (int groupID: response.groups) {
                            observablesArrayList.add(groupsService.getGroup(groupID));
                        }
                    }
                    return observablesArrayList;
                })
                .switchMap(observables ->
                    Observable.zip(observables, responses -> {
                        ArrayList<Group> groupsArrayListTmp = new ArrayList<>();
                        for (Object obj: responses) {
                            GroupsResponse response = (GroupsResponse) obj;
                            if (response.success && response.group.isTrackingGroup) {
                                groupsArrayListTmp.add(response.group);
                            }
                        }
                        System.out.println(groupsArrayListTmp);
                        return groupsArrayListTmp;
                    })
                )
                .subscribe(new Observer<ArrayList<Group>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Group> groups) {
                        view.updateRecyclerViewDataSet(groups);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
//                        Log.w("error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
