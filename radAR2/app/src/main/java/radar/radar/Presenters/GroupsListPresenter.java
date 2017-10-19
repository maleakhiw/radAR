package radar.radar.Presenters;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.Status;
import radar.radar.Views.GroupsListView;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Services.GroupsService;



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

        view.setRefreshing(true);

        groupsService.getGroups()
                .map(getChatsResponse -> {
                    if (getChatsResponse.success) {
                        return getChatsResponse.groups;
                    } else {
                        return new ArrayList<Group>();
                    }
                })
                .subscribe(new Observer<ArrayList<Group>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<Group> groups) {
                        view.updateRecyclerViewDataSet(groups);
                        view.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                        view.setRefreshing(false);
//                        Log.w("error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void deleteGroup(int groupID) {
        groupsService.deleteGroup(groupID).subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                if (status.success) {
                    view.showToast("Group deleted");
                    loadData();
                } else {
                    view.showToast("Unexpected error");
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
                view.showToast("Unexpected error");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
