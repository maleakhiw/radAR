package radar.radar.Views;

import radar.radar.Models.Responses.UsersSearchResult;

/**
 * Interface for SearchUserFragment to support MVP model
 */
public interface SearchUserView {
    void showToast(String message);

    void bindAdapterToRecyclerView(UsersSearchResult usersSearchResult);
}
