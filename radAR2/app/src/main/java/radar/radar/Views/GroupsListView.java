package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Domain.Group;

public interface GroupsListView {
    void setRefreshing(boolean refreshing);

    void updateRecyclerViewDataSet(ArrayList<Group> groups);

    void showToast(String s);
}
