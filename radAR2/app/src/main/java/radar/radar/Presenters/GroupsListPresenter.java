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

/**
 * Application logic/ presenter for GroupList
 */
public class GroupsListPresenter {
    GroupsService groupsService;
    GroupsListView view;

    /**
     * Constructor for GroupsListPresenter
     * @param groupsService instance of the group service
     * @param view instance of the GroupListView
     */
    public GroupsListPresenter(GroupsService groupsService, GroupsListView view) {
        this.groupsService = groupsService;
        this.view = view;

        // This was removed to make the unit test easier
        // loadData();
    }

    /**
     * Used to load group for a particular user
     */
    public void loadData() {
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
                        view.showToast("Internal Error. Failure to load groups.");
                        view.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * Used to delete a group when user click delete
     * @param groupID id of the group to be deleted
     */
    public void deleteGroup(int groupID) {
        groupsService.deleteGroup(groupID).subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                if (status.success) {
                    view.showToast("Group deleted successfully.");
                    loadData();
                } else {
                    view.showToast("Failure to delete group");
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
                view.showToast("Internal error. Failure to delete group.");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
