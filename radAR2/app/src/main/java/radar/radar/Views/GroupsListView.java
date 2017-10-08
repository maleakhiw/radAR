package radar.radar.Views;

import java.util.ArrayList;

import radar.radar.Models.Domain.Group;

/**
 * Created by kenneth on 3/10/17.
 */

public interface GroupsListView {
    void updateRecyclerViewDataSet(ArrayList<Group> groups);
}
