package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Domain.Group;

/**
 * Interface for group list to support MVP model
 */
public interface GroupsListView {
    void setRefreshing(boolean refreshing);

    void updateRecyclerViewDataSet(ArrayList<Group> groups);

    void showToast(String s);
}
